package oshai.prefx.adapters;

import java.util.List;

import oshai.prefx.common.DataObject;


public interface IDataAdapter {
	void putD(DataObject data);
	List<String> getAllPhones();
	DataObject getD(String key);
	void deleteAll();
}
