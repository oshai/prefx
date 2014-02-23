package oshai.prefx;

import java.util.List;

import oshai.prefx.adapters.IDataAdapter;
import oshai.prefx.adapters.IHttpDataAdapter;
import oshai.prefx.common.Constants;
import oshai.prefx.common.ContactData;
import oshai.prefx.common.DataObject;
import oshai.prefx.common.Utils;
import oshai.prefx.phone_numbers.PhoneNumberNormalizer;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class ContactUpdateTask extends AsyncTask<Void, Integer, Void> {
    static final String TAG = ContactUpdateTask.class.getSimpleName();
    private final IHttpDataAdapter httpDataAdapter;
    private final IDataAdapter dataAdapter;
    private final PhoneNumberNormalizer phoneNumberNormalizer;
    private final List<ContactData> contacts;
    private boolean finished;
    private final String updater;

    

public ContactUpdateTask(List<ContactData> contacts, String updater,
	    IHttpDataAdapter httpDataAdapter, IDataAdapter dataAdapter,
	    PhoneNumberNormalizer phoneNumberNormalizer) {
	super();
	this.contacts = contacts;
	this.updater = updater;
	this.httpDataAdapter = httpDataAdapter;
	this.dataAdapter = dataAdapter;
	this.phoneNumberNormalizer = phoneNumberNormalizer;
    }

public boolean isFinished() {
	return finished;
}

protected Void doInBackground(Void... params) {
	Log.i(TAG, "starting task");
	int i = 0;
	for (ContactData contactData : contacts) {
		if (Utils.isEmpty(contactData.phone)) {
			continue;
		}
		try {
			String phone = phoneNumberNormalizer.getNormalizedNumberWithCountryPrefix(contactData.phone);
			DataObject dataObject = httpDataAdapter.getData(phone,updater);
			if (null != dataObject) {
				dataObject.key = phoneNumberNormalizer.getNumberAsKey(contactData.phone);
				DataObject d = dataAdapter.getD(dataObject.key);
				if (null == d){
				    d = dataObject;
				}
				else {
				    d.smsTypeFromServer = dataObject.smsTypeFromServer;
				}
				dataAdapter.putD(d);
			}
		} catch (Exception e) {
			Log.w(TAG, "error on phone " + contactData.phone, e);
		}
		publishProgress((int) ((i / (float) contacts.size()) * 100));
		i++;
	}
	Log.i(TAG, "task finished");
    return null;
}

protected void onPostExecute(Void result) {
	finished = true;
	 Log.d("sender", "Broadcasting message");
	  Intent intent = new Intent(Constants.BROADCAST_CONTACT_REFRESH);
	  // You can also include some extra data.
	  intent.putExtra("message", "This is my message!");
	  LocalBroadcastManager.getInstance(MyApplication.instance()).sendBroadcast(intent);
}

}