package org.oldcask.kannada4android.processing;

import java.io.InputStream;

import org.oldcask.kannada4android.activity.Kannada4Android;
import org.oldcask.kannada4android.ocr.IOpticalCharacterRecognizer;


import android.os.AsyncTask;

public final class TrainerDataLoaderTask extends
		AsyncTask<IOpticalCharacterRecognizer, Integer, Integer> {
	
	private final Kannada4Android activity;
	private final InputStream trainingData;

	public TrainerDataLoaderTask(Kannada4Android activity, InputStream trainingData) {
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