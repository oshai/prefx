package oshai.prefx;

import oshai.prefx.activities.PrefxActivity;
import oshai.prefx.common.ErrorReporting;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends PrefxActivity {

@Override
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	initComponents();
}

private void initComponents() {
	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	setContentView(R.layout.about);
	TextView t = (TextView) findViewById(R.id.prefx_version);
	t.setText(getVersionText());
}

private CharSequence getVersionText() {
	try {
		String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		return "Version: " + versionName + "." + versionCode;
	} catch (NameNotFoundException e) {
		new ErrorReporting().handle(e);
	}
	return "";
}

}
