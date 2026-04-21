package com.safelogic.pgp.api;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;

import org.bouncycastle.bcpg.PublicKeyAlgorithmTags;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;

/**
 * PGP Private Key Holder
 *
 * Necessary because some provider implementations do not define their PGP Private Key as
 * Java PrivateKey
 *
 * @author Nicolas de Pomereu
 */
public class PgeepPrivateKey implements PrivateKey {

    private final PGPSecretKey m_pgpKey;
    private final char[] pass;

    /**
     * Constructor
     */
    public PgeepPrivateKey(PGPSecretKey pgpKey, char[] passphrase) {
        if (pgpKey == null) {
            throw new IllegalArgumentException("pgpKey cannot be null!");
        }
        if (passphrase == null) {
            throw new IllegalArgumentException("passphrase cannot be null!");
        }
        this.m_pgpKey = pgpKey;
        this.pass = passphrase;
    }

    /**
     * @return Returns the pgpKey.
     */
    public PGPSecretKey getPGPSecretKey() {
        return m_pgpKey;
    }

    @Override
    public String getAlgorithm() {
        int alg = m_pgpKey.getPublicKey().getAlgorithm();

        switch (alg) {
            case PublicKeyAlgorithmTags.RSA_GENERAL:
            case PublicKeyAlgorithmTags.RSA_ENCRYPT:
            case PublicKeyAlgorithmTags.RSA_SIGN:
                return "RSA";

            case PublicKeyAlgorithmTags.DSA:
                return "DSA";

            case PublicKeyAlgorithmTags.ECDSA:
                return "ECDSA";

            case PublicKeyAlgorithmTags.ECDH:
                return "ECDH";

            case PublicKeyAlgorithmTags.ELGAMAL_ENCRYPT:
            case PublicKeyAlgorithmTags.ELGAMAL_GENERAL:
                return "ELGAMAL";

            default:
                return "PGP";
        }
    }

    @Override
    public byte[] getEncoded() {
        try {
            return m_pgpKey.getEncoded();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getFormat() {
        return null;
    }

    private PGPPrivateKey extractPgpPrivateKey() throws PGPException {
        PBESecretKeyDecryptor decryptor = new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider())
                .build(pass);

        return m_pgpKey.extractPrivateKey(decryptor);
    }

    // Rule 8: Make your classes noncloneable
    @Override
    public final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    // Rule 9: Make your classes nonserializeable
    private void writeObject(ObjectOutputStream out) throws IOException {
        throw new IOException("Object cannot be serialized");
    }

    // Rule 10: Make your classes nondeserializeable
    private void readObject(ObjectInputStream in) throws IOException {
        throw new IOException("Class cannot be deserialized");
    }
}
