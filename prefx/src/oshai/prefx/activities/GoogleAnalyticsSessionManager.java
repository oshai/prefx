package oshai.prefx.activities;

import javax.inject.Inject;

import android.app.Application;
import android.content.Context;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class GoogleAnalyticsSessionManager {

	private static final String ANALYTICS_KEY = "UA-26322608-2";
	protected int activityCount = 0;
    protected Integer dispatchIntervalSecs;
    protected String apiKey;
    protected Context context;
    private final GoogleAnalyticsTracker tracker;

    /**
     * NOTE: you should use your Application context, not your Activity context, in order to avoid memory leaks.
     */
    @Inject
    public GoogleAnalyticsSessionManager(Application context, GoogleAnalyticsTracker tracker ) {
        this.apiKey = ANALYTICS_KEY;
        this.context = context;
        this.tracker = tracker;
    }

    /**
     * NOTE: you should use your Application context, not your Activity context, in order to avoid memory leaks.
     */
//    protected GoogleAnalyticsSessionManager( String apiKey, int dispatchIntervalSecs, Application context ) {
//        this.apiKey = apiKey;
//        this.dispatchIntervalSecs = dispatchIntervalSecs;
//        this.context = context;
//    }

    /**
     * This should be called once in onCreate() for each of your activities that use GoogleAnalytics.
     * These methods are not synchronized and don't generally need to be, so if you want to do anything
     * unusual you should synchronize them yourself.
     */
    public void incrementActivityCount() {
        if( activityCount==0 )
            if( dispatchIntervalSecs==null )
                tracker.startNewSession(apiKey,context);
            else
                tracker.startNewSession(apiKey,dispatchIntervalSecs,context);

        ++activityCount;
    }


    /**
     * This should be called once in onDestrkg() for each of your activities that use GoogleAnalytics.
     * These methods are not synchronized and don't generally need to be, so if you want to do anything
     * unusual you should synchronize them yourself.
     */
    public void decrementActivityCount() {
        activityCount = Math.max(activityCount-1, 0);

        if( activityCount==0 )
            tracker.stopSession();
    }

}