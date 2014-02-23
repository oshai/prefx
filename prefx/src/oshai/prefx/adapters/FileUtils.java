package oshai.prefx.adapters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.util.Log;

public class FileUtils {
	private static final String TAG = FileUtils.class.getSimpleName();

	public static String readFileFromSDCard(String fullPath) {
		Log.v(TAG, "readFileFromSDCard() - " + fullPath);
		StringBuilder builder = new StringBuilder();
		File file = new File(fullPath);
		if (!file.exists()) {
			throw new RuntimeException("file not found " + fullPath);
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					Log.w(TAG, "error closing reader", e);
				}
			}
		}
		return builder.toString();
	}

	public static void writeFileToSDCard(String fullPath,
			String content) {
		Log.v(TAG, "writeFileToSDCard() - file " + fullPath + " content "
				+ content);
		File file = new File(fullPath);

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file)));
				writer.write(content + "\n");
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					Log.w(TAG, "error closing reader", e);
				}
			}
		}
		Log.i(TAG, "file saved " + fullPath + " content " + content);
	}

	public static File[] getAllFiles(String path) {
		return new File(path).listFiles();
	}

	public static boolean deleteDir(String path) {
		return deleteDirectory(new File(path));
	}

	public static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	public static boolean exists(String fullPath) {
		File file = new File(fullPath);
		return file.exists();
	}
}