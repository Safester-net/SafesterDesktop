import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import com.safelogic.pgp.api.KeyHandlerOne;
import com.safelogic.pgp.api.PgpActionsOne;
import com.safelogic.pgp.api.util.parms.PgpCryptoParms;
import com.safelogic.pgp.util.UrlUtil;

/**
 * Validates passphrase change and restore on a generated BC light keyring.
 */
public final class BcLightPassphraseRoundTripTest {

    private static final String USER_ID = "Codex Light <codex-light@safester.test>";
    private static final String KEY_ID = "codex-light@safester.test";
    private static final char[] FINAL_PASSPHRASE = "codex-new-passphrase-2026".toCharArray();
    private static final char[] TEMP_PASSPHRASE = "codex-temp-passphrase-2026".toCharArray();

    private BcLightPassphraseRoundTripTest() {
    }

    public static void main(String[] args) throws Exception {
        Path codexDir = Paths.get("codex").toAbsolutePath();
        Path keysDir = codexDir.resolve("keys");
        Files.createDirectories(keysDir);

        UrlUtil.setKeyDirectory(keysDir.toString());

        KeyHandlerOne keyHandler = new KeyHandlerOne();
        byte[] seed = "codex-bc-light-passphrase-round-trip-seed".getBytes(StandardCharsets.US_ASCII);

        try (OutputStream secretOut = Files.newOutputStream(keysDir.resolve(UrlUtil.SECRING_SKR));
             OutputStream publicOut = Files.newOutputStream(keysDir.resolve(UrlUtil.PUBRING_PKR))) {
            keyHandler.generateKeyPair(
                    USER_ID,
                    FINAL_PASSPHRASE,
                    PgpCryptoParms.RSA,
                    2048,
                    PgpCryptoParms.AES,
                    256,
                    seed,
                    null,
                    secretOut,
                    publicOut);
        }

        String plainText = "Safester Codex BC light passphrase round trip";
        PgpActionsOne actions = new PgpActionsOne();
        String encrypted = actions.encryptPgp(plainText, Collections.singletonList(KEY_ID));
        String decryptedBeforeChange = actions.decryptPgp(encrypted, KEY_ID, FINAL_PASSPHRASE);
        assertEquals("Initial decrypt", plainText, decryptedBeforeChange);

        boolean changedToTemp = false;
        try {
            keyHandler.changePassphrase(KEY_ID, FINAL_PASSPHRASE, TEMP_PASSPHRASE);
            changedToTemp = true;

            assertInvalid(keyHandler, FINAL_PASSPHRASE, "Final passphrase should be invalid while temp is active.");
            assertValid(keyHandler, TEMP_PASSPHRASE, "Temp passphrase should be valid after first change.");

            String decryptedWithTemp = actions.decryptPgp(encrypted, KEY_ID, TEMP_PASSPHRASE);
            assertEquals("Decrypt with temp passphrase", plainText, decryptedWithTemp);

            keyHandler.changePassphrase(KEY_ID, TEMP_PASSPHRASE, FINAL_PASSPHRASE);
            changedToTemp = false;

            assertInvalid(keyHandler, TEMP_PASSPHRASE, "Temp passphrase should be invalid after restore.");
            assertValid(keyHandler, FINAL_PASSPHRASE, "Final passphrase should be valid after restore.");

            String decryptedAfterRestore = actions.decryptPgp(encrypted, KEY_ID, FINAL_PASSPHRASE);
            assertEquals("Decrypt after final passphrase restore", plainText, decryptedAfterRestore);

            Files.writeString(codexDir.resolve("passphrase-roundtrip-plain.txt"), plainText, StandardCharsets.UTF_8);
            Files.writeString(codexDir.resolve("passphrase-roundtrip-encrypted.asc"), encrypted, StandardCharsets.UTF_8);
            Files.writeString(codexDir.resolve("passphrase-roundtrip-decrypted-before-change.txt"),
                    decryptedBeforeChange,
                    StandardCharsets.UTF_8);
            Files.writeString(codexDir.resolve("passphrase-roundtrip-decrypted-after-temp-change.txt"),
                    decryptedWithTemp,
                    StandardCharsets.UTF_8);
            Files.writeString(codexDir.resolve("passphrase-roundtrip-decrypted-after-restore.txt"),
                    decryptedAfterRestore,
                    StandardCharsets.UTF_8);
        } finally {
            if (changedToTemp && keyHandler.isPassphraseValid(KEY_ID, TEMP_PASSPHRASE)) {
                keyHandler.changePassphrase(KEY_ID, TEMP_PASSPHRASE, FINAL_PASSPHRASE);
            }
        }

        assertValid(keyHandler, FINAL_PASSPHRASE, "Final passphrase must be restored at test end.");
        System.out.println("OK BC light passphrase roundtrip");
        System.out.println("Keys directory: " + keysDir);
        System.out.println("Final passphrase: codex-new-passphrase-2026");
    }

    private static void assertValid(KeyHandlerOne keyHandler, char[] passphrase, String message) throws Exception {
        if (!keyHandler.isPassphraseValid(KEY_ID, passphrase)) {
            throw new IllegalStateException(message);
        }
    }

    private static void assertInvalid(KeyHandlerOne keyHandler, char[] passphrase, String message) throws Exception {
        if (keyHandler.isPassphraseValid(KEY_ID, passphrase)) {
            throw new IllegalStateException(message);
        }
    }

    private static void assertEquals(String label, String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new IllegalStateException(label + " mismatch: " + actual);
        }
    }
}
