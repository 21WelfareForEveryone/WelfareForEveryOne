package com.example.tts_tave;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

    private EditText edit_readText;

    private Button btn_speech;



    private TextToSpeech tts;



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



        edit_readText = (EditText) findViewById(R.id.edit_readText);

        btn_speech = (Button) findViewById(R.id.btn_speech);

        btn_speech.setEnabled(false);

        btn_speech.setOnClickListener(this);



        tts = new TextToSpeech(this, this);

    }



    // 글자 읽어주기

    private void Speech() {

        String text = edit_readText.getText().toString().trim();

        tts.setPitch((float) 0.1);      // 음량

        tts.setSpeechRate((float) 1.0); // 재생속도

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

    }



    @Override

    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            // 작업 성공



            int language = tts.setLanguage(Locale.KOREAN);  // 언어 설정

            if (language == TextToSpeech.LANG_MISSING_DATA

                    || language == TextToSpeech.LANG_NOT_SUPPORTED) {

                // 언어 데이터가 없거나, 지원하지 않는경우

                btn_speech.setEnabled(false);

                Toast.makeText(this, "지원하지 않는 언어입니다.", Toast.LENGTH_SHORT).show();

            } else {

                // 준비 완료

                btn_speech.setEnabled(true);

            }



        } else {

            // 작업 실패

            Toast.makeText(this, "TTS 작업에 실패하였습니다.", Toast.LENGTH_SHORT).show();

        }

    }



    @Override

    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_speech:

                Speech();

                break;



            default:

                break;

        }

    }



    @Override

    protected void onDestroy() {

        if (tts != null) {

            tts.stop();

            tts.shutdown();

        }

        super.onDestroy();

    }

}

