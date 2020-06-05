package com.example.sensehat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;


public class ledMatrixActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {
    SeekBar redSeekBar, greenSeekBar, blueSeekBar;
    View colorView;
    EditText urlText;
    String imageText;


    int a, r, g, b, ARGB;
    int ledOffColor;
    Vector ledOffColorVec;
    Integer[][][] ledColors = new Integer[8][8][3];
    //end colors

    //image init
    String[] colorsString;
    Integer[][][]imageColors =new Integer[8][8][3];

    List<List<Integer>> imageJson;

    //spinner flags
    boolean mario=false;
    boolean luigi=false;
    boolean clear_flag;

    //begin request
    String url = "http://192.168.0.11/led_display.php";
    String url_mario = "http://192.168.0.11/mario.php";
    String url_luigi = "http://192.168.0.11/luigi.php";
    private RequestQueue queue;
    Map<String, String> paramsClear = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_matrix);
        ledOffColor = ResourcesCompat.getColor(getResources(), R.color.ledIndBackground, null);
        ledOffColorVec = inToRGB(ledOffColor);
        ARGB = ledOffColor;
        a = 0xff;
        r = 0x00;
        g = 0x00;
        b = 0x00;
        clearLedArray();
        //end of color init
        //begin spinners
        Spinner spinner = (Spinner) findViewById(R.id.imageSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.images,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //begin widgets init
        redSeekBar = (SeekBar) findViewById(R.id.seekBarR);
        redSeekBar.setMax(255);
        redSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue=0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ARGB = seekBarUpdate('R', progressChangedValue);
                colorView.setBackgroundColor(ARGB);
            }
        });

        greenSeekBar = (SeekBar) findViewById(R.id.seekBarG);
        greenSeekBar.setMax(255);
        greenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue=0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ARGB = seekBarUpdate('G', progressChangedValue);
                colorView.setBackgroundColor(ARGB);
            }
        });
        blueSeekBar = (SeekBar) findViewById(R.id.seekBarB);
        blueSeekBar.setMax(255);
        blueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue=0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ARGB = seekBarUpdate('B', progressChangedValue);
                colorView.setBackgroundColor(ARGB);
            }
        });

        colorView = findViewById(R.id.colorView);

        urlText = findViewById(R.id.urlText);
        urlText.setText(url);
        //end widgets init

        //begin volley init
        queue = Volley.newRequestQueue(this);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                //"LEDij" : "[i,j,r,g,b]"
                String data = "[" + Integer.toString(i) + "," + Integer.toString(j) + ",0,0,0]";
                paramsClear.put(ledIndexToTag(i, j), data);
            }
        }
        //end volley init
    }

    int seekBarUpdate(char color, int value) {
        switch (color) {
            case 'R':
                r = value;
                break;
            case 'G':
                g = value;
                break;
            case 'B':
                b = value;
                break;
            default: /*do nothing*/
                break;
        }
        a=(r+g+b)/3;
        return argbToInt(a,r,g,b);
    }

    public int argbToInt (int _a, int _r, int _g, int _b)
    {
        return (_a & 0xff) << 24 | (_r & 0xff) << 16 | (_g & 0xff) << 8 | (_b & 0xff);
    }
    public Vector inToRGB(int argb)
    {
        int _r =(argb >>16)& 0xff;
        int _g =(argb >>8)& 0xff;
        int _b =argb & 0xff;
        Vector rgb =new Vector(3);
        rgb.add(0,_r);
        rgb.add(1,_g);
        rgb.add(2,_b);
        return rgb;
    }
    public void changeLedIndicatorColor (View v)
    {
        v.setBackgroundColor(ARGB);
        String tag = (String)v.getTag();
        Vector index =ledTagToIndex(tag);
        int x = (int)index.get(0);
        int y = (int)index.get(1);
        ledColors[x][y][0]=r;
        ledColors[x][y][1]=g;
        ledColors[x][y][2]=b;
    }
    public void changeColorToImage(List<List<Integer>> imageList)
    {   int xi,yi;
        int ri=0;
        int gi=0;
        int bi =0;
        int ai=0;

        TableLayout tb = (TableLayout)findViewById(R.id.ledTable);
        View ledInd;
        for (int i=0;i<64;i++)
        {
            xi=imageList.get(i).get(0);
            yi=imageList.get(i).get(1);
            ri=imageList.get(i).get(2);
            gi=imageList.get(i).get(3);
            bi=imageList.get(i).get(4);
            ledColors[xi][yi][0]=ri;
            ledColors[xi][yi][1]=gi;
            ledColors[xi][yi][2]=bi;
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ledInd=tb.findViewWithTag(ledIndexToTag(i,j));
                ri=ledColors[i][j][0];
                gi=ledColors[i][j][1];
                bi=ledColors[i][j][2];
                ai=(ri+gi+bi)/3;
                 //how to mix color here?
                ARGB=(ai & 0xff) << 24 | (ri & 0xff) << 16 | (gi & 0xff) << 8 | (bi & 0xff);
                ledInd.setBackgroundColor(ARGB);
            }
        }
    }
    Vector ledTagToIndex(String tag)
    {
        //tag "LEDxy"
        Vector vec =new Vector(2);
        vec.add(0,Character.getNumericValue(tag.charAt(3)));
        vec.add(1,Character.getNumericValue(tag.charAt(4)));
        return vec;
    }
    String ledIndexToTag (int x, int y){
        return "LED"+Integer.toString(x)+Integer.toString(y);
    }
    String ledIndexToJsonColor(int x, int y) {
        String _x=Integer.toString(x);
        String _y=Integer.toString(y);
        String _r=Integer.toString(ledColors[x][y][0]);
        String _g=Integer.toString(ledColors[x][y][1]);
        String _b=Integer.toString(ledColors[x][y][2]);
        return "["+_x+","+_y+","+_r+","+_g+","+_b+"]";
    }

    boolean ledColorNotNull(int x ,int y)
    {
        return !((ledColors[x][y][0]==null)||(ledColors[x][y][1]==null)||(ledColors[x][y][2]==null));
    }

    public void clearLedArray()
    {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ledColors[i][j][0]=null;
                ledColors[i][j][1]=null;
                ledColors[i][j][2]=null;
            }
        }
    }
    public void clearAllLed(View v)
    {

        TableLayout tb = (TableLayout)findViewById(R.id.ledTable);
        View ledInd;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ledInd=tb.findViewWithTag(ledIndexToTag(i,j));
                ledInd.setBackgroundColor(ledOffColor);
            }
        }
        clearLedArray();
        sendClearRequest();
    }
    public void clearAllLedFromSpinner()
    {

        TableLayout tb = (TableLayout)findViewById(R.id.ledTable);
        View ledInd;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ledInd=tb.findViewWithTag(ledIndexToTag(i,j));
                ledInd.setBackgroundColor(ledOffColor);
            }
        }
        clearLedArray();
    }
    public Map<String,String> getLedDisplayParams()
    {
        String led;
        String color;
        Map <String,String> params =new HashMap<String, String>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(ledColorNotNull(i,j)) {
                    led=ledIndexToTag(i,j);
                    color=ledIndexToJsonColor(i,j);
                    params.put(led, color);
                }

            }
        }
        return  params;
    }
    public void send_OnClick(View v)
    {
        if (mario) {

            sendControlImageRequest(url_mario);
            mario=false;
        } else if (luigi){

            sendControlImageRequest(url_luigi);
            luigi=false;
        }else
        {
            sendClearRequest();
            sendControlRequest(v);
        }


    }
    public void sendControlRequest (View v)
    {
        url=urlText.getText().toString();

        StringRequest postRequest=new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        // TODO: check if ACK is valid
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String msg =error.getMessage();
                        if(msg!=null)
                            Log.d("Error response",msg);
                        else {
                            //error specific code
                        }
                    }
                }
        ){
            @Override
            protected  Map<String,String>getParams()
            {
                return getLedDisplayParams();
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(5000,0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }
    void sendClearRequest()
    {
        clear_flag = true;
        url=urlText.getText().toString();
        StringRequest postRequest=new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String msg =error.getMessage();
                        if(msg!=null)
                            Log.d("Error response",msg);
                        else{
                            //error type specific code
                        }
                    }
                }
        ){
            @Override
            protected Map<String,String> getParams(){
                return paramsClear;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        imageText = parent.getSelectedItem().toString();

        if(clear_flag)
        {
         parent.setSelection(0);
         clear_flag=false;
        }
        Log.d("errorHandling", imageText);
        switch (imageText)
        {
            case "-":
                clearAllLedFromSpinner();
                break;
            case "Mario":
                sendImageRequest(COMMON.FILE_MARIO);

                mario=true;
                luigi=false;
                break;
            case "Luigi":
                sendImageRequest(COMMON.FILE_LUIGI);
                mario=false;
                luigi=true;
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void sendImageRequest(String fileName)
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = "http://192.168.0.11/"+fileName;

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        responseHandling(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {  }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void sendControlImageRequest(String url_image)
    {

        //url_image=urlText.getText().toString();
        StringRequest postRequest=new StringRequest(Request.Method.POST, url_image,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        // TODO: check if ACK is valid
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String msg =error.getMessage();
                        if(msg!=null)
                            Log.d("Error response",msg);
                        else {
                            //error specific code
                        }
                    }
                }
        ){
            @Override
            protected  Map<String,String>getParams()
            {
                return getLedDisplayParams();
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(5000,0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    private void responseHandling(String response)
    {
        imageJson =getJsonFromResponse(response);
        changeColorToImage(imageJson);


    }
    private List<List<Integer>> getJsonFromResponse(String response) {
        JSONArray jArr = null;
        List<List<Integer>> rgbs = new ArrayList<>();
        //rgbs.get(0).get(0);
        try (JsonReader jsonReader = new JsonReader(new StringReader(response))) {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                jsonReader.beginArray();
                List<Integer> currentRgb = new ArrayList<Integer>();
                rgbs.add(currentRgb);
                while (jsonReader.hasNext()) {
                    currentRgb.add(jsonReader.nextInt());
                }

                jsonReader.endArray();
            }
            jsonReader.endArray();
        } catch (IOException e) {
            Log.d("ERROR", String.valueOf(e.getStackTrace()));
        }

        Log.d("asd", rgbs.toString());
    return rgbs;
    }


}


