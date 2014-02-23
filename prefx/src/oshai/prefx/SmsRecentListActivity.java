package oshai.prefx;

import oshai.prefx.common.Constants;
import android.content.SharedPreferences;
import android.provider.ContactsContract;


public class SmsRecentListActivity extends SmsListActivity {
private static final String TAG = SmsRecentListActivity.class.getSimpleName();

protected String getPhoneSelection() {
    String selection = "";
    SharedPreferences settings = getSharedPreferences(Constants.PREFERENCE_STORE, 0);
    for (int i = 0; i < Constants.RECENT_NUMBER; i++) {
	String phone = settings.getString(Constants.PREFERENCE_RECENT_PHONE + i, null);
	if (null != phone){
	    if (i > 0){
		selection += " OR ";
	    }
	    selection += ContactsContract.CommonDataKinds.Phone.NUMBER + "=='" + phone + "'";
	}
    }
    if ("".equals(selection)){
	selection = ContactsContract.CommonDataKinds.Phone.NUMBER + "=='blablablaqqq'";
    }
    return selection ;
}
protected int getViewId() {
    return R.layout.phone_recent_list;
}
protected void initSearchEdit() {
}
}
