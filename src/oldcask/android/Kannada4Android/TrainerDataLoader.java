package oldcask.android.Kannada4Android;

import java.io.InputStream;

import oldcask.android.Kannada4Android.ocr.IOpticalCharacterRecognizer;
import android.os.AsyncTask;

public final class TrainerDataLoader extends
		AsyncTask<IOpticalCharacterRecognizer, Integer, Integer> {
	
	private final Kannada4Android activity;
	private final InputStream trainingData;

	public TrainerDataLoader(Kannada4Android activity, InputStream trainingData) {
		this.activity = activity;
		this.trainingData = trainingData;
	}
	
	protected Integer doInBackground(IOpticalCharacterRecognizer... ocr) {
		ocr[0].trainNeuralNetwork(trainingData);
		return 100;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		activity.onTraningComplete();
	}
}