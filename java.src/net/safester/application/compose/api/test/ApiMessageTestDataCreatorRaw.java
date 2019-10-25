/**
 * 
 */
package net.safester.application.compose.api.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.safester.application.compose.api.ApiRecipientsBuilder;
import net.safester.application.http.dto.IncomingMessageDTO;
import net.safester.application.http.dto.IncomingRecipientDTO;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ApiMessageTestDataCreatorRaw {

    private String token = "5ec030e8c5161f6175be";
	
    /**
     * 
     */
    public ApiMessageTestDataCreatorRaw() {

    }

    public String getToken() {
	return token;
    }

    public IncomingMessageDTO incomingMssageDTOBuild(List<File> enuncryptedFiles)
	    throws FileNotFoundException {
	String senderEmailAddr = "brunopaul88@outlook.com"; // passphrase is: 82223bafcd814f5d5600

	List<String> emailAddresses = new ArrayList<>();
	List<String> names = new ArrayList<>();
	List<Integer> recipientTypes = new ArrayList<>();

	emailAddresses.add("brunopaul88@outlook.com");
	names.add("Bruno Paul");
	recipientTypes.add(1);

	List<IncomingRecipientDTO> recipients = ApiRecipientsBuilder.getRecipients(emailAddresses, names, recipientTypes);

//	String subject = "-----BEGIN PGP MESSAGE-----\r\nVersion: BCPG v1.42\r\n\r\nhQIOA1JIrh8cPetQEAgA5C67dLHUGG32nU9wcbKq/Mf5FBiejyRHKVo45h5Fovon\r\nRNTrAnffZ9hwxHSx2wwvt3IcmAgXRDb+hDGEFJmdd68o0vG1ThpP3Pqp52bM556n\r\nBA6irI+rWLnMBF1uqKVMsMI+PxVeFuKI/QhiIvvEIZCUPcvvgTP5Kez8ITcQ2iSE\r\n2Jw/FzNyGmJLZN23EZBg0aaKKYwl83p2uoJ5RMISYTI2GTTEPF/iDzEpv8CRAtBe\r\n5kg6w1lKjtXzEO2/VeGymCEV4gyMGohAFz88d32YngabgIcszE9bAA1V1tkz5QBV\r\n4zOSqVXDXLlYNfLIWfL0AwHdw2d59kckuNBNZtp/EQf/Qa2Tz2FGv8u7Qja5cUuq\r\nVYpxEWyhsc9iFL5YpDQhbRubF2ScWi7UwZglGNFm9VcAsw2w/kTy9STMM9qZhx2L\r\nYNNQH4OVknXfB+auXtb3+zkKknqFy4j/aPRykGmQmN1oX9G3Qp4rk/S6jejk7rUv\r\nUHc7pMoviwIwpe9ubRxFPkUOqi6abuPshrxU+vlFO7bPtubWkjb8O0tAmbUvjU4r\r\noj1zzwHETLSKJJA1RQCeggvpVA+sd7H1tGjhWAsqYDj//fEi5x2XnFOf0Q0zQH5L\r\nfDiCXHNJxn5YR8rY5PZDObSPnNB9ex1MRLBO+3QNShZK9Cl67gjiWraTAdHadk7j\r\nQtI7AYLwrXzUqSiRW1dTwao76ZkL9yVBXiOqUnoVBRDUN+d2iPu82nOMNTqR4XIH\r\nWdA2KcjrsPf7NewY1E8\u003d\r\n\u003dvqRT\r\n-----END PGP MESSAGE-----\r\n";
//	String body = "-----BEGIN PGP MESSAGE-----\r\nVersion: BCPG v1.42\r\n\r\nhQIOA1JIrh8cPetQEAgAqY+ktCkHMPGa/4ha0uAsdps2uyV/rDPTulpvHdf5+RlR\r\nAf5I9Z4Kx5VbwoqDKMLHUmIglFJKm0QpHXm1tgSkZOHXpbj/5FtPqJsmXCqRWatq\r\nr5KKSZXItbaR8dNIr2vgoYjedbbvl6vs1744kXCMgCtM8NN+6hPuO93u97VRVdL/\r\ndo+UodXvsgbGwrGqGMSRtubw2kKE8QddEjOxHPx/ZdHJFbebZ/zjQM3FekTkBV/F\r\nYLPi5hl4PQu9rHTuyRaJXQyQ+yQAYbUMRA4+tZCwa5pDeJg5fUoP2n1+7gyE1OLY\r\nT8wPkA7sMxBRUTPwPemJn9pca4f2ZRmUD/EhIALJTwf+PCDZ9OELd3Sm7WpFvRCA\r\nz+tolCu4+7cF+R1oGpTpJDVxb1p7IAEtFDl01XoLFNGqDBOu/AZjkpqTIy2NSWUk\r\nOpSp1GxIlTr6+diTyv9HAuhdiSlm2rwFVH3tFUhXOCJ8c9wVlJjGGkAYWmDT25Sm\r\n4DT1uODWGxTBHnzOxpkNpaZ3hCmarOR3huv90jMrYiq3yVq+xqWe9TGWc5JtUS+O\r\nJxvenTj4va1maSsqaoi4z3jYR0BWqCXM0kAjLLGAAVzLwGryucnUXKrxeXtcFGGg\r\noa40xu1W8JqMCPhN7nMBZ7JO09/EXeZ3kSjdq9hGbuby56VfgCeOi1wASEbUMHfH\r\nT9KgAWTFc3mkmOauYWoZEU6RNS3OyWzD3sws4zBZq7z9urZpzw2XsYgDAKqds1NA\r\nVVjzdwHBVH8nh/OnR7qkV52aydk+SihqGEWxQMqzaIUWwnbeF57CjfqLMaQy56dQ\r\njvqDqdaGytT9IHQVk6EVq6m1wsUZoNAoCJvnzBEOHllzCwCgU74F1zA8G9X0SakS\r\neue"
//		+ "/q0qrOJqDuIxGnl6AAVqmoA\u003d\u003d\r\n\u003da01P\r\n-----END PGP MESSAGE-----\r\n";

	String subject = "Test Subject " + new Date();
	String body = "Test Body " + new Date();
	
	// -- END SAMPLE DATA --

	System.out.println(new Date() + " /sendMessage Begin.");

	IncomingMessageDTOBuilder jsonMessageElementsBuilder = new IncomingMessageDTOBuilder(senderEmailAddr,
		recipients, subject, body, false, false, false, enuncryptedFiles);

	IncomingMessageDTO incomingMessageDTO = jsonMessageElementsBuilder.build();
	return incomingMessageDTO;
    }


}
