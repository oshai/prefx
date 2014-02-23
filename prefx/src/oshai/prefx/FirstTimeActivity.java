package oshai.prefx;

import javax.inject.Inject;

import oshai.prefx.activities.PrefxActivity;
import oshai.prefx.adapters.IHttpDataAdapter;
import oshai.prefx.async_tasks.RunnableTask;
import oshai.prefx.common.SmsType;
import oshai.prefx.common.UserEmailFetcher;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FirstTimeActivity extends PrefxActivity {

private static final String TAG = FirstTimeActivity.class.getSimpleName();
@Inject private IHttpDataAdapter httpDataAdapter;

@Override
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	Log.v(TAG, "Activity State: onCreate()");
	initComponents();
	sendToServer("972","972");
}

private void initComponents() {
	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	setContentView(R.layout.welcome);
	addListenerOnSubmitButton();
}

private void sendToServer(String countryCode, String phoneNumber) {
	final String phone = countryCode + phoneNumber;
	final String smsTypeString = SmsType.DeviceBuiltIn.toString();
	final String updater = "registration_of_" + UserEmailFetcher.getEmail(getApplicationContext());
	RunnableTask task = new RunnableTask(new Runnable(){
		@Override
		public void run() {
			try {
			    httpDataAdapter.put(phone, smsTypeString, updater );
			} catch (Exception e) {
			    Log.w(TAG, "error on logVersionAtServer()", e);
			}
		}});
	task.execute();
}

private void submitButtonOnClick() {
	Intent newIntent = new Intent(FirstTimeActivity.this.getApplicationContext(), RegistrationActivity.class);
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
