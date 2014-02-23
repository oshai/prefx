package oshai.prefx;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.inject.Inject;

import oshai.prefx.activities.PrefxListActivity;
import oshai.prefx.adapters.IDataAdapter;
import oshai.prefx.adapters.IHttpDataAdapter;
import oshai.prefx.common.Constants;
import oshai.prefx.common.ContactData;
import oshai.prefx.common.ErrorReporting;
import oshai.prefx.common.UserEmailFetcher;
import oshai.prefx.common.Utils;
import oshai.prefx.contacts.IContactReader;
import oshai.prefx.phone_numbers.PhoneNumberNormalizer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.LruCache;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SmsListActivity extends PrefxListActivity {
private static final String TAG = SmsListActivity.class.getSimpleName();

@Inject
private IContactReader contactReader;
@Inject
private ContentResolver cr;
private LayoutInflater inflater;
private Bitmap DEFAULT_ICON;
private List<ContactData> contacts;
private EditText edt;
private CustomAdapter adapter;
@Inject private IHttpDataAdapter httpDataAdapter;
@Inject private IDataAdapter dataAdapter;
@Inject private PhoneNumberNormalizer phoneNumberNormalizer;
private ContactUpdateTask updateTask;

@Override
protected void onResume() {
	super.onResume();
	if (forwardToRegistrationIfRequired()) {
		return;
	}
	logAtServer();
	trackAppResumedEvent();
}

private void logAtServer() {
	try {
	    SharedPreferences settings = getSharedPreferences(Constants.PREFERENCE_STORE, 0);
	    String prevVersion = settings.getString(Constants.PREFERENCE_APP_VERSION, null);
	    int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
	    if (!String.valueOf(versionCode).equals(prevVersion)){
		logVersionAtServer("upgrade from " + prevVersion + " to " + String.valueOf(versionCode));
		Editor editor = settings.edit();
		editor.putString(Constants.PREFERENCE_APP_VERSION, String.valueOf(versionCode));
		editor.commit();
	    }
	} catch (Exception e) {
	    new ErrorReporting().handle(e);
	}
}

private void logVersionAtServer(final String message) {
    new Thread(){public void run() {
	try {
	    SharedPreferences settings = getSharedPreferences(Constants.PREFERENCE_STORE, 0);
	    String prefix = settings.getString(Constants.PREFERENCE_COUNTRY_PREFIX, null);
	    String phone = settings.getString(Constants.PREFERENCE_PHONE_NUMBER, null);
	    httpDataAdapter.log(UserEmailFetcher.getEmail(getApplicationContext()), prefix + "-" + phone, message);
	} catch (Exception e) {
	    Log.w(TAG, "error on logVersionAtServer()", e);
	}	
    };}.start();
}

private void trackAppResumedEvent() {
	SharedPreferences settings = getSharedPreferences(Constants.PREFERENCE_STORE, 0);
	String prefix = settings.getString(Constants.PREFERENCE_COUNTRY_PREFIX, null);
	String phone = settings.getString(Constants.PREFERENCE_PHONE_NUMBER, null);
	trackEvent("ListActivityResumed", prefix + "-" + phone, "", 1);
}

@Override
protected void onListItemClick(ListView l, View v, int position, long id) {
	super.onListItemClick(l, v, position, id);
	Intent newIntent = new Intent();
	newIntent.setClass(this.getApplicationContext(), SmsTypeResolverActivity.class);
	ContactData cd = (ContactData) l.getItemAtPosition(position);
	updateRecent(cd.phone);
	newIntent.setData(Uri.parse("smsto:" + cd.phone));
	startActivity(newIntent);
}

private void updateRecent(String phone) {
    LruCache<String, String> lru = new LruCache<String, String>(Constants.RECENT_NUMBER);
    SharedPreferences settings = getSharedPreferences(Constants.PREFERENCE_STORE, 0);
    LruCache<String, String> iterator = lru;
    for (int i = Constants.RECENT_NUMBER-1; i >= 0; i--) {
	String key = settings.getString(Constants.PREFERENCE_RECENT_PHONE + (i), null);
	if (null != key){
	    iterator.put(key, key);
	}
    }
    lru.put(phone, phone);
    int i = lru.size()-1;
    Editor editor = settings.edit();
    for (Entry<String, String> entry : lru.snapshot().entrySet()) {
	editor.putString(Constants.PREFERENCE_RECENT_PHONE + i, entry.getKey());
	i--;
    }
    editor.commit();
}

public void onCreate(Bundle icicle) {
	super.onCreate(icicle);
	setContentView(getViewId());
	inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	addLongClickListener();
	contacts = contactReader.readContacts(getPhoneSelection());
	adapter = new CustomAdapter(this, R.layout.list, R.id.title, contacts);
	setListAdapter(adapter);
	DEFAULT_ICON = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.prefx_icon);
	getListView().setTextFilterEnabled(true);
	initSearchEdit();
	LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
		      new IntentFilter(Constants.BROADCAST_CONTACT_REFRESH));
}

