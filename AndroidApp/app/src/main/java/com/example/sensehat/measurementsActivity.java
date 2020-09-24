package com.example.sensehat;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;


//import android.support.v7.app.AppCompatActivity;

public class measurementsActivity extends AppCompatActivity {

    /* BEGIN config data */
    private String ipAddress = COMMON.DEFAULT_IP_ADDRESS;
    private int sampleTime = COMMON.DEFAULT_SAMPLE_TIME;
    private int maxSamples = COMMON.DEFAULT_MAX_SAMPLES;
    private String portNumber = COMMON.DEFAULT_PORT_NUMBER;
    /* END config data */

    private TextView textViewError;
    int JSONSizeGloabal;
    int iGlobal;
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
       setContentView(R.layout.activity_measurements);

        textViewError = findViewById(R.id.textViewErrorMsg);
        textViewError.setText("");





        queue = Volley.newRequestQueue(measurementsActivity.this);
    }
    /**
     * @brief Main activity button onClick procedure - common for all upper menu buttons
     * @param v the View (Button) that was clicked
     */
    public void btns_onClick(View v) {
        switch (v.getId()) {

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
    private String getURL(String ip,String portNumber,String fileName) {
        return ("http://" + ip +":"+portNumber+ "/" + fileName);
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
     * @brief checking size of JSON response.
     * @param response IoT server JSON response as string
     * @retval size of JSON response
     */
    private int getJSONSize(String response) {
        JSONObject jObject;
        int size = 0;
        // Create generic JSON object form string
        try {
            jObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            return size;
        }

        // Read chart data form JSON object
        size = jObject.length();
        return size;
    }
    /**
     * @brief checking size of JSON response.
     * @param response IoT server JSON response as string
     * @retval size of JSON response
     */
    private JSONArray getJSONnames(String response)  {
        JSONObject jObject = null;
        int x = 0;
        // Create generic JSON object form string

        try {
            jObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONArray name=jObject.names();

        return name;
    }  /**
     * @brief checking size of JSON response.
     * @param response IoT server JSON response as string
     * @retval size of JSON response
     */
    private List getListJSONnames(String response)  {
        JSONObject jObject = null;
        List<String> namesList=new ArrayList<String>();
        // Create generic JSON object form string

        try {
            jObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray names=jObject.names();
        //converting JSONarray to normal list
        if(names!=null)
        {
            int len=names.length();
            for(int i=0;i<len;i++){
                try {
                    namesList.add(names.get(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    return  namesList;

    }
    /**
     * @brief checking size of JSON response.
     * @param response IoT server JSON response as string
     * @retval size of JSON response
     */
    private double getJSONvalue(String response, JSONArray names,int it)  {
        JSONObject jObject = null;
        double val = 0;
        // Create generic JSON object form string

        try {
            jObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Read value from JSONobject
        try {
            val = (double) jObject.getDouble((String) names.get(it));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return val;

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
            requestTimer.schedule(requestTimerTask, 0, COMMON.DEFAULT_SAMPLE_TIME);


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
        String url = getURL(ipAddress,portNumber,COMMON.FILE_NAME);

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
            if((currentTime - requestTimerPreviousTime) > COMMON.DEFAULT_SAMPLE_TIME)
                requestTimerPreviousTime = currentTime - COMMON.DEFAULT_SAMPLE_TIME;

            requestTimerFirstRequestAfterStop = false;
        }

        // If time difference is equal zero after start
        // return sample time
        if((currentTime - requestTimerPreviousTime) == 0)
            return COMMON.DEFAULT_SAMPLE_TIME;

        // Return time difference between current and previous request
        return (currentTime - requestTimerPreviousTime);
    }
    /**
     * @brief GET response handling - chart data series updated with IoT server data.
     */
    @SuppressLint("SetTextI18n")
    private void responseHandling(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);
            // get raw data from JSON response
            //int XData_int = getXDataFromResponse(response);

            JSONSizeGloabal=getJSONSize(response);
            int it = 0;
            String name;
            JSONArray names= getJSONnames(response);
            List namesString=getListJSONnames(response);
           List<Double> vals = new ArrayList<>();
           //dynamic generate
            LinearLayout linearLayout = findViewById(R.id.rootLayout);

            List views=new ArrayList();
            if (linearLayout != null) {
                linearLayout.removeAllViewsInLayout();
            }
           for(int i=0;i<JSONSizeGloabal;i++)
           {
               vals.add(getJSONvalue(response,names,i));
           }
            for(int i=0;i<JSONSizeGloabal;i++)
            {
                TextView textViewName = new TextView(this);
                TextView textViewValue = new TextView(this);

                textViewName.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textViewValue.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

                textViewName.setGravity(Gravity.LEFT);
                textViewValue.setGravity(Gravity.RIGHT);
                //textViewValue.setGravity(Gravity.CENTER);

                textViewValue.setText(vals.get(i).toString());
                textViewName.setText(namesString.get(i).toString());
                if (linearLayout != null) {
                   linearLayout.addView(textViewName);
                    linearLayout.addView(textViewValue);
                }
            }

            // remember previous time stamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }
}
