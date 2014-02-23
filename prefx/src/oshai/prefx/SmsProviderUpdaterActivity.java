package oshai.prefx;

import java.util.List;

import javax.inject.Inject;

import oshai.prefx.activities.PrefxActivity;
import oshai.prefx.adapters.IDataAdapter;
import oshai.prefx.adapters.IHttpDataAdapter;
import oshai.prefx.async_tasks.RunnableTask;
import oshai.prefx.common.AvailableMessagingTypes;
import oshai.prefx.common.Constants;
import oshai.prefx.common.DataObject;
import oshai.prefx.common.SmsType;
import oshai.prefx.phone_numbers.PhoneNumberNormalizer;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public final class SmsProviderUpdaterActivity extends PrefxActivity
{

private static final String TAG = SmsProviderUpdaterActivity.class.getSimpleName();
@Inject private IDataAdapter dataAdapter;
@Inject private IHttpDataAdapter httpDataAdapter;
@Inject private AvailableMessagingTypes smsTypes;
@Inject private PhoneNumberNormalizer phoneNumberNormalizer;

@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showMyDialog();
	}
private void showMyDialog() {
	final List<SmsType> values = smsTypes.values();
	final CharSequence[] items = new CharSequence[values.size()];
	for (int i = 0; i < values.size(); i++) {
		items[i] = values.get(i).asString();
	}
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setTitle("Pick a messaging provider");
	builder.setItems(items, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int item) {
	    	Log.i(TAG, "selected " + values.get(item));
	        saveAndForward(values.get(item));
	        dialog.dismiss();
	    }
	});
	AlertDialog alert = builder.create();
	alert.setOnDismissListener(new OnDismissListener() {
		@Override
		public void onDismiss(DialogInterface dialog) {
			finish();
		}
	} );
	alert.show();
}
private void saveAndForward(SmsType smsType) {
	try {
		saveToFile(smsType);
	} catch (Exception ex) {
		Log.w(TAG, ex);
	}
	forwardToForwardActivity(smsType);
}
private void saveToFile(SmsType smsType) {
	DataObject data = new DataObject();
	data.phone = getIntent().getExtras().getString(Constants.INTENT_PARAM_PHONE_FORWARD);
	data.key = phoneNumberNormalizer.getNumberAsKey(data.phone);
	data.smsType = smsType.toString();
	dataAdapter.putD(data);
	SharedPreferences settings = getSharedPreferences(Constants.PREFERENCE_STORE, 0);
	String prefix = settings.getString(Constants.PREFERENCE_COUNTRY_PREFIX, null);
	String phone = settings.getString(Constants.PREFERENCE_PHONE_NUMBER, null);
	String normalizedNumberWithCountryPrefix = phoneNumberNormalizer.getNormalizedNumberWithCountryPrefix(data.phone);
	String smsType2 = data.smsType;
	String updater = prefix + phone;
	sendToServer(normalizedNumberWithCountryPrefix, smsType2, updater);
}
private void sendToServer(final String normalizedNumberWithCountryPrefix, final String smsType2, final String updater) {
	RunnableTask task = new RunnableTask(new Runnable(){
		@Override
		public void run() {
		    boolean success = false;
		    long sleepTime = 1000;
		    while (!success)
			try {
			    Log.i(TAG, "sendToServer going to sleep " + sleepTime);
			    Thread.sleep(sleepTime);
			    sleepTime *= 2;
			    httpDataAdapter.put(normalizedNumberWithCountryPrefix, smsType2, updater);
			    success = true;
			} catch (Exception e) {
			    Log.w(TAG, "error on sendToServer()", e);
			}
		    Log.i(TAG, "sendToServer success");
		}});
	task.execute();
}
private void forwardToForwardActivity(SmsType smsType) {
	Intent newIntent = new Intent(getIntent());
	newIntent.setClass(this.getApplicationContext(), SmsForwardActivity.class);
	newIntent.putExtra(Constants.INTENT_PARAM_SMS_TYPE, smsType);
	startActivity(newIntent);
}

}