protected void initSearchEdit() {
    edt = (EditText) findViewById(R.id.editTextSearch);
    edt.addTextChangedListener(new TextWatcher() {
    	@Override
    	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    	}

    	@Override
    	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    	}

    	@Override
    	public void afterTextChanged(Editable arg0) {
    		adapter.getFilter().filter(arg0);

    	}
    });
}

protected int getViewId() {
    return R.layout.phone_list;
}

protected String getPhoneSelection() {
    return Constants.SELECTION_ALL_PHONE;
}
private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
	  @Override
	  public void onReceive(Context context, Intent intent) {
	    // Get extra data included in the Intent
	    String message = intent.getStringExtra("message");
	    Log.d("receiver", "Got message: " + message);
	    SmsListActivity.this.refresh();
	  }
	};

	
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}
private class CustomAdapter extends ArrayAdapter<ContactData> implements Filterable {

private Filter filter;
public List<ContactData> filteredContacts = null;

public CustomAdapter(Context context, int resource, int textViewResourceId, List<ContactData> objects) {
	super(context, resource, textViewResourceId, objects);

	filteredContacts = objects;
	filter = new MyFilter();
}

public int getCount() {
	return filteredContacts.size();
}

public ContactData getItem(int position) {
	return filteredContacts.get(position);
}

public long getItemId(int position) {
	return position;
}

@Override
public Filter getFilter() {
	if (filter == null) {
		filter = new MyFilter();
	}
	return filter;
}

private class MyFilter extends Filter {

protected FilterResults performFiltering(CharSequence prefix) {
	// Initiate our results object
	FilterResults results = new FilterResults();
	// Collection<? extends Articulo> mItemsArray = null;
	// If the adapter array is empty, check the actual items array
	// and use it
	if (filteredContacts == null) {
			filteredContacts = contacts;
	}
	// No prefix is sent to filter by so we're going to send back
	// the original array
	if (prefix == null || prefix.length() < 2) {
			results.values = contacts;
			results.count = contacts.size();
	} else {
		// Compare lower case strings
		String prefixString = prefix.toString().toLowerCase();
		// Local to here so we're not changing actual array
		final List<ContactData> items = filteredContacts;
		final int count = items.size();
		final ArrayList<ContactData> newItems = new ArrayList<ContactData>();
		for (int i = 0; i < count; i++) {
			boolean match = false;
			final ContactData item = items.get(i);
			final String itemName = item.name.toString().toLowerCase();
			// First match against the whole, non-splitted value
			if (itemName.startsWith(prefixString)) {
				newItems.add(item);
				match = true;
			} else {
				// else {} // This is option and taken from the
				// source of
				// ArrayAdapter
				final String[] words = itemName.split(" ");
				final int wordCount = words.length;
				for (int k = 0; k < wordCount; k++) {
					if (words[k].startsWith(prefixString)) {
						newItems.add(item);
						match = true;
						break;
					}
				}
			}
			if (!match) {
				if (item.phone.contains(prefixString)) {
					newItems.add(item);
					match = true;
				}
			}
		}

		// Set and return
		results.values = newItems;
		results.count = newItems.size();
	}
	return results;
}

@SuppressWarnings("unchecked")
protected void publishResults(CharSequence prefix, FilterResults results) {
	// noinspection unchecked
	filteredContacts = (ArrayList<ContactData>) results.values;
	// Let the adapter know about the updated list
	if (results.count > 0) {
		notifyDataSetChanged();
	} else {
		notifyDataSetInvalidated();
	}
}

}

public Bitmap loadContactPhoto(ContactData cd) {
	Long id = Long.parseLong(cd.id);
	Bitmap bitmap = getBitmap(id);
	if (null == bitmap && !Utils.isEmpty(cd.photoId)) {
		long photoId = Long.parseLong(cd.photoId);
		bitmap = getBitmap(photoId);
		if (null == bitmap) {
			byte[] photoBytes = null;

			Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photoId);

			Cursor c = cr.query(photoUri, new String[] { ContactsContract.CommonDataKinds.Photo.PHOTO }, null, null,
					null);

			try {
				if (c.moveToFirst())
					photoBytes = c.getBlob(0);

			} catch (Exception e) {
				new ErrorReporting().handle(e);
			} finally {

				c.close();
			}

			if (photoBytes != null) {
				bitmap = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length);
			}
		}
	}
	if (null == bitmap) {
		Log.v(TAG, "no picture found, falling to default");
		if (!Utils.isEmpty(cd.getSmsType())) {
		    bitmap = DEFAULT_ICON;
		}
		else {
		    Log.v(TAG, "user not in prefx - not showing icon"); 
		}
	}
	return bitmap;
}

