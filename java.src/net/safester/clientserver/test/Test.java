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
package net.safester.clientserver.test;

import java.awt.Font;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.NumericEntityEscaper;
import org.awakefw.file.api.util.HtmlConverter;

import net.safester.application.util.crypto.PassphraseUtil;

public class Test {

    /** Universal and clean line separator */
    public static String CR_LF = System.getProperty("line.separator");

    Connection connection = null;

    /**
     * @param connection
     */
    public Test(Connection connection) {
	this.connection = connection;
    }

    /**
     * @param args
     */

    /**
     * Translator object for escaping HTML version 4.0.
     *
     * While {@link #escapeHtml4(String)} is the expected method of use, this
     * object allows the HTML escaping functionality to be used
     * as the foundation for a custom translator.
     *
     * @since 3.0
     */
    public static final CharSequenceTranslator ESCAPE_HTML4 =
        new AggregateTranslator(
            //new LookupTranslator(EntityArrays.BASIC_ESCAPE()),
            //new LookupTranslator(EntityArrays.ISO8859_1_ESCAPE()),
            //new LookupTranslator(EntityArrays.ISO8859_1_ESCAPE())
        );

    public static void main(String[] args) throws Exception {

	String email = "test@test.com";
	String password = "avancée1234&<_%[@?\"%+*#'(-_^%*!:/;.,?%*!=";

	String result = PassphraseUtil.computeHashAndSaltedPassphrase(email, password.toCharArray());
	String toPrint = new Date() + " In Java " + email + " " + password + ": " + CR_LF + result;

	System.out.println(toPrint);
	boolean doContinue = false;
	if (! doContinue)
	    return;

	Font font = new JLabel().getFont();
	System.out.println(font);
	System.out.println(font.getName());

	System.out.println(Locale.FRENCH.toLanguageTag());
	System.out.println(HtmlConverter.fromHtml("Charles Andr&eacute;"));
	System.out.println(HtmlConverter.fromHtml("Charles Andr&#233;"));
	System.out.println(System.currentTimeMillis());

	String text = JOptionPane.showInputDialog("Enter the text");

	String textHtml = null;

	textHtml = org.apache.commons.lang3.StringEscapeUtils.ESCAPE_HTML4
		.with(NumericEntityEscaper.between(0x7f, Integer.MAX_VALUE)).translate(text);
	System.out.println("textHtml lang3: " + textHtml);

//        textHtml = org.apache.commons.lang3.StringEscapeUtils.escapeHtml(text);
//        System.out.println("textHtml lang3: " + textHtml);

	text = StringEscapeUtils.unescapeHtml4(textHtml);
	JOptionPane.showInputDialog("Enter the text", text);

	if (true)
	    return;

	System.out.println(new Date());

	File file = new File("c:\\temp\\crypto-145.zip");

	String s = "00000000000000000000000000000000000000000000000000";
	wipeFile(file, s);

	s = "11111111111111111111111111111111111111111111111111";
	wipeFile(file, s);

	System.out.println(new Date());
    }

    /**
     * Wipe the file using a pattern
     *
     * @param file    the file to wipe
     * @param pattern the pattern to use
     * @throws IOException
     */
    public static void wipeFile(File file, String pattern) throws IOException {
	if (file == null) {
	    throw new IllegalArgumentException("file can not be null!");
	}

	if (pattern == null) {
	    throw new IllegalArgumentException("pattern can not be null!");
	}

	BufferedOutputStream bos = null;

	try {
	    Long length = file.length();
	    bos = new BufferedOutputStream(new FileOutputStream(file));

	    int cpt = 0;
	    int len = pattern.length();

	    while (cpt <= length) {
		cpt += len;
		bos.write(pattern.getBytes());
	    }
	} finally {
	    IOUtils.closeQuietly(bos);
	}
    }

    /*
	String escaped =
	        HtmlEscape.escapeHtml(
	        	password,
	                HtmlEscapeType.DECIMAL_REFERENCES,
	                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);

	String amp_decimal =	        HtmlEscape.escapeHtml(
        	"&",
                HtmlEscapeType.DECIMAL_REFERENCES,
                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);


	String gt_decimal =	        HtmlEscape.escapeHtml(
        	">",
                HtmlEscapeType.DECIMAL_REFERENCES,
                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);

	String lt_decimal =	        HtmlEscape.escapeHtml(
        	"<",
                HtmlEscapeType.DECIMAL_REFERENCES,
                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);

	String quote_decimal =	        HtmlEscape.escapeHtml(
        	"\"",
                HtmlEscapeType.DECIMAL_REFERENCES,
                HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);

	String amp_classic = org.apache.commons.lang3.StringEscapeUtils.ESCAPE_HTML4
		.with(NumericEntityEscaper.between(0x7f, Integer.MAX_VALUE)).translate("&");
	String gt_classic = HtmlConverter.toHtml(">");
	String lt_classic = HtmlConverter.toHtml("<");
	String quote_classic = HtmlConverter.toHtml("\"");

	System.out.println("& : " + amp_decimal + " " +  amp_classic);
	System.out.println("> : " + gt_decimal + " " + gt_classic);
	System.out.println("< : " + lt_decimal + " " + lt_classic);
	System.out.println("\" : " + quote_decimal + " " + quote_classic);

	escaped = escaped.replace(amp_decimal, amp_classic);
	escaped = escaped.replace(gt_decimal, gt_classic);
	escaped = escaped.replace(lt_decimal, lt_classic);
	escaped = escaped.replace(quote_decimal, quote_classic);

	System.out.println("Java " + password + ": " + escaped);
	*/
}
