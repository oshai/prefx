package oshai.prefx.async_tasks;

import oshai.prefx.common.ErrorReporting;
import android.os.AsyncTask;

public class RunnableTask extends AsyncTask<Void, Integer, Void>{

	private Runnable runnable;

	public RunnableTask(Runnable runnable2) {
		runnable = runnable2;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			runnable.run();
		} catch (Exception e) {
			new ErrorReporting().handleSilent(e);
		}
		return null;
	}

}
