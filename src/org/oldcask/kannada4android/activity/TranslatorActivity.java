package org.oldcask.kannada4android.activity;


import org.oldcask.kannada4android.processing.SearchKannadaWordListener;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

public class TranslatorActivity extends Activity
{
    private TextToSpeech textToSpeech;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate);

        Button findMeaningButton = (Button) findViewById(R.id.findMeaning);
        findMeaningButton.setOnClickListener(new SearchKannadaWordListener(this));

    }

    @Override
    protected void onDestroy() {
        textToSpeech.shutdown();
        super.onDestroy();
    }

    public void readText(){
        final EditText meaningBox = (EditText) findViewById(R.id.meaning);
        TextToSpeech.OnInitListener ttsListener = new TextToSpeech.OnInitListener() {
            public void onInit(int i) {
            	if(i == TextToSpeech.SUCCESS){
            		textToSpeech.speak(meaningBox.getText().toString(), TextToSpeech.QUEUE_ADD, null);
            	}else{
            		Log.e("Kannda4Android", "Could not initialise TextToSpeech Engine");
            	}
                
            }
        };
        textToSpeech = new TextToSpeech(getApplicationContext(), ttsListener);
    }
}
