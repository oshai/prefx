package oshai.prefx.adapters;

import oshai.prefx.common.DataObject;

public class NoOpHttpDataAdapter implements IHttpDataAdapter{

    @Override
    public void put(String phone, String smsType, String updater) {
	
    }

    @Override
    public DataObject getData(String key, String updater) {
	return null;
    }

    @Override
    public void log(String mail, String phone, String message) {
	
    }

}