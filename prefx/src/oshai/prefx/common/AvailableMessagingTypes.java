package oshai.prefx.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class AvailableMessagingTypes {

private static final String TAG = AvailableMessagingTypes.class.getSimpleName();

	private final List<SmsType> smsTypes;
	private Context context;
	
	@Inject
	public AvailableMessagingTypes(Context context){
		this.context = context;
		smsTypes = calculateAvailableTypes();
	}
	private List<SmsType> calculateAvailableTypes() {
		ArrayList<SmsType> $ = new ArrayList<SmsType>();
		for (SmsType smsType : SmsType.values()) {
			try {
				if (smsType != SmsType.DeviceBuiltIn) {
					context.getPackageManager().getPackageInfo(smsType.packageName(),0);
				}
				Log.i(TAG, "app added " + smsType);
				$.add(smsType);
			} catch (NameNotFoundException e) {
				Log.i(TAG, "app not found " + smsType);
			}
		}
		return Collections.unmodifiableList($);
	}

	public List<SmsType> values() {
		return smsTypes;
	}
}
