package oshai.prefx.adapters;

import oshai.prefx.common.DataObject;

public interface IHttpDataAdapter {

    void put(String phone, String smsType, String updater);

    DataObject getData(String key, String updater);

    void log(String mail, String phone, String message);

}