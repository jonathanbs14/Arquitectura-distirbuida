package com.prueba.graftsql.credito.auth.adapter;

import com.prueba.graftsql.credito.auth.application.ports.MFAPort;
import com.prueba.graftsql.credito.auth.domain.MFASetup;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.util.Utils;
import org.springframework.stereotype.Component;

@Component
public class GoogleAuthenticatorAdapter implements MFAPort {

    private static final CodeVerifier CODE_VERIFIER =
            new DefaultCodeVerifier(
                    new DefaultCodeGenerator(),
                    new SystemTimeProvider()
            );

    @Override
    public MFASetup generateMFASecret(String userId) {
        String secret = new DefaultSecretGenerator().generate();
        String qrCodeUrl = generateQRCodeUrl(secret, userId);

        return new MFASetup(secret, qrCodeUrl, userId);
    }

    @Override
    public boolean verifyMFACode(String secret, String code) {
        if (secret == null || secret.isBlank()) {
            return false;
        }

        if (code == null || !code.matches("\\d{6}")) {
            return false;
        }

        return CODE_VERIFIER.isValidCode(secret, code);
    }

    @Override
    public String generateQRCodeUrl(String secret, String email) {
        try {
            QrData data = new QrData.Builder()
                    .label(email)
                    .secret(secret)
                    .issuer("AuthService")
                    .digits(6)
                    .period(30)
                    .build();

            QrGenerator generator = new ZxingPngQrGenerator();
            byte[] imageData = generator.generate(data);

            return Utils.getDataUriForImage(
                    imageData,
                    generator.getImageMimeType()
            );
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Error al generar el código QR para MFA",
                    e
            );
        }
    }
}
