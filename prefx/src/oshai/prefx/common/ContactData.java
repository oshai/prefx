package oshai.prefx.common;

import javax.inject.Inject;

import oshai.prefx.adapters.IDataAdapter;
import oshai.prefx.phone_numbers.PhoneNumberNormalizer;
import android.util.Log;

public class ContactData {
    private static final String TAG = ContactData.class.getSimpleName();
    @Inject
    private IDataAdapter dataAdapter;
    @Inject
    private PhoneNumberNormalizer phoneNumberNormalizer;

    public String phone;
    public String id;
    public String name;
    public String photoId;
    public String photoUri;
    public String photoTumbUri;

    public String getSmsType() {
	try {
	    String normalizedPhone = phoneNumberNormalizer.getNumberAsKey(phone);
	    DataObject dataObject = dataAdapter.getD(normalizedPhone);
	    if (null != dataObject) {
		if (null != dataObject.smsType) {
		    return SmsType.valueOf(dataObject.smsType.trim()).asString();
		}
		return SmsType.valueOf(dataObject.smsTypeFromServer.trim()).asString();
	    }
	} catch (Exception e) {
	    Log.w(TAG, e);
	}
	return "";
    }

    @Override
    public String toString() {
	return "ContactData [phone=" + phone + ", id=" + id + ", name=" + name
		+ "]";
    }

}