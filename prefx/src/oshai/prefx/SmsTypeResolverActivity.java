package oshai.prefx;

import oshai.prefx.activities.PrefxActivity;
import oshai.prefx.adapters.IDataAdapter;
import oshai.prefx.common.Constants;
import oshai.prefx.common.DataObject;
import oshai.prefx.common.SmsType;
import oshai.prefx.phone_numbers.PhoneNumberNormalizer;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.inject.Inject;

public class SmsTypeResolverActivity extends PrefxActivity {

    private static final String TAG = SmsTypeResolverActivity.class
	    .getSimpleName();
    @Inject
    private IDataAdapter dataAdapter;
    @Inject
    private PhoneNumberNormalizer phoneNumberNormalizer;
    private SmsType smsType;

    @Override
    protected void onResume() {
	super.onResume();
	smsType = getSmsTypeFromData();
	if (null == smsType) {
	    updateUserLong("no provider found, please select once");
	    forwardToUpdaterActivity();
	} else {
	    forwardToForwardActivity();
	}
	finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
    }

    private void forwardToUpdaterActivity() {
	Intent newIntent = new Intent(this.getApplicationContext(),
		SmsProviderUpdaterActivity.class);
	newIntent.putExtra(Constants.INTENT_PARAM_PHONE_FORWARD,
		phoneNumberNormalizer.removeSmsto(getDataString()));
	newIntent.putExtra(Constants.INTENT_PARAM_ORIGINAL_INTENT, getIntent());
	startActivity(newIntent);
    }

    private void forwardToForwardActivity() {
	Intent newIntent = new Intent();
	newIntent.setClass(this.getApplicationContext(),
		SmsForwardActivity.class);
	newIntent.putExtra(Constants.INTENT_PARAM_SMS_TYPE, smsType);
	newIntent.putExtra(Constants.INTENT_PARAM_ORIGINAL_INTENT, getIntent());
	startActivity(newIntent);
    }

    private SmsType getSmsTypeFromData() {
	try {
	    DataObject dataObject = dataAdapter.getD(phoneNumberNormalizer
		    .getNumberAsKey(getDataString()));
	    if (null != dataObject) {
		if (null != dataObject.smsType) {
		    return SmsType.valueOf(dataObject.smsType.trim());
		}
		return SmsType.valueOf(dataObject.smsTypeFromServer.trim());
	    }
	} catch (Exception e) {
	    Log.w(TAG, "exception when looking for provider ", e);
	}
	return null;
    }

    private void updateUserLong(String message) {
	Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private String getDataString() {
	Log.i(TAG, "getDataString() " + getIntent().getDataString());
	return getIntent().getDataString();
    }
}
