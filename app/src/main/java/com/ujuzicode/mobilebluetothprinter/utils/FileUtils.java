package com.ujuzicode.mobilebluetothprinter.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;

public class FileUtils {

	public static void AddToFile(byte[] buffer, int byteOffset, int byteCount,
			String dumpfile) {
		if (null == dumpfile)
			return;
		if (null == buffer)
			return;
		if (byteOffset < 0 || byteCount <= 0)
			return;

		try {
			File file = new File(dumpfile);
			if (!file.exists()) {
				file.createNewFile();
			}
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(file.length());
			raf.write(buffer, byteOffset, byteCount);
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void AddToFile(String text, String dumpfile) {
		if (null == dumpfile)
			return;
		if (null == text)
			return;
		if ("".equals(text))
			return;

		try {
			File file = new File(dumpfile);
			if (!file.exists()) {
				file.createNewFile();
			}
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(file.length());
			raf.write(text.getBytes());
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void SaveToFile(String text, String dumpfile) {
		if (null == dumpfile)
			return;
		if (null == text)
			return;

		try {
			File file = new File(dumpfile);
			if (file.exists())
				file.delete();

			file.createNewFile();
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(0);
			raf.write(text.getBytes());
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String ReadToString(String filePathName) {

		File file = new File(filePathName);
		if (!file.exists())
			return null;

		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(filecontent);
			in.close();
			return new String(filecontent);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] ReadToMem(String filePathName) {
		File file = new File(filePathName);
		if (!file.exists())
			return null;

		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(filecontent);
			in.close();
			return filecontent;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> void SaveToFile(T t, String file, Context context) {

		FileOutputStream fos;
		ObjectOutputStream oos;

		try {
			fos = context.openFileOutput(file, Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(t);
			oos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static <T> void SaveToSD(T t, String file) {

		FileOutputStream fos;
		ObjectOutputStream oos;

		try {
			File dir = new File(file).getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}

			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(t);
			oos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T ReadFromFile(String file, Context context) {

		T t = null;
		FileInputStream fis;
		ObjectInputStream ois;

		try {
			fis = context.openFileInput(file);
			ois = new ObjectInputStream(fis);
			t = (T) ois.readObject();
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return t;
	}

	@SuppressWarnings("unchecked")
	public static <T> T ReadFromSD(String file, Context context) {

		T t = null;
		FileInputStream fis;
		ObjectInputStream ois;

		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			t = (T) ois.readObject();
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return t;
	}
	
	public void saveDataToBin(String fileName, byte[] data) {
		File f = new File(Environment.getExternalStorageDirectory().getPath(),
				fileName);
		try {
			f.createNewFile();
		} catch (IOException e) {
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
			fOut.write(data, 0, data.length);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
	


	@SuppressWarnings("unused")
	private void saveDataToBin(byte[] data) {
		File f = new File(Environment.getExternalStorageDirectory().getPath(),
				"Btatotest.bin");
		try {
			f.createNewFile();
		} catch (IOException e) {
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
			fOut.write(data, 0, data.length);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	public void saveMyBitmap(Bitmap mBitmap, String name) {
		File f = new File(Environment.getExternalStorageDirectory().getPath(),
				name);
		try {
			f.createNewFile();
		} catch (IOException e) {
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

	}
}
