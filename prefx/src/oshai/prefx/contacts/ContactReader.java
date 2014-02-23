package oshai.prefx.contacts;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import oshai.prefx.common.ContactData;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactReader implements IContactReader{
@Inject private Provider<ContactData> contactDataProvider;
@Inject private ContentResolver cr;
/**
 * http://www.coderzheaven.com/2011/06/13/get-all-details-from-contacts-in-android/
 */
public List<ContactData> readContacts(String selection){
    List<ContactData> cd = new ArrayList<ContactData>();
               Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            		   new String[]{ContactsContract.CommonDataKinds.Phone._ID,ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,ContactsContract.CommonDataKinds.Phone.PHOTO_ID,ContactsContract.CommonDataKinds.Phone.PHOTO_URI,ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI},
            		   selection,
                                      null, 
                                      ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY);
               while (pCur.moveToNext()) {
                     String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                     String id = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                     String name = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));
                     String photoId = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID));
                     String photoUri = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                     String photoTumbUri = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                    	 ContactData cd1 = contactDataProvider.get();
                    	 cd1.id = id;
                    	 cd1.name = name;
                    	 cd1.phone = phone;
                    	 cd1.photoId = photoId;
                    	 cd1.photoUri = photoUri;
                    	 cd1.photoTumbUri = photoTumbUri;
                    	 cd.add(cd1);
               }
               pCur.close();
    return cd;
}
}