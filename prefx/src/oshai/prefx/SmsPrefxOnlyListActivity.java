package oshai.prefx;

import java.util.List;

import javax.inject.Inject;

import oshai.prefx.adapters.IDataAdapter;
import android.provider.ContactsContract;


public class SmsPrefxOnlyListActivity extends SmsListActivity {
private static final String TAG = SmsPrefxOnlyListActivity.class.getSimpleName();
@Inject private IDataAdapter dataAdapter;

protected String getPhoneSelection() {
    List<String> allPhones = dataAdapter.getAllPhones();
    String selection = "";
    boolean first = true;
    for (String phone : allPhones) {
	    if (!first){
		selection += " OR ";
	    }
	    else {
		first = false;
	    }
//	    DataObject d = dataAdapter.getD(phone);
	    selection += ContactsContract.CommonDataKinds.Phone.NUMBER + "=='" + phone + "'";
    }
    if (allPhones.isEmpty()){
	selection = ContactsContract.CommonDataKinds.Phone.NUMBER + "=='blablablaqqq'";
    }
    return selection ;
}
protected int getViewId() {
    return R.layout.phone_prefx_only_list;
}

}
