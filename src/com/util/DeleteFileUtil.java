package com.util;

import java.io.File;

/**
 * DeleteFileUtil.java
 * 
 * @description delete one file
 *
 */
public class DeleteFileUtil {

	/**
	 * Delete file
	 * 
	 * @param sFileName
	 *            file name
	 * @return true if delete success or false if fail
	 */
	public static boolean DeleteFile(String sFileName) {
		File file = new File(sFileName);
		
		// first determine whether a file exists
		if(!file.exists())
			return false;
		
		return file.delete();
	}
}
