package two.assignment.com.remotecontroller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.HeaderViewListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main2Activity extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private TextView accXText,accYText,accZText, gyroXText,gyroYText,gyroZText, rotXText,rotYText,rotZText;
    private Sensor acc,gyro,rot;

    private RequestQueue requestQueue;

    private MqttHelper helper;

    private long lastUpdate = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        helper = new MqttHelper(this);
        helper.connect();



        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
//
//        Intent intent = getIntent();
//        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        accXText = (TextView) findViewById(R.id.accXValue);
        accYText = (TextView) findViewById(R.id.accYValue);
        accZText = (TextView) findViewById(R.id.accZValue);

        gyroXText = (TextView) findViewById(R.id.gyroXValue);
        gyroYText = (TextView) findViewById(R.id.gyroYValue);
        gyroZText = (TextView) findViewById(R.id.gyroZValue);

        rotXText = (TextView) findViewById(R.id.rotXValue);
        rotYText = (TextView) findViewById(R.id.rotYValue);
        rotZText = (TextView) findViewById(R.id.rotZValue);


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        rot = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        requestQueue = Volley.newRequestQueue(this);

    }
    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {

        Sensor mySensor = event.sensor;
        float x,y,z;
        JSONObject coords = new JSONObject(),temp = new JSONObject();

        switch (mySensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:

                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                accXText.setText(Float.toString(x));
                accYText.setText(Float.toString(y));
                accZText.setText(Float.toString(z));

                try{

                    temp.put("x",Float.toString(x));
                    temp.put("y",Float.toString(y));
                    temp.put("z",Float.toString(z));

                    coords.put("ACCELEROMETER",temp);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                break;
            case Sensor.TYPE_GYROSCOPE:

                temp = new JSONObject();

                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                gyroXText.setText(Float.toString(x));
                gyroYText.setText(Float.toString(y));
                gyroZText.setText(Float.toString(z));

                try{

                    temp.put("x",Float.toString(x));
                    temp.put("y",Float.toString(y));
                    temp.put("z",Float.toString(z));

                    coords.put("GYROSCOPE",temp);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                break;
            case Sensor.TYPE_ROTATION_VECTOR:

                temp = new JSONObject();

                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                rotXText.setText(Float.toString(x));
                rotYText.setText(Float.toString(y));
                rotZText.setText(Float.toString(z));

                try{

                    temp.put("x",Float.toString(x));
                    temp.put("y",Float.toString(y));
                    temp.put("z",Float.toString(z));

                    coords.put("ROTATION_VECTOR",temp);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                break;
        }

        helper.publishMessage(coords.toString());
//        long actualTime = event.timestamp; //get the event's timestamp
//        if(actualTime - lastUpdate > 10000) {
//            Log.e("TIme: ",actualTime+""+coords.toString());
//            helper.publishMessage(coords.toString());
//            lastUpdate = actualTime;
//        }


//        long time= System.currentTimeMillis()/1000;
//        //Log.e("TIme: ",time+"");
//        if(time%5 == 0){
//            Log.e("TIme: ",time+""+coords.toString());
//            helper.publishMessage(coords.toString());
//        }

//        Log.e("JSON DATA", coords.toString());
//        post(coords.toString());

//        inst.doInBackground("http://192.168.0.2:8080/apis/index.php",coords.toString());
//        Log.e("Output from Client",inst.doInBackground("http://192.168.0.2:8080/apis/index.php",coords.toString()));
//        We don't need all this data so we need to make sure we only sample a subset of the data we get from the device's accelerometer. We store the system's current time (in milliseconds) store it in curTime and check whether more than 100 milliseconds have passed since the last time onSensorChanged was invoked.
    }

    protected void post(final String data){
//        String URL = "http://192.168.0.2:8080/apis/index.php";
//        String URL = "http://getcoords.us-west-2.elasticbeanstalk.com/";
        String URL = "http://s3674204-cc2016.appspot.com/";

        StringRequest sr = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("HttpClient", "success! response: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("HttpClient", "error: " + error.toString());
                    }
                })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("result",data);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        requestQueue.add(sr);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_UI );
        mSensorManager.registerListener(this, rot, SensorManager.SENSOR_DELAY_UI );
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