private Bitmap getBitmap(Long id) {
	Bitmap bitmap = null;
	Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
	InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
	if (input != null) {
		bitmap = BitmapFactory.decodeStream(input);
	}
	return bitmap;
}

@Override
public View getView(int position, View convertView, ViewGroup parent) {
	View view = convertView;
	ViewHolder holder = null;
	TextView title = null;
	TextView detail = null;
	TextView smsTypeTextView = null;
	ImageView i11 = null;
	ContactData rowData = getItem(position);
	if (null == view) {
		view = inflater.inflate(R.layout.list, null);
		holder = new ViewHolder(view);
		view.setTag(holder);
	}
	holder = (ViewHolder) view.getTag();
	title = holder.gettitle();
	title.setText(rowData.name);
	detail = holder.getdetail();
	detail.setText(rowData.phone);
	smsTypeTextView = holder.getSmsTypeView();
	smsTypeTextView.setText(rowData.getSmsType());
	i11 = holder.getImage();
	Bitmap contactPhoto = loadContactPhoto(rowData);
	i11.setImageBitmap(contactPhoto);
	return view;
}

private class ViewHolder {
private View mRow;
private TextView title = null;
private TextView detail = null;
private TextView smsType = null;
private ImageView i11 = null;

public ViewHolder(View row) {
	mRow = row;
}

public TextView gettitle() {
	if (null == title) {
		title = (TextView) mRow.findViewById(R.id.title);
	}
	return title;
}

public TextView getSmsTypeView() {
	if (null == smsType) {
		smsType = (TextView) mRow.findViewById(R.id.sms_type);
	}
	return smsType;
}

public TextView getdetail() {
	if (null == detail) {
		detail = (TextView) mRow.findViewById(R.id.detail);
	}
	return detail;
}

public ImageView getImage() {
	if (null == i11) {
		i11 = (ImageView) mRow.findViewById(R.id.img);
	}
	return i11;
}
}

