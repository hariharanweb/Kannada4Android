package com.ocrapp;

import oldcask.android.Kannada4Android.R;
import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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
                textToSpeech.speak(meaningBox.getText().toString(), TextToSpeech.QUEUE_ADD, null);
            }
        };
        textToSpeech = new TextToSpeech(getApplicationContext(), ttsListener);
    }
}
