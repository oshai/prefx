package oshai.prefx.common;

import android.provider.ContactsContract;

public class Constants {
	public static final String PREFERENCE_COUNTRY_PREFIX = "PREFERENCE_COUNTRY_PREFIX";
	public static final String PREFERENCE_STORE = "PREFERENCE_STORE";
	public static final String PREFERENCE_PHONE_NUMBER = "PREFERENCE_PHONE_NUMBER";
	public static final String PREFERENCE_PREFERED_SMS_APPLICATION = "PREFERENCE_PREFERED_SMS_APPLICATION";
	public static final String PREFERENCE_APP_VERSION = "PREFERENCE_APP_VERSION";
	public static final String PREFERENCE_RECENT_PHONE = "PREFERENCE_RECENT_PHONE";
	
	public static boolean DEVELOPMENT = false;
	public static final boolean ALWAYS_OPEN_REGISTRATION = false;
//	public static boolean DELETE_DATA = DEVELOPMENT && false;
	
	public static final String INTENT_PARAM_PHONE_FORWARD = "oshai.prefx.param_phone_forwarding";
	public static final String INTENT_PARAM_ORIGINAL_INTENT = "oshai.prefx.param_originalIntent";
	public static final String INTENT_PARAM_SMS_TYPE = "oshai.prefx.param_smsType";
	
	
	public static final String BROADCAST_CONTACT_REFRESH = "oshai.prefx.contact_refresh";
	
	public static final String LINK_TO_APP = "https://play.google.com/store/apps/details?id=oshai.prefx";
	
	public static final String SELECTION_ALL_PHONE = ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL";
	public static final int RECENT_NUMBER = 15;
}
