/**
 * 
 */
package net.safester.application.compose.api;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bouncycastle.openpgp.PGPPublicKey;

import com.safelogic.pgp.api.KeySignatureHandlerOne;
import com.safelogic.pgp.api.PgeepPublicKey;

import net.safester.application.util.crypto.CryptoUtil;
import net.safester.clientserver.PgpKeyPairLocal;
import net.safester.clientserver.PgpPublicKeyListExtractorClient;
import net.safester.clientserver.PgpPublicKeyLocal;
import net.safester.clientserver.ServerParms;
import net.safester.clientserver.holder.PgpKeyPairHolder;

/**
 * Builds all the PGPPublicKeys for the message to encrypt. 
 * (Gets the PGPPublicKeys from the server.).
 * 
 * If a key signature is not correctly verified we will add it.
 * 
 * @author Nicolas de Pomereu
 *
 */
public class PGPPublicKeysBuilder {
    
    private Set<String> keyIds = null;
    private Connection connection = null;
    
    /** The key userIs that could not be correctly verified. */
    private List<String> unverifiedKeyIds = new ArrayList<>();
    
 
    /**
     * Constructor.
     * @param keyIds
     * @param connection
     */
    public PGPPublicKeysBuilder(Set<String> keyIds, Connection connection) {
	
	if (keyIds == null) {
	    throw new NullPointerException("keyIds is null!");
	}
	
	if (connection == null) {
	    throw new NullPointerException("connection is null!");
	}
	
	this.keyIds = keyIds;
	this.connection = connection;
    }

    /**
     * Build the list of PGPPublicKeys.
     */
    public List<PGPPublicKey> buildPGPPublicKeys()
            throws Exception {
    	//System.out.println("buildPublicKeyList with external set to " + addMasterKey);
		
	PgpPublicKeyListExtractorClient pgpPublicKeyListExtractor = new PgpPublicKeyListExtractorClient(connection, this.keyIds);
        List<PgpPublicKeyLocal> pgpPublicKeyLocalList = pgpPublicKeyListExtractor.getList();
        boolean externalRecipients = pgpPublicKeyListExtractor.containsUnknownKeys();
        
        // Put in memory cache the master key
        String masterUserId = ServerParms.getMasterKeyId();
        PgpKeyPairHolder pgpKeyPairHolder = new PgpKeyPairHolder(connection, ServerParms.getMasterKeyUserNumber());
        PgpKeyPairLocal pgpMasterKeyPairLocal = pgpKeyPairHolder.get();
        String masterPublicKeyBloc = pgpMasterKeyPairLocal.getPublicKeyPgpBlock();
        
        KeySignatureHandlerOne keySignatureHandlerOne = new KeySignatureHandlerOne();

        List<PGPPublicKey> pGPPublicKeyList = new ArrayList<PGPPublicKey>();
        for (PgpPublicKeyLocal pgpPublicKeyLocal : pgpPublicKeyLocalList) {

            String pgpPublicKeyBloc = pgpPublicKeyLocal.getPublicKeyPgpBlock();

            String userId = CryptoUtil.extractUserIdFromKeyBloc(pgpPublicKeyBloc);

            /*
            if (VERIFY_PUBLIC_KEY_SGINATURE) {
                if (!keySignatureHandlerOne.verifyKeySignature(masterUserId, userId, masterPublicKeyBloc, pgpPublicKeyBloc)) {
                    String msg = messages.getMessage("signature_of_key_invalid");
                    msg = MessageFormat.format(msg, pgpMasterKeyPairLocal.getPgpKeyId());
                    JOptionPane.showMessageDialog(caller, msg);
                    return false;
                }
            }
            */
            
            if (!keySignatureHandlerOne.verifyKeySignature(masterUserId, userId, masterPublicKeyBloc, pgpPublicKeyBloc)) {
        	unverifiedKeyIds.add(userId);
            }
           
            PgeepPublicKey pgeepPublicKey = pgpPublicKeyLocal.getPgeepPublicKey();
            pGPPublicKeyList.add(pgeepPublicKey.getKey());
        }

        // If we hare external recipients we must encrypt to server master key.
        if (externalRecipients) {
            PgeepPublicKey pgeepPublicKey = pgpMasterKeyPairLocal.getPgeepPublicKey();
            pGPPublicKeyList.add(pgeepPublicKey.getKey());
        }
        
        return pGPPublicKeyList;

    }

    /**
     * Says if all key signatures verification is OK.
     * @return true if all key signatures are OK.
     */
    public boolean allKeysverified() {

	if (unverifiedKeyIds != null && !unverifiedKeyIds.isEmpty()) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Returns the list of key Ids that could not be verified (verification failed). 
     * @return the list of key Ids that could not be verified (verification failed). 
     */
    public List<String> getUnverifiedKeyIds() {
        return unverifiedKeyIds;
    }
    
    
 
}
