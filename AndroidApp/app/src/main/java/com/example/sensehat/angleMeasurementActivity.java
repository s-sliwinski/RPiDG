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


public class angleMeasurementActivity extends AppCompatActivity {

    /* BEGIN config data */
    private String ipAddress = COMMON.DEFAULT_IP_ADDRESS;
    private int sampleTime = COMMON.DEFAULT_SAMPLE_TIME;
    private int maxSamples = COMMON.DEFAULT_MAX_SAMPLES;
    private int portNumber = COMMON.DEFAULT_PORT_NUMBER;
    /* END config data */

    /* BEGIN widgets */
    private TextView textViewIP;
    private TextView textViewSampleTime;
    private TextView textViewError;

    private GraphView rollGraph;
    private GraphView pitchGraph;
    private GraphView yawGraph;

    private LineGraphSeries<DataPoint> rollDataSeries;
    private LineGraphSeries<DataPoint> pitchDataSeries;
    private LineGraphSeries<DataPoint> yawDataSeries;

    private final int dataGraphMaxDataPointsNumber = 1000;

    private final double rollDataGraphMaxX = 10.0d;
    private final double rollDataGraphMinX =  0.0d;
    private final double rollDataGraphMaxY =  40.0d;
    private final double rollDataGraphMinY = 10.0d;


    private final double pitchDataGraphMaxX = 10.0d;
    private final double pitchDataGraphMinX =  0.0d;
    private final double pitchDataGraphMaxY =  1050.0d;
    private final double pitchDataGraphMinY = 950.0d;

    private final double yawDataGraphMaxX = 10.0d;
    private final double yawDataGraphMinX =  0.0d;
    private final double yawDataGraphMaxY =  100.0d;
    private final double yawDataGraphMinY = 0.0d;

    private AlertDialog.Builder configAlterDialog;
    /* END widgets */

