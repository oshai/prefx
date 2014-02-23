package oshai.prefx.tabs;

import oshai.prefx.R;
import oshai.prefx.SmsListActivity;
import oshai.prefx.SmsPrefxOnlyListActivity;
import oshai.prefx.SmsRecentListActivity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
 
@SuppressWarnings("deprecation")
public class AndroidTabLayoutActivity extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs_main);
 
        TabHost tabHost = getTabHost();
 
        TabSpec photospec = tabHost.newTabSpec("Recent");
        photospec.setIndicator("Recent");
        Intent photosIntent = new Intent(this, SmsRecentListActivity.class);
        photospec.setContent(photosIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
 
        // Adding all TabSpec to TabHost
        tabHost.addTab(photospec);
        tabHost.addTab(createTabSpec(tabHost, "All contacts", SmsListActivity.class));
        tabHost.addTab(createTabSpec(tabHost, "Prefx contacts", SmsPrefxOnlyListActivity.class));
    }

    private TabSpec createTabSpec(TabHost tabHost, String name, Class<?> class1) {
	TabSpec songspec = tabHost.newTabSpec(name);
        songspec.setIndicator(name);
        Intent songsIntent = new Intent(this, class1);
        songspec.setContent(songsIntent);
	return songspec;
    }
}
