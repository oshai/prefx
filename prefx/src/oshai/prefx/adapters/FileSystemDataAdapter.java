package oshai.prefx.adapters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import oshai.prefx.common.DataObject;
import oshai.prefx.common.Utils;
import android.os.Environment;
import android.util.Log;

public class FileSystemDataAdapter implements IDataAdapter {

private static final String SEPERATOR = "_~~~_";
private static final String TAG = FileSystemDataAdapter.class.getSimpleName();

	private String getPath(String fileName) {
		File directory = Environment.getExternalStorageDirectory();
		String fullPath = directory + "/prefx/data/" + fileName;
		Log.v(TAG, "getPath() - fullPath " + fullPath);
		return fullPath;
	}

	private String getDir() {
		return getPath("");
	}
	@Override
	public void putD(DataObject data) {
		boolean mkdirs = new File(getDir()).mkdirs();
		Log.d(TAG, "dir created " + getDir() + " " + mkdirs);
		Log.v(TAG, "data is " + data);
		String fullPath = getPath(data.key);
		Log.v(TAG, "fullPath " + fullPath);
		StringBuilder sb = new StringBuilder();
		sb.append("key=").append(data.key).append(SEPERATOR);
		sb.append("phone=").append(data.phone).append(SEPERATOR);
		if (!Utils.isEmpty(data.name)) {
			sb.append("name=").append(data.name).append(SEPERATOR);
		}
		sb.append("smsType=").append(data.smsType).append(SEPERATOR);
		if (!Utils.isEmpty(data.smsTypeFromServer)) {
			sb.append("smsTypeFromServer=").append(data.smsTypeFromServer).append(SEPERATOR);
		}
		Log.v(TAG, "writing " + fullPath + " data: " + sb.toString());
		FileUtils.writeFileToSDCard(fullPath, sb.toString());
	}
	@Override
	public DataObject getD(String key) {
		String fullPath = getPath(key);
		Log.v(TAG, "fullPath " + fullPath);
		if (!FileUtils.exists(fullPath)) {
			return null;
		}
		String content = FileUtils.readFileFromSDCard(fullPath);
		DataObject $ = new DataObject();
		for (String line : content.split(SEPERATOR)) {
			if (Utils.isEmpty(line))
			{
				continue;
			}
			Log.v(TAG, "line is " + line);
			if (!line.contains("=")) {//v1
				$.smsType = line.trim();
			}
			else {
				String[] split2 = line.split("=");
				if (split2[0].equals("phone") && !"null".equals(split2[1]) ){
					$.phone = split2[1];
				}
				else if (split2[0].equals("name") && !"null".equals(split2[1]) ){
					$.name = split2[1];
				}
				else if (split2[0].equals("smsType") && !"null".equals(split2[1]) ){
					$.smsType = split2[1];
				}
				else if (split2[0].equals("smsTypeFromServer") && !"null".equals(split2[1]) ){
					$.smsTypeFromServer = split2[1];
				}
				else if (split2[0].equals("key") && !"null".equals(split2[1]) ){
					$.key = split2[1];
				}
			}
				
		}
		Log.v(TAG, "data is " + $);
		return $ ;
	}

	@Override
	public List<String> getAllPhones() {
		List<String> $ = new ArrayList<String>();
		File[] allFiles = FileUtils.getAllFiles(getDir());
		if (null == allFiles)
		{
			return $;
		}
		for (File file : allFiles) {
			$.add(file.getName());
		}
		return $;
	}

	@Override
	public void deleteAll() {
		FileUtils.deleteDir(getDir());	
	}

}
