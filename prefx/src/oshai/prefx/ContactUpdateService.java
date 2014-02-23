package oshai.prefx;

import java.util.List;

import javax.inject.Inject;

import oshai.prefx.adapters.IDataAdapter;
import oshai.prefx.adapters.IHttpDataAdapter;
import oshai.prefx.common.Constants;
import oshai.prefx.common.ContactData;
import oshai.prefx.contacts.IContactReader;
import oshai.prefx.phone_numbers.PhoneNumberNormalizer;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class ContactUpdateService extends Service {

static final String TAG = ContactUpdateService.class.getSimpleName();
@Inject private IContactReader contactReader;
@Inject IHttpDataAdapter httpDataAdapter;
@Inject IDataAdapter dataAdapter;
@Inject PhoneNumberNormalizer phoneNumberNormalizer;
private ContactUpdateTask updateTask;

@Override
public void onCreate() {
	Log.v(TAG, "Activity State: onCreate() " + getClass().getSimpleName());
	MyApplication.injector().injectMembers(this);
}

public int onStartCommand(Intent intent, int flags, int startId) {
	Log.i(TAG, "onstartcommand");
	update();
	return super.onStartCommand(intent, flags, startId);
};

private void update() {
	if (null != updateTask && ! (updateTask.isFinished())) {
		Log.i(TAG, "task already running");
		return;
	}
	List<ContactData> contacts = contactReader.readContacts(Constants.SELECTION_ALL_PHONE);
	SharedPreferences settings = getSharedPreferences(Constants.PREFERENCE_STORE, 0);
	    String prefix = settings.getString(Constants.PREFERENCE_COUNTRY_PREFIX, null);
		String phone1 = settings.getString(Constants.PREFERENCE_PHONE_NUMBER, null);
		String updater = prefix + phone1;
//	updateTask = new ContactUpdateTask(contacts, this, updater);
//	updateTask.execute();
}
@Override
public void onDestroy() {
}
@Override
public IBinder onBind(Intent intent) {
	return null;
}
}
