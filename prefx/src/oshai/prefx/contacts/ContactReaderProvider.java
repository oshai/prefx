package oshai.prefx.contacts;

import javax.inject.Inject;
import javax.inject.Provider;

import android.content.ContentResolver;
import android.provider.ContactsContract;
import android.util.Log;


public class ContactReaderProvider implements Provider<IContactReader>{

private static final String TAG = ContactReaderProvider.class.getSimpleName();

@Inject private ContentResolver cr;
@Inject private Provider<ContactReader> contactReaderProvider;
@Inject private Provider<ContactReaderNoPhoto> contactReaderNoPhotoProvider;
	@Override
	public IContactReader get() {
		try {
			cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					new String[]{ContactsContract.CommonDataKinds.Phone._ID,ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,ContactsContract.CommonDataKinds.Phone.PHOTO_ID,ContactsContract.CommonDataKinds.Phone.PHOTO_URI,ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI},
					ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL",
			        null, 
			        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY);
			return contactReaderProvider.get();
//			return contactReaderNoPhotoProvider.get();
		} catch (Exception e) {
			Log.w(TAG, e);
			return contactReaderNoPhotoProvider.get();
		}
	}

}
