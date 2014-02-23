package oshai.prefx;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import oshai.prefx.contacts.ContactReaderModule;
import android.app.Application;

import com.google.inject.Guice;
import com.google.inject.Injector;

@ReportsCrashes(formKey="dGJGQ01HN0RHbVFpVnpPWlVxQnBGVGc6MQ",
mode = ReportingInteractionMode.NOTIFICATION,
resToastText = R.string.crash_toast_text, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
resNotifTickerText = R.string.crash_notif_ticker_text,
resNotifTitle = R.string.crash_notif_title,
resNotifText = R.string.crash_notif_text,
resNotifIcon = android.R.drawable.stat_notify_error, // optional. default is a warning sign
resDialogText = R.string.crash_dialog_text,
resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. when defined, adds a user text field input with this text resource as a label
resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.
)
public class MyApplication extends Application{

private static MyApplication instance;
private Injector injector;

public void onCreate(){
    ACRA.init(this);
    super.onCreate();
    instance = this;
    injector = Guice.createInjector(
    		new MainModule(),
    		new ContactReaderModule());
}
public static MyApplication instance(){
	return instance;
}
public static Injector injector(){
	return instance().injector;
}
public static Application getAppContext() {
    return instance();
}
}
