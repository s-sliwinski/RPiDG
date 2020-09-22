package com.example.sensehat;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

public class configActivity extends AppCompatActivity {

    /* BEGIN config textboxes */
    EditText ipEditText;
    EditText sampleTimeEditText;
    EditText maxSamplesEditText;
    EditText portNumberEditText;
    /* END config textboxes */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        // get the Intent that started this Activity
        Intent intent = getIntent();

        // get the Bundle that stores the data of this Activity
        Bundle configBundle = intent.getExtras();

        ipEditText = findViewById(R.id.ipEditTextConfig);
        String ip = configBundle.getString(COMMON.CONFIG_IP_ADDRESS, COMMON.DEFAULT_IP_ADDRESS);
        ipEditText.setText(ip);

        sampleTimeEditText = findViewById(R.id.sampleTimeEditTextConfig);
        int st = configBundle.getInt(COMMON.CONFIG_SAMPLE_TIME, COMMON.DEFAULT_SAMPLE_TIME);
        sampleTimeEditText.setText(Integer.toString(st));

        maxSamplesEditText=findViewById(R.id.maxSamplesEditTextConfig);
        int maxSamples=configBundle.getInt(COMMON.CONFIG_MAX_SAMPLES,COMMON.DEFAULT_MAX_SAMPLES);
        maxSamplesEditText.setText(Integer.toString(maxSamples));


        portNumberEditText=findViewById(R.id.portNumberEditTextConfig);
        int portNumber=configBundle.getInt(COMMON.CONFIG_PORT_NUMBER,COMMON.DEFAULT_PORT_NUMBER);
        portNumberEditText.setText(Integer.toString(portNumber));
    }

    @Override
    public void onBackPressed() {
       /*Intent intent = new Intent();
        intent.putExtra(COMMON.CONFIG_IP_ADDRESS, ipEditText.getText().toString());
        intent.putExtra(COMMON.CONFIG_SAMPLE_TIME, sampleTimeEditText.getText().toString());
        intent.putExtra(COMMON.CONFIG_MAX_SAMPLES, maxSamplesEditText.getText().toString());
        setResult(RESULT_OK, intent);*/
        COMMON.DEFAULT_IP_ADDRESS=ipEditText.getText().toString();
        COMMON.DEFAULT_SAMPLE_TIME=Integer.parseInt(sampleTimeEditText.getText().toString());
        COMMON.DEFAULT_MAX_SAMPLES=Integer.parseInt(maxSamplesEditText.getText().toString());
        COMMON.DEFAULT_PORT_NUMBER=Integer.parseInt(portNumberEditText.getText().toString());

        finish();
    }
}
