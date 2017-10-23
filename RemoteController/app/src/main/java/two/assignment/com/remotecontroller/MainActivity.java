package two.assignment.com.remotecontroller;

import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private int connection;
    private MqttHelper helper;

    private Button btn;
    private Button serverButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.button2);
        serverButton = (Button)findViewById(R.id.button);

        btn.setEnabled(false);

        helper = new MqttHelper(getApplicationContext());

        connection = 0;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void connectMQTTServer(View view){
        if(!isNetworkAvailable()){
            Toast.makeText(this,
                    "No internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        if(connection == 0){
            if(helper.connect()>0){
                Toast.makeText(this,
                        "Connection is successful", Toast.LENGTH_LONG).show();

                serverButton.setText("Click to disconnect");
                serverButton.setBackgroundColor(Color.RED);

                btn.setEnabled(true);

                connection = 1;
            }
            else{
                Toast.makeText(this,
                        "Connection failed", Toast.LENGTH_LONG).show();
            }

        }else{
            if(helper.disconnect()>0){

                serverButton.setBackgroundColor(Color.GRAY);
                serverButton.setText("Connect to server");

                btn.setEnabled(false);

                connection = 0;

                Toast.makeText(this,
                        "Disconnected from server", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(this,
                        "Cannot disconnect", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void sendMessage(View view){
        Intent intent = new Intent(this, Main2Activity.class);
//        intent.putExtra("serialize_data", helper);
        startActivity(intent);
    }
}
