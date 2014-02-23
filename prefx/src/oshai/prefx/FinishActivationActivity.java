package oshai.prefx;

import java.util.List;

import javax.inject.Inject;

import oshai.prefx.activities.PrefxActivity;
import oshai.prefx.adapters.IDataAdapter;
import oshai.prefx.adapters.IHttpDataAdapter;
import oshai.prefx.common.Constants;
import oshai.prefx.common.ContactData;
import oshai.prefx.contacts.IContactReader;
import oshai.prefx.phone_numbers.PhoneNumberNormalizer;
import oshai.prefx.tabs.AndroidTabLayoutActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FinishActivationActivity extends PrefxActivity {

private static final String TAG = FinishActivationActivity.class.getSimpleName();

@Inject
private IContactReader contactReader;
@Inject private IHttpDataAdapter httpDataAdapter;
@Inject private IDataAdapter dataAdapter;
@Inject private PhoneNumberNormalizer phoneNumberNormalizer;
private ContactUpdateTask updateTask;

@Override
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	Log.v(TAG, "Activity State: onCreate()");
	initComponents();
}

private void initComponents() {
	setContentView(R.layout.finish_registration);
	addListenerOnSubmitButton();
	refreshData();
}

private void refreshData() {
    if (null != updateTask && ! (updateTask.isFinished())) {
	Log.i(TAG, "task already running");
	return;
    }
    List<ContactData> contacts = contactReader.readContacts(Constants.SELECTION_ALL_PHONE);
    SharedPreferences settings = getSharedPreferences(Constants.PREFERENCE_STORE, 0);
    String prefix = settings.getString(Constants.PREFERENCE_COUNTRY_PREFIX, null);
    String phone1 = settings.getString(Constants.PREFERENCE_PHONE_NUMBER, null);
    String updater = prefix + phone1;
    updateTask = new ContactUpdateTask(contacts, updater, httpDataAdapter, dataAdapter, phoneNumberNormalizer);
    updateTask.execute();
//    refresh();
}


private void submitButtonOnClick() {
    	Intent newIntent = new Intent(getApplicationContext(), AndroidTabLayoutActivity.class);
	startActivity(newIntent);
	finish();
}

private void addListenerOnSubmitButton() {


	((Button) findViewById(R.id.okBtn)).setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			submitButtonOnClick();
		}

	});
}

}
