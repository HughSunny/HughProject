package com.set.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class UnZip {
	
	/**
	 * 瑙ｅ帇涓�釜鍘嬬缉鏂囨。 鍒版寚瀹氫綅缃�
	 * @param zipFileString	鍘嬬缉鍖呯殑鍚嶅瓧
	 * @param outPathString	鎸囧畾鐨勮矾寰�
	 * @throws Exception
	 */
	public static void UnZipFolder(String zipFileString, String outPathString) {
		java.util.zip.ZipInputStream inZip = null;
		File desFile = new File(outPathString);
		if(!desFile.exists()||!desFile.isDirectory()){
			desFile.mkdirs();
		}
		try {
			inZip = new java.util.zip.ZipInputStream(new java.io.FileInputStream(zipFileString));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(inZip == null){
			return;
		}
		java.util.zip.ZipEntry zipEntry;
		String szName = "";
		
		try {
			while ((zipEntry = inZip.getNextEntry()) != null) {
				szName = zipEntry.getName();
			
				if (zipEntry.isDirectory()) {
					// get the folder name of the widget
					szName = szName.substring(0, szName.length() - 1);
					File folder = new File(outPathString + File.separator + szName);
					folder.mkdirs();
			
				} else {
			
					File file = new File(outPathString + File.separator + szName);
					file.createNewFile();
					// get the output stream of the file
					FileOutputStream out = new FileOutputStream(file);
					int len;
					byte[] buffer = new byte[1024];
					// read (len) bytes into buffer
					while ((len = inZip.read(buffer)) != -1) {
						// write (len) byte from buffer at the position 0
						out.write(buffer, 0, len);
						out.flush();
					}
					out.close();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}//end of while
		
		try {
			inZip.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}//end of func
}
