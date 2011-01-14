package org.oldcask.kannada4android.processing;

import java.io.IOException;

import org.oldcask.kannada4android.activity.ResultActivity;
import org.oldcask.kannada4android.http.KannadaMeaningFinder;

import android.os.AsyncTask;

final class KannadaMeaningFinderTask extends
		AsyncTask<String, Integer, String> {
	
	private final ResultActivity resultActivity;

	public KannadaMeaningFinderTask(ResultActivity resultActivity) {
		this.resultActivity = resultActivity;
	}

	protected String doInBackground(String... data) {
		KannadaMeaningFinder kannadaMeaningFinderService = new KannadaMeaningFinder(data[0]);
		try {
			return kannadaMeaningFinderService.getKannadaMeaning();
		} catch (IOException e) {
			return "Unable to get the meaning or the internet connection is down"; 
		}
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		resultActivity.showMeaning("Meanings :"+result);
	}
}