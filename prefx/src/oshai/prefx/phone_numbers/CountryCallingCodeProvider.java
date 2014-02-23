package oshai.prefx.phone_numbers;

import oshai.prefx.MyApplication;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CountryCallingCodeProvider {
private static final String TAG = CountryCallingCodeProvider.class.getSimpleName();

public String getCountryCallingCode() {
	TelephonyManager tm = (TelephonyManager) MyApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
	String countryCode = tm.getSimCountryIso();
	Log.i(TAG, "getCountryCallingCode() - countryCode " + countryCode);
	return countryCode;
}
}
