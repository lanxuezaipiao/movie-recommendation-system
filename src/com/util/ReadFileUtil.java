package com.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ReadFileUtil.java
 * 
 * @description read information from file
 *
 */
public class ReadFileUtil {

	/**
	 * Read file by line, each line ends with \r\n
	 * 
	 * @param sFileName
	 *            file name
	 * @return a list of all lines
	 */
	public static List<String> ReadFileByLine(String sFileName) {
		File file = new File(sFileName);
		BufferedReader reader = null;
		
		// return information list
		List<String> infoList = new ArrayList<String>();
		
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			
			// read by line
			while ((tempString = reader.readLine()) != null) {
				if(!tempString.equals("")) {  //not null
					infoList.add(tempString);
				}			
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		
		return infoList;
	}

}
