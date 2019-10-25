/**
 * 
 */
package net.safester.application.compose.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchProviderException;
import java.util.List;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;

import com.safelogic.pgp.api.PgpActionsOne;

/**
 * Allows to encrypt a text with a list of PGPPublicKey.
 * @author Nicolas de Pomereu
 *
 */
public class PgpTextEncryptor {

    /** The list of PGPPublicKey to use for text encryption. */
    private List<PGPPublicKey> pGPPublicKeyList = null;

    /**
     * Constructor.
     * @param pGPPublicKeyList The list of PGPPublicKey to use for text encryption.
     */
    public PgpTextEncryptor(List<PGPPublicKey> pGPPublicKeyList) {
	this.pGPPublicKeyList = pGPPublicKeyList;

    }

    /**
     * Encrypts a text for a list of PGPPublicKey.
     * @param text	the text to encrypt.
     * @return	the encrypted text.
     * @throws NoSuchProviderException
     * @throws UnsupportedEncodingException
     * @throws IllegalArgumentException
     * @throws PGPException
     * @throws IOException
     */
    public String encrypt(String text) throws NoSuchProviderException, UnsupportedEncodingException,
	    IllegalArgumentException, PGPException, IOException {
	
	PgpActionsOne pgpActions = new PgpActionsOne();
	pgpActions.setIntegrityCheck(true);

	String subject = pgpActions.encryptStringPgp(text, pGPPublicKeyList);
	return subject;
    }

}
