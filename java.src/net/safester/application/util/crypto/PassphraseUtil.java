/*
 * This file is part of Safester.
 * Copyright (C) 2019, KawanSoft SAS
 * (https://www.Safester.net). All rights reserved.
 *
 * Safester is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * Safester is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301  USA
 *
 * Any modifications to this file must keep this entire header
 * intact.
 */
package net.safester.application.util.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.apache.commons.lang3.text.translate.NumericEntityEscaper;
import org.awakefw.commons.api.server.util.Sha1;
import org.awakefw.file.api.util.HtmlConverter;
import org.unbescape.html.HtmlEscape;
import org.unbescape.html.HtmlEscapeLevel;
import org.unbescape.html.HtmlEscapeType;

import com.safelogic.utilx.ArrayMgr;

import net.safester.application.parms.Parms;

/**
 * Utility class fo passphrase management
 *
 * @author Nicolas de Pomereu
 *
 */
public class PassphraseUtil
{
    public static boolean DEBUG = false;

    //
    // There a four values which must not be encoded with numerical/decimal values: &, >, <, "
    //
    private final static String AMP_DECIMAL = HtmlEscape.escapeHtml("&", HtmlEscapeType.DECIMAL_REFERENCES,
	    HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);

    private final static String GT_DECIMAL = HtmlEscape.escapeHtml(">", HtmlEscapeType.DECIMAL_REFERENCES,
	    HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);

    private final  static String LT_DECIMAL = HtmlEscape.escapeHtml("<", HtmlEscapeType.DECIMAL_REFERENCES,
	    HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);

    private final static String QUOTE_DECIMAL = HtmlEscape.escapeHtml("\"", HtmlEscapeType.DECIMAL_REFERENCES,
	    HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);

    private final static String AMP_CLASSIC = org.apache.commons.lang3.StringEscapeUtils.ESCAPE_HTML4
	    .with(NumericEntityEscaper.between(0x7f, Integer.MAX_VALUE)).translate("&");
    private final static String GT_CLASSIC = HtmlConverter.toHtml(">");
    private final static String LT_CLASSIC = HtmlConverter.toHtml("<");
    private final static String QUOTE_CLASSIC = HtmlConverter.toHtml("\"");


    /**
     * Get password for connection using hash
     * @param login         the login
     * @param password
     *
     * @return  the password to use for connection
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public static String computeHashAndSaltedPassphrase(String login, char []password)
            throws NoSuchAlgorithmException, NoSuchProviderException {
        Sha1 sha1 = new Sha1();

        //Use old system to maintain compatibility
        String passphraseStr = new String(password);

        // Always convert passphrase to HTML so stah the getBytes() will produce
        // the same on all platforms: Windows, Mac OS X, Linux

        // NO! See 1) and 2: Below
        //passphraseStr = HtmlConverter.toHtml(passphraseStr);

        // 1) Encode with 	String escaped =
//        HtmlEscape.escapeHtml(
//        	password,
//                HtmlEscapeType.DECIMAL_REFERENCES,
//                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);

         passphraseStr =
              HtmlEscape.escapeHtml(
        	      passphraseStr,
                      HtmlEscapeType.DECIMAL_REFERENCES,
                      HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);

         // 2) Replace the numerical for &, >, <, " by their classic HTML encode values
         passphraseStr = replaceHtmlNumericByHtmlClassicEncoding(passphraseStr);

         debug("html encoded passphrase: " + passphraseStr);

         passphraseStr = sha1.getHexHash(passphraseStr.getBytes());
         passphraseStr = passphraseStr.substring(0,20);
         passphraseStr = passphraseStr.toLowerCase();

        //Apply salt and hash iterations
        //public static String salt = "ThiS*IsSAlt4loGin$";
        //public static int PASSPHRASE_HASH_ITERATIONS = 3;

        String salt = login + Parms.salt;
        byte [] bPassphraseSaltCompute = ArrayMgr.AddByte(passphraseStr.getBytes(), salt.getBytes());
        String connectionPassword = "";
        for(int i = 0; i < Parms.PASSPHRASE_HASH_ITERATIONS; i++){
            connectionPassword = sha1.getHexHash(bPassphraseSaltCompute);
            bPassphraseSaltCompute = connectionPassword.getBytes();
        }

        connectionPassword = connectionPassword.substring(0, 20); // half of hash
        connectionPassword = connectionPassword.toLowerCase(); // All tests in lowercase

        return connectionPassword;
    }


    private static String replaceHtmlNumericByHtmlClassicEncoding(String passphraseStr) {
	if (passphraseStr == null) {
	    throw new NullPointerException("passphraseStr is null!");
	}

	passphraseStr = passphraseStr.replace(AMP_DECIMAL, AMP_CLASSIC);
	passphraseStr = passphraseStr.replace(GT_DECIMAL, GT_CLASSIC);
	passphraseStr = passphraseStr.replace(LT_DECIMAL, LT_CLASSIC);
	passphraseStr = passphraseStr.replace(QUOTE_DECIMAL, QUOTE_CLASSIC);

	return passphraseStr;
    }

    @SuppressWarnings("unused")
    private static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new java.util.Date() + " " + s);
	}
    }

}

