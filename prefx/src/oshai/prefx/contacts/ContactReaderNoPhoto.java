package oshai.prefx.contacts;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import oshai.prefx.common.ContactData;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactReaderNoPhoto implements IContactReader{
@Inject private Provider<ContactData> contactDataProvider;
@Inject private ContentResolver cr;
/**
 * http://www.coderzheaven.com/2011/06/13/get-all-details-from-contacts-in-android/
 */
public List<ContactData> readContacts(String selection){
    List<ContactData> cd = new ArrayList<ContactData>();
               Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            		   new String[]{ContactsContract.CommonDataKinds.Phone._ID,ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY},
            		   selection,
                                      null, 
                                      ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY);
               while (pCur.moveToNext()) {
                     String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                     String id = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                     String name = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));
                    	 ContactData cd1 = contactDataProvider.get();
                    	 cd1.id = id;
                    	 cd1.name = name;
                    	 cd1.phone = phone;
                    	 cd.add(cd1);
               }
               pCur.close();
    return cd;
}
}