package org.oldcask.kannada4android.processing;


import org.oldcask.kannada4android.activity.R;
import org.oldcask.kannada4android.activity.TranslatorActivity;
import org.oldcask.kannada4android.http.KannadaMeaningFinder;

import android.view.View;
import android.widget.EditText;


public class SearchKannadaWordListener implements View.OnClickListener {

    private TranslatorActivity myActivity;

    public SearchKannadaWordListener(TranslatorActivity myActivity) {
        this.myActivity = myActivity;
    }

    public void onClick(View view) {
        EditText kannadaTextBox = (EditText) myActivity.findViewById(R.id.kannadaWord);
        String kannadaText = kannadaTextBox.getText().toString();
        KannadaMeaningFinder meaningFinder = new KannadaMeaningFinder(kannadaText);
        EditText meaningBox = (EditText) myActivity.findViewById(R.id.meaning);
        try {
            meaningBox.setText(meaningFinder.getKannadaMeaning());
        } catch (Exception e) {
            meaningBox.setText("Could not find meaning for " + kannadaText);
        }

        myActivity.readText();
    }
}
