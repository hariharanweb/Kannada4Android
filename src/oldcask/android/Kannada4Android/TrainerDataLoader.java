package oldcask.android.Kannada4Android;

import oldcask.android.Kannada4Android.interfaces.IOpticalCharacterRecognizer;
import android.os.AsyncTask;

public final class TrainerDataLoader extends
		AsyncTask<IOpticalCharacterRecognizer, Integer, Integer> {
	
	private final Kannada4Android activity;

	public TrainerDataLoader(Kannada4Android activity) {
		this.activity = activity;
	}
	
	protected Integer doInBackground(IOpticalCharacterRecognizer... ocr) {
		ocr[0].trainNetwork();
		return null;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		activity.setContinueButtonVisible();
	}
}