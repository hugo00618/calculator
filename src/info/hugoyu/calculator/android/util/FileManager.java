package info.hugoyu.calculator.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

public class FileManager {

	public static Object read(File f) {

		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
			}
		}

		try {
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);

			Object o = ois.readObject();

			ois.close();
			fis.close();

			return o;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static boolean write(File f, Object o) {

		try {
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(o);

			oos.close();
			fos.close();

			return true;
		} catch (FileNotFoundException fnfe) {
			return write(f, o);
		} catch (IOException ioe) {
			return false;
		}

	}

	public static void clear(File f) {
		write(f, null);
	}

}
