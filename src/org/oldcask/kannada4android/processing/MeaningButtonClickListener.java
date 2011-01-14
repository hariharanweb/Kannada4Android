package org.oldcask.kannada4android.processing;

import org.oldcask.kannada4android.activity.ResultActivity;

import android.view.View;
import android.widget.TextView;

public class MeaningButtonClickListener implements View.OnClickListener {
	
	private final ResultActivity resultActivity;
	private final TextView literalTranslation;

	public MeaningButtonClickListener(ResultActivity resultActivity, TextView literalTranslation) {
		this.resultActivity = resultActivity;
		this.literalTranslation = literalTranslation;
	}

	public void onClick(View v) {
		KannadaMeaningFinderTask meaningFinderTask = new KannadaMeaningFinderTask(resultActivity);
		meaningFinderTask.execute(literalTranslation.getText().toString());
	}
}