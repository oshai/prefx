package oshai.prefx.activities;

import javax.inject.Inject;

import oshai.prefx.MyApplication;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class PrefxActivity extends Activity {

private static final String TAG = PrefxActivity.class.getSimpleName();

@Inject private GoogleAnalyticsTracker tracker;
@Inject private GoogleAnalyticsSessionManager analyticsSessionManager;

public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	Log.v(TAG, "Activity State: onCreate() " + getClass().getSimpleName());
	MyApplication.injector().injectMembers(this);
	startAnalyticsTracker();
}
private void startAnalyticsTracker() {
	analyticsSessionManager.incrementActivityCount();
}
@Override
protected void onDestroy() {
  super.onDestroy();
  analyticsSessionManager.decrementActivityCount();
}
protected void trackEvent(String category, String action, String label, int value){
	//	tracker.setDebug(true);
tracker.trackEvent(
        category,
        action,
        label,
        value);
tracker.dispatch();
}
}
