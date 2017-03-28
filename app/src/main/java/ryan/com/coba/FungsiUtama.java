package ryan.com.coba;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class FungsiUtama extends Activity implements SensorEventListener {

    private SensorManager sensorManager;


    TextView x;
    TextView y;
    TextView z;
    TextView cond;

File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "logaccel.txt");

    float currentDis;
    String sx, sy, sz, def;

String buff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fungsiutama);

        cond = (TextView) findViewById (R.id.textView1);
        x = (TextView) findViewById (R.id.textView2);
        y = (TextView) findViewById (R.id.textView3);
        z = (TextView) findViewById (R.id.textView4);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensorManager.registerListener(this, sensorManager.getDefaultSensor
                (Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this, sensorManager.getDefaultSensor
                (Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float xVal = event.values[0];
            float yVal = event.values[1];
            float zVal = event.values[2];

            sx = "X Value : <font color = '#800080'> " + xVal + "</font>";
            sy = "Y Value : <font color = '#800080'> " + yVal + "</font>";
            sz = "Z Value : <font color = '#800080'> " + zVal + "</font>";

        }

        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){

            currentDis = event.values[0];
            buff = sx + " " + sy + " " + sz;
            def = "You Wont See This";

            if (currentDis < 2) {

                x.setText(Html.fromHtml(sx));
                y.setText(Html.fromHtml(sy));
                z.setText(Html.fromHtml(sz));
                cond.setText(Html.fromHtml(def));

                writetofile(buff);
            }

            else {

                x.setText(getString(R.string.textView2));
                y.setText(getString(R.string.textView3));
                z.setText(getString(R.string.textView4));
                cond.setText(getString(R.string.textView1));

            }

        }


    }

   public void writetofile(String buff){

        try {
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(buff);
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }


    }






}

