import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collections;

import com.safelogic.pgp.api.KeyHandlerOne;
import com.safelogic.pgp.api.PgeepPrivateKey;
import com.safelogic.pgp.api.PgeepPublicKey;
import com.safelogic.pgp.api.PgpActionsOne;
import com.safelogic.pgp.api.util.parms.PgpCryptoParms;
import com.safelogic.pgp.util.UrlUtil;

/**
 * Generates a test keyring and validates an encrypt/decrypt round-trip.
 */
public final class BcLightRoundTripTest {

    private static final String USER_ID = "Codex Light <codex-light@safester.test>";
    private static final String KEY_ID = "codex-light@safester.test";
    private static final char[] PASSPHRASE = "codex-passphrase-2026".toCharArray();
    private static final char[] NEW_PASSPHRASE = "codex-new-passphrase-2026".toCharArray();

    private BcLightRoundTripTest() {
    }

    public static void main(String[] args) throws Exception {
        Path codexDir = Paths.get("codex").toAbsolutePath();
        Path keysDir = codexDir.resolve("keys");
        Files.createDirectories(keysDir);

        Path publicRing = keysDir.resolve(UrlUtil.PUBRING_PKR);
        Path secretRing = keysDir.resolve(UrlUtil.SECRING_SKR);

        UrlUtil.setKeyDirectory(keysDir.toString());

        KeyHandlerOne keyHandler = new KeyHandlerOne();
        byte[] seed = "codex-bc-light-round-trip-seed".getBytes(StandardCharsets.US_ASCII);

        try (OutputStream secretOut = Files.newOutputStream(secretRing);
             OutputStream publicOut = Files.newOutputStream(publicRing)) {
            keyHandler.generateKeyPair(
                    USER_ID,
                    PASSPHRASE,
                    PgpCryptoParms.RSA,
                    2048,
                    PgpCryptoParms.AES,
                    256,
                    seed,
                    null,
                    secretOut,
                    publicOut);
        }

        PublicKey publicKey = keyHandler.getPgpPublicKeyForEncryption(KEY_ID);
        if (!(publicKey instanceof PgeepPublicKey)) {
            throw new IllegalStateException("Expected PgeepPublicKey, got " + publicKey);
        }

        PrivateKey privateKey = keyHandler.getPgpPrivateKey(KEY_ID, null, PASSPHRASE);
        if (!(privateKey instanceof PgeepPrivateKey)) {
            throw new IllegalStateException("Expected PgeepPrivateKey, got " + privateKey);
        }

        if (!keyHandler.isPassphraseValid(KEY_ID, PASSPHRASE)) {
            throw new IllegalStateException("Generated passphrase is not valid.");
        }

        String plainText = "Safester Codex BC light round trip";
        PgpActionsOne actions = new PgpActionsOne();
        String encrypted = actions.encryptPgp(plainText, Collections.singletonList(KEY_ID));
        String decrypted = actions.decryptPgp(encrypted, KEY_ID, PASSPHRASE);

        if (!plainText.equals(decrypted)) {
            throw new IllegalStateException("Round-trip mismatch: " + decrypted);
        }

        keyHandler.changePassphrase(KEY_ID, PASSPHRASE, NEW_PASSPHRASE);

        if (keyHandler.isPassphraseValid(KEY_ID, PASSPHRASE)) {
            throw new IllegalStateException("Old passphrase still decrypts after changePassphrase().");
        }

        if (!keyHandler.isPassphraseValid(KEY_ID, NEW_PASSPHRASE)) {
            throw new IllegalStateException("New passphrase is not valid after changePassphrase().");
        }

        String decryptedAfterPassphraseChange = actions.decryptPgp(encrypted, KEY_ID, NEW_PASSPHRASE);
        if (!plainText.equals(decryptedAfterPassphraseChange)) {
            throw new IllegalStateException("Round-trip mismatch after passphrase change: "
                    + decryptedAfterPassphraseChange);
        }

        Path plainFile = codexDir.resolve("roundtrip-file-plain.txt");
        Path encryptedFile = codexDir.resolve("roundtrip-file-encrypted.pgp");
        Path decryptedFile = codexDir.resolve("roundtrip-file-decrypted.txt");
        Files.writeString(plainFile, plainText, StandardCharsets.UTF_8);
        actions.encryptPgp(plainFile.toFile(), encryptedFile.toFile(), Collections.singletonList(KEY_ID));
        actions.decryptPgp(encryptedFile.toFile(), decryptedFile.toFile(), KEY_ID, NEW_PASSPHRASE);

        String decryptedFileText = Files.readString(decryptedFile, StandardCharsets.UTF_8);
        if (!plainText.equals(decryptedFileText)) {
            throw new IllegalStateException("File round-trip mismatch: " + decryptedFileText);
        }

        Files.writeString(codexDir.resolve("roundtrip-plain.txt"), plainText, StandardCharsets.UTF_8);
        Files.writeString(codexDir.resolve("roundtrip-encrypted.asc"), encrypted, StandardCharsets.UTF_8);
        Files.writeString(codexDir.resolve("roundtrip-decrypted.txt"), decrypted, StandardCharsets.UTF_8);
        Files.writeString(codexDir.resolve("roundtrip-decrypted-after-passphrase-change.txt"),
                decryptedAfterPassphraseChange,
                StandardCharsets.UTF_8);

        System.out.println("OK BC light key generation/encrypt/decrypt");
        System.out.println("Keys directory: " + keysDir);
        System.out.println("Public key algorithm: " + publicKey.getAlgorithm());
        System.out.println("Private key algorithm: " + privateKey.getAlgorithm());
    }
}
