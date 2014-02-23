package oshai.prefx;

import javax.inject.Inject;
import javax.inject.Provider;

import oshai.prefx.activities.GoogleAnalyticsSessionManager;
import oshai.prefx.adapters.FileSystemDataAdapter;
import oshai.prefx.adapters.IDataAdapter;
import oshai.prefx.adapters.IHttpDataAdapter;
import oshai.prefx.adapters.NoOpHttpDataAdapter;
import oshai.prefx.common.AvailableMessagingTypes;
import oshai.prefx.common.Constants;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

public class MainModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IDataAdapter.class).to(FileSystemDataAdapter.class);
		bind(GoogleAnalyticsSessionManager.class).in(Scopes.SINGLETON);
		bind(AvailableMessagingTypes.class).in(Scopes.SINGLETON);
		bind(IHttpDataAdapter.class).to(NoOpHttpDataAdapter.class);
		bind(SharedPreferences.class).toProvider(SharedPreferencesProvider.class);
		bind(Application.class).toProvider(ContextProvider.class);
		bind(Context.class).toProvider(ContextProvider.class);
		bind(GoogleAnalyticsTracker.class).toProvider(GoogleAnalyticsTrackerProvider.class);
		bind(String.class).annotatedWith(Names.named("countryPrefix")).toProvider(CountryPrefixProvider.class);
		bind(ContentResolver.class).toProvider(ContentResolverProvider.class);
	}

	public static class GoogleAnalyticsTrackerProvider implements Provider<GoogleAnalyticsTracker>{
		@Override
		public GoogleAnalyticsTracker get() {
			return GoogleAnalyticsTracker.getInstance();
		}
	}
	public static class SharedPreferencesProvider implements Provider<SharedPreferences>{
		private Context context;
		
		@Inject
		public SharedPreferencesProvider(Context context) {
			super();
			this.context = context;
		}

		@Override
		public SharedPreferences get() {
			return context.getSharedPreferences(Constants.PREFERENCE_STORE, 0);
		}
	
	}
	public static class ContentResolverProvider implements Provider<ContentResolver>{
	private Context context;
	
	@Inject
	public ContentResolverProvider(Context context) {
		super();
		this.context = context;
	}

	@Override
	public ContentResolver get() {
		return context.getContentResolver();
	}

}
	public static class CountryPrefixProvider implements Provider<String>{
	private SharedPreferences settings;
	
	@Inject
	public CountryPrefixProvider(SharedPreferences settings) {
		super();
		this.settings = settings;
	}

	@Override
	public String get() {
		return settings.getString(Constants.PREFERENCE_COUNTRY_PREFIX, null);
	}

}
	public static class ContextProvider implements Provider<Application>{
	@Override
	public Application get() {
		return MyApplication.getAppContext();
	}

}
}
