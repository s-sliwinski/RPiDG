package com.example.sensehat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Double.isNaN;
public class MainActivity extends AppCompatActivity {
    /* BEGIN config data */
    private String ipAddress = COMMON.DEFAULT_IP_ADDRESS;
    private int sampleTime = COMMON.DEFAULT_SAMPLE_TIME;
    private int maxSamples = COMMON.DEFAULT_MAX_SAMPLES;
    private int portNumber = COMMON.DEFAULT_PORT_NUMBER;
    /* END config data */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    /**
     * @brief Main activity button onClick procedure - common for all upper menu buttons
     * @param v the View (Button) that was clicked
     */
    public void btns_onClick(View v) {
        switch (v.getId()) {
            case R.id.measurementBtn: {
                openMeasurement();
                break;
            }
            case R.id.chartsBtn: {
                openCharts();
                break;
            }
            case R.id.angleMeasurementBtn: {
                openAngleMeasurement();
                break;
            }
            case R.id.joystickBtn: {
               openJoystick();
                break;
            }
            case R.id.ledMatrixBtn: {
               openLedMatrix();
                break;
            }
            case R.id.settingsBtn: {
               openConfig();
                break;
            }
            default: {
                // do nothing
            }
        }
    }
    /**
     * @brief Called when the user taps the 'Measurement' button.
     * */
    private void openMeasurement() {
        Intent openMeasurementIntent = new Intent(this, measurementsActivity.class);
        Bundle measurementBundle = new Bundle();
        openMeasurementIntent.putExtras(measurementBundle);
        startActivityForResult(openMeasurementIntent, COMMON.REQUEST_CODE_CONFIG);
    }
    /**
     * @brief Called when the user taps the 'Charts' button.
     * */
    private void openCharts() {
        Intent openChartsIntent = new Intent(this, chartsActivity.class);
        Bundle chartsBundle = new Bundle();
        openChartsIntent.putExtras(chartsBundle);
        startActivityForResult(openChartsIntent, COMMON.REQUEST_CODE_CONFIG);
    }
    /**
     * @brief Called when the user taps the 'angle measurement' button.
     * */
    private void openAngleMeasurement() {
        Intent openAngleMeasurementIntent = new Intent(this, angleMeasurementActivity.class);
        Bundle angleMeasurementBundle = new Bundle();
        openAngleMeasurementIntent.putExtras(angleMeasurementBundle);
        startActivityForResult(openAngleMeasurementIntent, COMMON.REQUEST_CODE_CONFIG);
    }
    /**
     * @brief Called when the user taps the 'Joystick' button.
     * */
    private void openJoystick() {
        Intent openJoystickIntent = new Intent(this, joystickActivity.class);
        Bundle joystickBundle = new Bundle();
        openJoystickIntent.putExtras(joystickBundle);
        startActivityForResult(openJoystickIntent, COMMON.REQUEST_CODE_CONFIG);
    }
    /**
     * @brief Called when the user taps the 'Led Matrix' button.
     * */
    private void openLedMatrix() {
        Intent openLedMatrixIntent = new Intent(this, ledMatrixActivity.class);
        Bundle ledMatrixBundle = new Bundle();
        openLedMatrixIntent.putExtras(ledMatrixBundle);
        startActivityForResult(openLedMatrixIntent, COMMON.REQUEST_CODE_CONFIG);
    }
    /**
     * @brief Called when the user taps the 'Settings' button.
     * */
    private void openConfig() {
        Intent openConfigIntent = new Intent(this, configActivity.class);
       Bundle configBundle = new Bundle();
        //configBundle.putString(COMMON.CONFIG_IP_ADDRESS, ipAddress);
       // configBundle.putInt(COMMON.CONFIG_SAMPLE_TIME, sampleTime);
       // configBundle.putInt(COMMON.CONFIG_MAX_SAMPLES, maxSamples);
       openConfigIntent.putExtras(configBundle);
       startActivityForResult(openConfigIntent, COMMON.REQUEST_CODE_CONFIG);
       startActivity(openConfigIntent);
    }

    /**
     * @brief Stops request timer (if currently exist)
     * and sets 'requestTimerFirstRequestAfterStop' flag.
     */


}
