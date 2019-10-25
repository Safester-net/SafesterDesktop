/**
 * 
 */
package net.safester.application;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Allows to store lexicon per user
 * @author Nicolas de Pomereu
 *
 */
public class LexiconStore {

    /** Contains all exlicons for all Key Ids */
    private static Map<String, Set<String>> lexiconPerKeyId = new HashMap<>();
    
    /** The username (email) of the user */
    private String keyId = null;
    
       
    /**
     * Constructor.
     * @param keyId
     */
    public LexiconStore(String keyId) {

	if (keyId == null) {
	    throw new NullPointerException(" keyId can not be null!");
	}
	
	this.keyId = keyId;
	
    }

    /**
     * Returns all lexicons for all key ids. This allows to get all addresses email of all login usernames of the current user.
     * @return all lexicons for all key ids
     */
    public static Set<String> getAllLexicons() {
	
	Set<String> allLexicons = new TreeSet<>();
	
	for (Map.Entry<String, Set<String>> entry : lexiconPerKeyId.entrySet()) {
	   Set<String> lexicon = lexiconPerKeyId.get(entry.getKey());
	   allLexicons.addAll(lexicon);
	}
	
	return allLexicons;
	   
    }

    
    /**
     * Says if exists a Lexicon for the Key Id passed on Constructor.
     * @return true if a lexicon already exists for the Key Id, else false.
     */
    public boolean existsLexicon() {
	return lexiconPerKeyId.containsKey(keyId);
    }
    
    /**
     * Add the lexicon to Map of existing of lexicons per Key Id..
     * @param lexicon the lexicon for a Key Id
     */
    public void addLexicon(Set<String> lexicon) {
	
	if (lexicon == null) {
	    throw new NullPointerException("lexicon can not be null!");
	}
	
	if (lexiconPerKeyId.containsKey(keyId)) {
	    Set<String> existing = lexiconPerKeyId.get(keyId);
	    existing.addAll(lexicon);
	    lexiconPerKeyId.put(keyId, lexicon);
	}
	else {
	    lexiconPerKeyId.put(keyId, lexicon);
	}
    }
 
}
