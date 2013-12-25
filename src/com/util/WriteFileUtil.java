package com.util;

import java.io.FileWriter;
import java.io.IOException;

/**
 * WriteFileUtil.java
 * 
 * @description write some information to file
 *
 */
public class WriteFileUtil {

	/**
	 * Write content to file
	 * 
	 * @param sFileName
	 *            file name
	 * @param content
	 *            the content that need to write to file
	 * @return true if write success or false if write fail
	 */
	public static boolean WriteFile(String sFileName, String content) {
		FileWriter fw = null;
		
		try {
			fw = new FileWriter(sFileName, true);

            fw.write(content);
            fw.close(); 
		} catch (IOException e) {
			e.printStackTrace();
			// if abnormal then return false
			return false;
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e1) {
				}
			}
		}
		
		// if finish writing then return true
		return true;
	}
}
