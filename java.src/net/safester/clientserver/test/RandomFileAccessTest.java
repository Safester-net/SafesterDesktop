/**
 * 
 */
package net.safester.clientserver.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author Nicolas de Pomereu
 *
 */
public class RandomFileAccessTest {

    // BEGIN VALUES NOT MODIFIED BY SESSION PARAMETERS
    /** The default buffer size when reading a file */
    public static final int DEFAULT_READ_BUFFER_SIZE = 4 * 1024;

    /** The default Buffer size when writing a file */
    public static final int DEFAULT_WRITE_BUFFER_SIZE = 4 * 1024;

    // For Chunk Length comparison. If buffer read > chunk
    // length ==> exit
    long totalRead = 0;

    /**
     * 
     */
    public RandomFileAccessTest() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {
//	
//	if (args.length == 0) {
//	    System.out.println("java RandomFileAccessTest <fileIn> <FileOut>");
//	}
//
//	File fileIn = new File(args[0]);
//	File fileOut = new File(args[1]);

	String fileInStr = "c:\\test\\Test.mp4";
	String fileoutStr = "c:\\test\\Test.out.mp4";

	File fileIn = new File(fileInStr);

	File fileOut = new File(fileoutStr);

	if (!fileIn.exists()) {
	    throw new FileNotFoundException("File does not exist: " + fileIn);
	}
	
	long pos = 0;
	
	if (fileOut.exists()) {
	    pos = fileOut.length();
	}
	
	System.out.println(getNowFormatted() + " Starting...");
	
	RandomAccessFile raf = null;
	OutputStream out = null;

	try {
	    raf = new RandomAccessFile(fileIn, "rw");
	    raf.seek(pos);
	    out = new BufferedOutputStream(new FileOutputStream(fileOut, true));

	    long fileLength = fileIn.length();

	    RandomFileAccessTest randomFileAccessTest = new RandomFileAccessTest();
	    randomFileAccessTest.copy(raf, out, fileLength);

	} finally {
	    raf.close();
	    out.close();
	}
	
	System.out.println(getNowFormatted() + " Done!");
	

    }

    private void copy(RandomAccessFile raf, OutputStream out, long fileLength) throws IOException {
	int writeBufferSize = DEFAULT_WRITE_BUFFER_SIZE;

	// For Chunk Length comparison. If buffer read > chunk
	// length ==> exit
	long totalRead = 0;
	long tempRead = 0;

	byte[] tmp = new byte[writeBufferSize];
	int len;

	while ((len = raf.read(tmp)) >= 0) {
	    totalRead += len;
	    tempRead += len;
	    out.write(tmp, 0, len);

	    if (tempRead > fileLength / 10) {
		System.out.println(getNowFormatted() + " Total Read Mb: " + totalRead / (1024 * 1024) + " Mb");
		tempRead = 0;
	    }

	}
    }

    public static String getNowFormatted() {
	Timestamp tsNow = new Timestamp(System.currentTimeMillis());
	DateFormat df = new SimpleDateFormat("yy/MM/dd HH:mm:ss.SSS");
	String now = df.format(tsNow);
	return now;
    }

}
