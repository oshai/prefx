package oshai.prefx.common;

import org.acra.ErrorReporter;

import oshai.prefx.MyApplication;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ErrorReporting {
	
	private String DEFAULT_MESSAGE = "prefx encountered an error";

	public void handle(Exception e) {
		handle(DEFAULT_MESSAGE, e);
	}

	public void handleSilent(Exception e) {
		handleSilent(DEFAULT_MESSAGE, e);
	}
	public void handleSilent(String message, Exception e) {
		ErrorReporter.getInstance().handleSilentException(e);
	}
	public void handle(String message, Exception e) {
		ErrorReporter.getInstance().handleException(e);
		updateUserLong(message);
	}

	public void updateUserLong(final String message) {
		Handler mHandler = new Handler(Looper.getMainLooper());
	       mHandler.post(new Runnable() {
	          public void run() {
	        	  Toast.makeText(MyApplication.getAppContext(),
	        			  message,
	        			  Toast.LENGTH_LONG).show();
	          }
	       });
	}
}
