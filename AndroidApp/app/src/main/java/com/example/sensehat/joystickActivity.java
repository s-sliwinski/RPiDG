package com.example.sensehat;
        import androidx.appcompat.app.AppCompatActivity;

        import android.app.AlertDialog;
        import android.content.DialogInterface;
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
        import com.jjoe64.graphview.series.PointsGraphSeries;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.Timer;
        import java.util.TimerTask;

public class joystickActivity extends AppCompatActivity {
    private final int dataGraphMaxDataPointsNumber = 1000;

    /* BEGIN config data */
    private String ipAddress = COMMON.DEFAULT_IP_ADDRESS;
    private int sampleTime = COMMON.DEFAULT_SAMPLE_TIME;
    private int maxSamples = COMMON.DEFAULT_MAX_SAMPLES;
    private String portNumber = COMMON.DEFAULT_PORT_NUMBER;
    /* END config data */

    /* BEGIN widgets */

    private TextView textViewError;

    private GraphView joystickGraph;

    // private LineGraphSeries<DataPoint> joystickDataSeries;
    private  PointsGraphSeries<DataPoint> joystickDataSeries;
    private final double joystickDataGraphMaxX = 4.0d;
    private final double joystickDataGraphMinX =  -4.0d;
    private final double joystickDataGraphMaxY =  4.0d;
    private final double joystickDataGraphMinY = -4.0d;

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
    int previousBtnData=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick);

        /* BEGIN initialize widgets */
        /* BEGIN initialize TextViews */

        textViewError = findViewById(R.id.textViewErrorMsg);
        textViewError.setText("");

        /* END initialize TextViews */

        /* BEGIN initialize GraphView */
        // https://github.com/jjoe64/GraphView/wiki

        joystickGraph = (GraphView)findViewById(R.id.joystickGraph);
        // joystickDataSeries = new PointsGraphSeries<>(new DataPoint[]{});
        // joystickGraph.addSeries(joystickDataSeries);
        joystickGraph.getViewport().setXAxisBoundsManual(true);
        joystickGraph.getViewport().setMinX(joystickDataGraphMinX);
        joystickGraph.getViewport().setMaxX(joystickDataGraphMaxX);
        joystickGraph.getViewport().setYAxisBoundsManual(true);
        joystickGraph.getViewport().setMinY(joystickDataGraphMinY);
        joystickGraph.getViewport().setMaxY(joystickDataGraphMaxY);
        joystickGraph.setTitle("Joystick");


        /* END initialize GraphView */

        /* BEGIN config alter dialog */
        configAlterDialog = new AlertDialog.Builder(joystickActivity.this);
        configAlterDialog.setTitle("This will STOP data acquisition. Proceed?");
        configAlterDialog.setIcon(android.R.drawable.ic_dialog_alert);
        configAlterDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                stopRequestTimerTask();

                //next widget here
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
        queue = Volley.newRequestQueue(joystickActivity.this);
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
    /**
     * @brief Create display text for IoT server IP address
     * @param ip IP address (string)
     * @retval Display text for textViewIP widget
     */
    private String getIpAddressDisplayText(String ip) {
        return ("IP: " + ip);
    }
    /**
     * @brief Create JSON file URL from IoT server IP.
     * @param ip IP address (string)
     * @retval GET request URL
     */
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
     * @brief Reading x value of joystick from JSON response.
     * @param response IoT server JSON response as string
     * @retval new chart data
     */
    private int getXDataFromResponse(String response) {
        JSONObject jObject;
        int x_pos = 0;

        // Create generic JSON object form string
        try {
            jObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            return x_pos;
        }

        // Read chart data form JSON object
        try {
            x_pos =(int) jObject.get("x");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return x_pos;
    }
    /**
     * @brief Reading y value of joystick from JSON response.
     * @param response IoT server JSON response as string
     * @retval new chart data
     */
    private int getYDataFromResponse(String response) {
        JSONObject jObject;
        int y_pos = 0;

        // Create generic JSON object form string
        try {
            jObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            return y_pos;
        }

        // Read chart data form JSON object
        try {
            y_pos = (int)jObject.get("y");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return y_pos;
    }
    /**
     * @brief Reading button value of joystick from JSON response.
     * @param response IoT server JSON response as string
     * @retval new chart data
     */
    private int getBtnDataFromResponse(String response) {
        JSONObject jObject;
        int mid =0;

        // Create generic JSON object form string
        try {
            jObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            return mid;
        }

        // Read chart data form JSON object
        try {
            mid = (int)jObject.get("mid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mid;
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
    private void responseHandling(String response)
    {
        if(requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);
            // get raw data from JSON response
            int XData_int = getXDataFromResponse(response);
            //double XData_double=(double)XData_int;

            int YData_int = getYDataFromResponse(response);
            //double YData_double=(double)YData_int;
            double YData = getYDataFromResponse(response);
            int btnData=getBtnDataFromResponse(response);

            boolean shape_flag=true;

            // update chart
            joystickDataSeries = new PointsGraphSeries<>(new DataPoint[]{new DataPoint(XData_int, YData_int)});
            joystickGraph.addSeries(joystickDataSeries);

            //change point shape
            if(previousBtnData==btnData)
            {
                joystickDataSeries.setShape(PointsGraphSeries.Shape.POINT);

            }
            else
            {
                joystickDataSeries.setShape(PointsGraphSeries.Shape.RECTANGLE);
                previousBtnData=btnData;
            }




            // remember previous time stamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }

}
