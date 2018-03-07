package com.uhmtech.util;

import android.content.Context;

import java.io.*;

public class LZMA {
	static {
		try {
			System.loadLibrary("lzma");
		} catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
		}
	}

	public static native int extract(String[] argc);
	
	/**
	 * extract7z
	 * 
	 * @param filePath
	 * @param destDir
	 * @return 0 is ok
	 */
	public static int extract7z( String filePath, String destDir) {	
		if (filePath==null || destDir==null) return -1;
		File outDir = new File(destDir);
		if (!outDir.exists()) {
			outDir.mkdirs();
		}

		String[] argc = { "7z", "x", filePath, destDir };
		return LZMA.extract(argc);
	}

	/**
	 * extract7z from asserts to sdcard or interal dir
	 * 
	 * @param context
	 * @param assertFile
	 * @param destDir
	 * @return 0 is ok
	 */
	public static int extract7zAssert(Context context, String assertFile, String destDir) {	
		// 1) copy from assert to sdcard
		// 2) unzip
		if (context==null || assertFile==null || destDir==null) return -1;
		String filename = assertFile.substring(assertFile.lastIndexOf("/") + 1);
		if (destDir.endsWith("/")) destDir += "/";
		String destPath = destDir + filename;
		File outDir = new File(destDir);
		if (!outDir.exists()) {
			outDir.mkdirs();
		}

		copyAssertsFile(context, assertFile, destPath);
		String[] argc = { "7z", "x", destPath, destDir };
		return LZMA.extract(argc);
	}
	
	private static void copyAssertsFile(Context context, String srcFile, String destPath) {
		InputStream in;
		try {
			in = context.getAssets().open(srcFile);
			OutputStream out = new BufferedOutputStream(new FileOutputStream(destPath));
			byte[] buffer = new byte[8092];
			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			out.flush();
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