    /* BEGIN request timer */
    private RequestQueue queue;
    private Timer requestTimer;
    private long requestTimerTimeStamp = 0;
    private long requestTimerPreviousTime = -1;
    private boolean requestTimerFirstRequest = true;
    private boolean requestTimerFirstRequestAfterStop;
    private TimerTask requestTimerTask;
    private final Handler handler = new Handler();
    /* END request timer */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_angle_measurement);

        /* BEGIN initialize widgets */
        /* BEGIN initialize TextViews */
        textViewIP = findViewById(R.id.textViewIP);
        textViewIP.setText(getIpAddressDisplayText(ipAddress));

        textViewSampleTime = findViewById(R.id.textViewSampleTime);
        textViewSampleTime.setText(getSampleTimeDisplayText(Integer.toString(sampleTime)));

        textViewError = findViewById(R.id.textViewErrorMsg);
        textViewError.setText("");
        /* END initialize TextViews */

        /* BEGIN initialize GraphView */
        // https://github.com/jjoe64/GraphView/wiki
        rollGraph = (GraphView)findViewById(R.id.rollGraph);
        rollDataSeries = new LineGraphSeries<>(new DataPoint[]{});
        rollGraph.addSeries(rollDataSeries);
        rollGraph.getViewport().setXAxisBoundsManual(true);
        rollGraph.getViewport().setMinX(rollDataGraphMinX);
        rollGraph.getViewport().setMaxX(rollDataGraphMaxX);
        rollGraph.getViewport().setYAxisBoundsManual(true);
        rollGraph.getViewport().setMinY(rollDataGraphMinY);
        rollGraph.getViewport().setMaxY(rollDataGraphMaxY);
        rollGraph.setTitle("Roll");

        pitchGraph =(GraphView)findViewById((R.id.pitchGraph));
        pitchDataSeries = new LineGraphSeries<>(new DataPoint[]{});
        pitchGraph.addSeries(pitchDataSeries);
        pitchGraph.getViewport().setXAxisBoundsManual(true);
        pitchGraph.getViewport().setMinX(pitchDataGraphMinX);
        pitchGraph.getViewport().setMaxX(pitchDataGraphMaxX);
        pitchGraph.getViewport().setYAxisBoundsManual(true);
        pitchGraph.getViewport().setMinY(pitchDataGraphMinY);
        pitchGraph.getViewport().setMaxY(pitchDataGraphMaxY);
        pitchGraph.setTitle("Pitch");

        yawGraph =(GraphView)findViewById((R.id.yawGraph));
        yawDataSeries = new LineGraphSeries<>(new DataPoint[]{});
        yawGraph.addSeries(yawDataSeries);
        yawGraph.getViewport().setXAxisBoundsManual(true);
        yawGraph.getViewport().setMinX(yawDataGraphMinX);
        yawGraph.getViewport().setMaxX(yawDataGraphMaxX);
        yawGraph.getViewport().setYAxisBoundsManual(true);
        yawGraph.getViewport().setMinY(yawDataGraphMinY);
        yawGraph.getViewport().setMaxY(yawDataGraphMaxY);
        yawGraph.setTitle("Yaw");
        /* END initialize GraphView */

        /* BEGIN config alter dialog */
        configAlterDialog = new AlertDialog.Builder(angleMeasurementActivity.this);
        configAlterDialog.setTitle("This will STOP data acquisition. Proceed?");
        configAlterDialog.setIcon(android.R.drawable.ic_dialog_alert);
        configAlterDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                stopRequestTimerTask();
                openConfig();
            }
        });
        configAlterDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        /* END config alter dialog */
        /* END initialize widgets */

        // Initialize Volley request queue
        queue = Volley.newRequestQueue(angleMeasurementActivity.this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);
        if ((requestCode == COMMON.REQUEST_CODE_CONFIG) && (resultCode == RESULT_OK)) {

            // IoT server IP address
            ipAddress = dataIntent.getStringExtra(COMMON.CONFIG_IP_ADDRESS);
            textViewIP.setText(getIpAddressDisplayText(ipAddress));

            // Sample time (ms)
            String sampleTimeText = dataIntent.getStringExtra(COMMON.CONFIG_SAMPLE_TIME);
            sampleTime = Integer.parseInt(sampleTimeText);
            textViewSampleTime.setText(getSampleTimeDisplayText(sampleTimeText));
            // Max samples
            String maxSamplesText = dataIntent.getStringExtra(COMMON.CONFIG_MAX_SAMPLES);
            maxSamples = Integer.parseInt(maxSamplesText);


        }
    }
    /**
     * @brief Main activity button onClick procedure - common for all upper menu buttons
     * @param v the View (Button) that was clicked
     */
    public void btns_onClick(View v) {
        switch (v.getId()) {
            case R.id.configBtn: {
                if(requestTimer != null)
                    configAlterDialog.show();
                else
                    openConfig();
                break;
            }
            case R.id.startBtn: {
                startRequestTimer();
                break;
            }
            case R.id.stopBtn: {
                stopRequestTimerTask();
                break;
            }
            default: {
                // do nothing
            }
        }
    }

    /**
     * @brief Create display text for IoT server IP address
     * @param ip IP address (string)
     * @retval Display text for textViewIP widget
     */
    private String getIpAddressDisplayText(String ip) {
        return ("IP: " + ip);
    }

    /**
     * @brief Create display text for requests sample time
     * @param st Sample time in ms (string)
     * @retval Display text for textViewSampleTime widget
     */
    private String getSampleTimeDisplayText(String st) {
        return ("Sample time: " + st + " ms");
    }

    /**
     * @brief Create JSON file URL from IoT server IP.
     * @param ip IP address (string)
     * @retval GET request URL
     */
    private String getURL(String ip) {
        return ("http://" + ip + "/" + COMMON.FILE_NAME);
    }

    /**
     * @brief Handles application errors. Logs an error and passes error code to GUI.
     * @param errorCode local error codes, see: COMMON
     */
    private void errorHandling(int errorCode) {
        switch(errorCode) {
            case COMMON.ERROR_TIME_STAMP:
                textViewError.setText("ERR #1");
                Log.d("errorHandling", "Request time stamp error.");
                break;
            case COMMON.ERROR_NAN_DATA:
                textViewError.setText("ERR #2");
                Log.d("errorHandling", "Invalid JSON data.");
                break;
            case COMMON.ERROR_RESPONSE:
                textViewError.setText("ERR #3");
                Log.d("errorHandling", "GET request VolleyError.");
                break;
            default:
                textViewError.setText("ERR ??");
                Log.d("errorHandling", "Unknown error.");
                break;
        }
    }

    /**
     * @brief Called when the user taps the 'Config' button.
     * */
    private void openConfig() {
        Intent openConfigIntent = new Intent(this, configActivity.class);
        Bundle configBundle = new Bundle();
        configBundle.putString(COMMON.CONFIG_IP_ADDRESS, ipAddress);
        configBundle.putInt(COMMON.CONFIG_SAMPLE_TIME, sampleTime);
        configBundle.putInt(COMMON.CONFIG_MAX_SAMPLES, maxSamples);
        openConfigIntent.putExtras(configBundle);
        startActivityForResult(openConfigIntent, COMMON.REQUEST_CODE_CONFIG);
    }

    /**
     * @brief Reading roll chart data from JSON response.
     * @param response IoT server JSON response as string
     * @retval new chart data
     */
    private double getRollDataFromResponse(String response) {
        JSONObject jObject;
        double x = Double.NaN;

        // Create generic JSON object form string
        try {
            jObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            return x;
        }

        // Read chart data form JSON object
        try {
            x = (double)jObject.get("roll");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return x;
    }
    /**
     * @brief Reading pitch chart data from JSON response.
     * @param response IoT server JSON response as string
     * @retval new chart data
     */
    private double getPitchDataFromResponse(String response) {
        JSONObject jObject;
        double x = Double.NaN;

        // Create generic JSON object form string
        try {
            jObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            return x;
        }

        // Read chart data form JSON object
        try {
            x = (double)jObject.get("pitch");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return x;
    }
    /**
     * @brief Reading yaw chart data from JSON response.
     * @param response IoT server JSON response as string
     * @retval new chart data
     */
    private double getYawDataFromResponse(String response) {
        JSONObject jObject;
        double x = Double.NaN;

        // Create generic JSON object form string
        try {
            jObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            return x;
        }

        // Read chart data form JSON object
        try {
            x = (double)jObject.get("yaw");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return x;
    }
    /**
     * @brief Starts new 'Timer' (if currently not exist) and schedules periodic task.
     */
    private void startRequestTimer() {
        if(requestTimer == null) {
            // set a new Timer
            requestTimer = new Timer();

            // initialize the TimerTask's job
            initializeRequestTimerTask();
            requestTimer.schedule(requestTimerTask, 0, sampleTime);

            // clear error message
            textViewError.setText("");
        }
    }

    /**
     * @brief Stops request timer (if currently exist)
     * and sets 'requestTimerFirstRequestAfterStop' flag.
     */
    private void stopRequestTimerTask() {
        // stop the timer, if it's not already null
        if (requestTimer != null) {
            requestTimer.cancel();
            requestTimer = null;
            requestTimerFirstRequestAfterStop = true;
        }
    }

    /**
     * @brief Initialize request timer period task with 'Handler' post method as 'sendGetRequest'.
     */
    private void initializeRequestTimerTask() {
        requestTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() { sendGetRequest(); }
                });
            }
        };
    }

    /**
     * @brief Sending GET request to IoT server using 'Volley'.
     */
    private void sendGetRequest()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURL(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandling(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(COMMON.ERROR_RESPONSE); }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    /**
     * @brief Validation of client-side time stamp based on 'SystemClock'.
     */
    private long getValidTimeStampIncrease(long currentTime)
    {
        // Right after start remember current time and return 0
        if(requestTimerFirstRequest)
        {
            requestTimerPreviousTime = currentTime;
            requestTimerFirstRequest = false;
            return 0;
        }

        // After each stop return value not greater than sample time
        // to avoid "holes" in the plot
        if(requestTimerFirstRequestAfterStop)
        {
            if((currentTime - requestTimerPreviousTime) > sampleTime)
                requestTimerPreviousTime = currentTime - sampleTime;

            requestTimerFirstRequestAfterStop = false;
        }

        // If time difference is equal zero after start
        // return sample time
        if((currentTime - requestTimerPreviousTime) == 0)
            return sampleTime;

        // Return time difference between current and previous request
        return (currentTime - requestTimerPreviousTime);
    }

    /**
     * @brief GET response handling - chart data series updated with IoT server data.
     */
    private void responseHandling(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);
            // get raw data from JSON response
            double rollData = getRollDataFromResponse(response);
            double pitchData = getPitchDataFromResponse(response);
            double yawData = getYawDataFromResponse(response);
            // update chart
            if (isNaN(rollData)||isNaN(pitchData)||isNaN(yawData)) {
                errorHandling(COMMON.ERROR_NAN_DATA);
            } else {
                // update plot series
                double timeStamp = requestTimerTimeStamp / 1000.0; // [sec]

                boolean scrollGraph = (timeStamp > rollDataGraphMaxX);
                boolean scrollPressGraph = (timeStamp > pitchDataGraphMaxX);
                boolean scrollHumGraph = (timeStamp > yawDataGraphMaxX);
                yawDataSeries.appendData(new DataPoint(timeStamp,yawData),scrollHumGraph,maxSamples);
                pitchDataSeries.appendData(new DataPoint(timeStamp, pitchData),scrollPressGraph, maxSamples);
                rollDataSeries.appendData(new DataPoint(timeStamp, rollData), scrollGraph, maxSamples);
                // refresh chart
                pitchGraph.onDataChanged(true, true);
                rollGraph.onDataChanged(true, true);
                yawGraph.onDataChanged(true,true);
            }

            // remember previous time stamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }
}
