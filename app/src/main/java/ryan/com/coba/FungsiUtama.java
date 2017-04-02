package ryan.com.coba;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.opencsv.CSVWriter;

public class FungsiUtama extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private File path;
    private int activity;
    private static final int request = 1;

    private static String[] permission = {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    TextView viewx;
    TextView viewy;
    TextView viewz;
    TextView cond;

    TextView textduduk;
    TextView textberdiri;
    TextView textberjalan;
    CheckBox cekduduk;
    CheckBox cekberdiri;
    CheckBox cekberjalan;

    String condition;

    private float xVal, yVal, zVal;
    private ArrayList<Float> X, Y, Z;
    private int window = 20;
    private boolean started = false;
    private boolean stopped = true;
    private RadioButton radiotrain;
    private RadioButton radioclassify;
    private RadioGroup radiogroup;

    float currentDis;
    String sx, sy, sz, def;

    private Sensor accel;
    private Sensor prox;

    CSVWriter writer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fungsiutama);

        if (shouldAskPermissions()) {
            verifyStoragePermissions(this);
        }
        path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);


        cond = (TextView) findViewById(R.id.textView1);
        viewx = (TextView) findViewById(R.id.textView2);
        viewy = (TextView) findViewById(R.id.textView3);
        viewz = (TextView) findViewById(R.id.textView4);

        radiotrain = (RadioButton)  findViewById(R.id.radioButton);
        radioclassify = (RadioButton)  findViewById(R.id.radioButton2);

        cekduduk = (CheckBox) findViewById(R.id.checkBox);
        cekberdiri = (CheckBox) findViewById(R.id.checkBox2);
        cekberjalan = (CheckBox) findViewById(R.id.checkBox3);

        cekduduk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cekberdiri.setChecked(false);
                cekberjalan.setChecked(false);
                condition = "duduk";
            }
        });

        cekberdiri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cekduduk.setChecked(false);
                cekberjalan.setChecked(false);
                condition = "berdiri";
            }
        });

        cekberjalan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cekduduk.setChecked(false);
                cekberdiri.setChecked(false);
                condition = "berjalan";
            }
        });

        textduduk = (TextView) findViewById(R.id.textduduk);
        textberdiri = (TextView) findViewById(R.id.textberdiri);
        textberjalan = (TextView) findViewById(R.id.textberjalan);

        X = new ArrayList<Float>();
        Y = new ArrayList<Float>();
        Z = new ArrayList<Float>();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);


        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                SensorManager.SENSOR_DELAY_NORMAL);


    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            xVal = event.values[0];
            yVal = event.values[1];
            zVal = event.values[2];

            sx = "X Value : <font color = '#800080'> " + xVal + "</font>";
            sy = "Y Value : <font color = '#800080'> " + yVal + "</font>";
            sz = "Z Value : <font color = '#800080'> " + zVal + "</font>";


            X.add(xVal);
            Y.add(yVal);
            Z.add(zVal);

            if (X.size() == window) {
                float sumX = 0, sumY = 0, sumZ = 0;
                float stdX = 0, stdY = 0, stdZ = 0;
                float stdDevX = 0, stdDevY = 0, stdDevZ = 0;
                float meanX = 0, meanY = 0, meanZ = 0;

                for (int j = 0; j < X.size(); j++) {
                    sumX += X.get(j);
                    sumY += Y.get(j);
                    sumZ += Z.get(j);
                }
                meanX = sumX / window;
                meanY = sumY / window;
                meanZ = sumZ / window;

                for (int j = 0; j < X.size(); j++) {
                    stdX += (X.get(j) - meanX) * (X.get(j) - meanX);
                    stdY += (Y.get(j) - meanY) * (Y.get(j) - meanY);
                    stdZ += (Z.get(j) - meanZ) * (Z.get(j) - meanZ);
                }
                Collections.max(X);
                stdDevX = (float) Math.sqrt(stdX / (window - 1));
                stdDevY = (float) Math.sqrt(stdY / (window - 1));
                stdDevZ = (float) Math.sqrt(stdZ / (window - 1));
                String data = String.valueOf(meanX) + ";" + String.valueOf(meanY) + ";" + String.valueOf(meanZ) + ';' + String.valueOf(stdDevX) + ';' + String.valueOf(stdDevY) + ';' + String.valueOf(stdDevZ)
                        + ';' + String.valueOf(Collections.max(X)) + ';' + String.valueOf(Collections.max(Y)) + ';' + String.valueOf(Collections.max(Z)) + ';' + String.valueOf(Collections.min(X))
                        + ';' + String.valueOf(Collections.min(Y)) + ';' + String.valueOf(Collections.min(Z) + ';' + condition);
                Log.d("a", data);

                try {
                    writer = new CSVWriter(new FileWriter(path + File.separator + "accel.csv", true), ',');
                    String[] entries = data.split(";"); // array of your values
                    Log.d("a", entries[0]);
                    writer.writeNext(entries);
                    writer.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                X.clear();
                Y.clear();
                Z.clear();

            }
        }

            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {

                currentDis = event.values[0];
                def = "You Can't See This";

                if (currentDis < 2) {

                    started = true;

                    viewx.setText(Html.fromHtml(sx));
                    viewy.setText(Html.fromHtml(sy));
                    viewz.setText(Html.fromHtml(sz));
                    cond.setText(Html.fromHtml(def));

                    String data = "MeanX;MeanY;MeanZ;StdDevX;StdDevY;StdDevZ;MaxX;MaxY;MaxZ;MinX;MinY;MinZ;Class";

                    try {
                        writer = new CSVWriter(new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + condition + ".csv"), ',');
                        String[] entries = data.split(";"); // array of your values
                        writer.writeNext(entries);
                        writer.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {

                    started = false;

                    viewx.setText(getString(R.string.textView2));
                    viewy.setText(getString(R.string.textView3));
                    viewz.setText(getString(R.string.textView4));
                    cond.setText(getString(R.string.textView1));

                }
            }




    }

    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // Is the button now checked?
        if(checkedId == R.id.radioButton) {
            Toast.makeText(getApplicationContext(), "KODE TRAIN DISINI",
                    Toast.LENGTH_SHORT).show();
    } else if(checkedId == R.id.radioButton2) {
            Toast.makeText(getApplicationContext(), "KODE CLASSIFY DISINI",
                    Toast.LENGTH_SHORT).show();

    }

    }

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int ijin = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (ijin != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity, permission, request);
        }
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
    }

}



