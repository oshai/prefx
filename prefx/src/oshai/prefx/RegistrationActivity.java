package oshai.prefx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import oshai.prefx.activities.PrefxActivity;
import oshai.prefx.adapters.IHttpDataAdapter;
import oshai.prefx.async_tasks.RunnableTask;
import oshai.prefx.common.AvailableMessagingTypes;
import oshai.prefx.common.Constants;
import oshai.prefx.common.ErrorReporting;
import oshai.prefx.common.MyCoutriesXmlParser;
import oshai.prefx.common.SmsType;
import oshai.prefx.common.UserEmailFetcher;
import oshai.prefx.common.Utils;
import oshai.prefx.phone_numbers.CountryCallingCodeProvider;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class RegistrationActivity extends PrefxActivity {

private static final String TAG = RegistrationActivity.class.getSimpleName();
private EditText textPhoneNumber;
private Button btnSubmit;
private Spinner spinnerSmsProvider;
private Spinner spinnerCountryPrefix;
private SmsType smsType;
@Inject private AvailableMessagingTypes smsTypes;
@Inject private IHttpDataAdapter httpDataAdapter;

@Override
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	Log.v(TAG, "Activity State: onCreate()");
	initComponents();
}

private void initComponents() {
	setContentView(R.layout.registration);
	textPhoneNumber = (EditText) findViewById(R.id.editTextPhone);
	addItemsOnSpinnerCountry();
	addItemsOnSpinnerSmsType();
	addListenerOnSpinnerItemSelection();
	addListenerOnUpdateButton();
}

private String getCountryCallingCode() {
	return new CountryCallingCodeProvider().getCountryCallingCode();
}
private void addItemsOnSpinnerCountry() {
	spinnerCountryPrefix = (Spinner) findViewById(R.id.country_spinner);
	String countryCallingCode = getCountryCallingCode();
	int position = 0;
	List<CountryCode> list = new ArrayList<CountryCode>();
	try {
		list = new MyCoutriesXmlParser().parse(getApplicationContext());
	} catch (Exception e) {
		e.printStackTrace();
	}
	Collections.sort(list, new Comparator<CountryCode>() {
		@Override
		public int compare(CountryCode lhs, CountryCode rhs) {
			return lhs.countryView.compareTo(rhs.countryView);
		}
	});
	for (int i = 0; i < list.size(); i++) {
		if (countryCallingCode.equalsIgnoreCase(list.get(i).country)){
			position = i;
			break;
		}
	}
	ArrayAdapter<CountryCode> dataAdapter = new ArrayAdapter<CountryCode>(this, android.R.layout.simple_spinner_item, list);
	dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	spinnerCountryPrefix.setAdapter(dataAdapter);
	spinnerCountryPrefix.setSelection(position);
}
public static class CountryCode {
public String code;
 public String country;
 public String countryView;
public String local;
 @Override
	public String toString() {
		return countryView + " (" + code + ")";
	}
}
private void addItemsOnSpinnerSmsType() {

	spinnerSmsProvider = (Spinner) findViewById(R.id.spinnerSmsProvider);
	List<String> list = new ArrayList<String>();
	for (SmsType s : smsTypes.values()) {
		list.add(s.asString());
	}
	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
	dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	spinnerSmsProvider.setAdapter(dataAdapter);
}

private void addListenerOnSpinnerItemSelection() {
	spinnerSmsProvider = (Spinner) findViewById(R.id.spinnerSmsProvider);
	spinnerSmsProvider.setOnItemSelectedListener(new OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			smsType = smsTypes.values().get(position);
		}

		public void onNothingSelected(AdapterView<?> parent) {
		}
	});
}

private void submitButtonOnClick() {
	trackEvent("registration", spinnerCountryPrefix.getSelectedItem() + "-" + textPhoneNumber.getText().toString() + ":" + smsType.toString(),"", 1);
	storeRegistration();
	Intent newIntent = new Intent(RegistrationActivity.this.getApplicationContext(), FinishActivationActivity.class);
	startActivity(newIntent);
	finish();
}

private void storeRegistration() {
	CountryCode countryCodeObject = (CountryCode)spinnerCountryPrefix.getSelectedItem();
	String countryCode = countryCodeObject.code;
	String phoneNumber = textPhoneNumber.getText().toString();
	if (!Utils.isEmpty(phoneNumber) && phoneNumber.startsWith(countryCodeObject.local)){
		phoneNumber = phoneNumber.substring(countryCodeObject.local.length());
	}
	sendToServer(countryCode, phoneNumber);
	storeRegistraionInSharedPreferences(countryCode, phoneNumber);
}

private void sendToServer(String countryCode, String phoneNumber) {
	final String phone = countryCode + phoneNumber;
	final String smsTypeString = smsType.toString();
	final String updater = "registration_of_" + UserEmailFetcher.getEmail(getApplicationContext());
	RunnableTask task = new RunnableTask(new Runnable(){
		@Override
		public void run() {
			try {
			    httpDataAdapter.put(phone, smsTypeString, updater );
			} catch (Exception e) {
			   new ErrorReporting().handleSilent(e);
			}
		}});
	task.execute();
}

private void storeRegistraionInSharedPreferences(String countryCode, String phoneNumber) {
	SharedPreferences settings = getSharedPreferences(Constants.PREFERENCE_STORE, 0);
	Editor editor = settings.edit();
	editor.putString(Constants.PREFERENCE_COUNTRY_PREFIX, countryCode);
	editor.putString(Constants.PREFERENCE_PHONE_NUMBER, phoneNumber);
	editor.putString(Constants.PREFERENCE_PREFERED_SMS_APPLICATION, smsType.toString());
	editor.commit();
}

private void addListenerOnUpdateButton() {

	btnSubmit = (Button) findViewById(R.id.okBtn);

	btnSubmit.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			submitButtonOnClick();
		}

	});
}

}