public void refreshAdapter() {
	List<ContactData> readContacts = contactReader.readContacts(getPhoneSelection());
	if (readContacts.size() != filteredContacts.size()){
		Log.i(TAG, "size not equals - assuming filtered");
		return;
	}
	contacts = readContacts;
	filteredContacts = contacts;
	notifyDataSetChanged();
}
}

private void addLongClickListener() {
	getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			showMyDialog((ContactData) getListView().getItemAtPosition(position));
			return true;
		}
	});
}

private void showMyDialog(final ContactData contactData) {
	final CharSequence[] items = { "Update sms type" };
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setItems(items, new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int item) {
			forwardToUpdaterActivity(contactData);
			dialog.dismiss();
			refresh();
		}
	});
	AlertDialog alert = builder.create();
	alert.show();
}

private void forwardToUpdaterActivity(ContactData contactData) {
	Intent newIntent = new Intent(this.getApplicationContext(), SmsProviderUpdaterActivity.class);
	newIntent.putExtra(Constants.INTENT_PARAM_PHONE_FORWARD, contactData.phone);
	newIntent.setData(Uri.parse(contactData.phone));
	startActivity(newIntent);
}

private boolean forwardToRegistrationIfRequired() {
	SharedPreferences settings = getSharedPreferences(Constants.PREFERENCE_STORE, 0);
	String prefix = settings.getString(Constants.PREFERENCE_COUNTRY_PREFIX, null);
	String phone = settings.getString(Constants.PREFERENCE_PHONE_NUMBER, null);
	if (null == prefix || null == phone || Constants.ALWAYS_OPEN_REGISTRATION) {
		openRegistrationActivity();
		return true;
	}
	return false;
}

private void openRegistrationActivity() {
	Intent newIntent = new Intent(SmsListActivity.this.getApplicationContext(), FirstTimeActivity.class);
	startActivity(newIntent);
	finish();
}

@Override
public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.layout.list_menu, menu);
	return true;
}

private void openAboutActivity() {
	Intent newIntent = new Intent(SmsListActivity.this.getApplicationContext(), AboutActivity.class);
	startActivity(newIntent);
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
	// Handle item selection
	switch (item.getItemId()) {
	case R.id.about:
		openAboutActivity();
		return true;
		// case R.id.help:
		// showHelp();
		// return true;
	case R.id.rateApp:
		openMarketActivity();
		return true;
	case R.id.logout:
		logout();
		return true;
	case R.id.refresh:
	    refreshData();
	    return true;
		// case R.id.help:
		// showHelp();
		// return true;
	default:
		return super.onOptionsItemSelected(item);
	}
}

private void refreshData() {
    if (null != updateTask && ! (updateTask.isFinished())) {
	Log.i(TAG, "task already running");
	return;
    }
    List<ContactData> contacts = contactReader.readContacts(Constants.SELECTION_ALL_PHONE);
    SharedPreferences settings = getSharedPreferences(Constants.PREFERENCE_STORE, 0);
    String prefix = settings.getString(Constants.PREFERENCE_COUNTRY_PREFIX, null);
    String phone1 = settings.getString(Constants.PREFERENCE_PHONE_NUMBER, null);
    String updater = prefix + phone1;
    updateTask = new ContactUpdateTask(contacts, updater, httpDataAdapter, dataAdapter, phoneNumberNormalizer);
    updateTask.execute();
//    refresh();
}

private void logout() {
    SharedPreferences settings = getSharedPreferences(Constants.PREFERENCE_STORE, 0);
	Editor editor = settings.edit();
	editor.remove(Constants.PREFERENCE_COUNTRY_PREFIX);
	editor.remove(Constants.PREFERENCE_PHONE_NUMBER);
	editor.remove(Constants.PREFERENCE_PREFERED_SMS_APPLICATION);
	editor.commit();
	finish();
}

private void openMarketActivity() {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(Uri.parse(Constants.LINK_TO_APP));
    startActivity(intent);
}

private void refresh() {
	adapter.refreshAdapter();
}

}
