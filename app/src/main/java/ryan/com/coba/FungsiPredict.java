package ryan.com.coba;

import android.content.Intent;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
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

import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.opencsv.CSVWriter;


public class FungsiPredict extends Activity implements SensorEventListener {


    private SensorManager sensorManager;
    private File path;
    private int activity;
    private static final int request = 1;

    private Sensor accel;
    private Sensor prox;

    TextView viewx;
    TextView viewy;
    TextView viewz;
    TextView cond;

    private float xVal, yVal, zVal;
    private ArrayList<Float> X, Y, Z;
    private int window = 20;
    private double aktivitas;
    private boolean started = false;
    private boolean stopped = true;

    float currentDis;
    String sx, sy, sz, def;

    CSVWriter writer;

    private Instances data;
    private Classifier knn;
    private ArrayList<String> kelas;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] permission = {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict);

        if (shouldAskPermissions()) {
            verifyStoragePermissions(this);
        }

        Button btn = (Button)findViewById(R.id.button);

        path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);


        cond = (TextView) findViewById(R.id.textView1);
        viewx = (TextView) findViewById(R.id.textView2);
        viewy = (TextView) findViewById(R.id.textView3);
        viewz = (TextView) findViewById(R.id.textView4);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        prox = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        sensorManager.registerListener(this, accel,
                SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this, prox,
                SensorManager.SENSOR_DELAY_NORMAL);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Intent donat = new Intent(FungsiPredict.this, FungsiUtama.class); //intent kusus untuk pindah activity
                startActivity(donat);
            }


        });

        X = new ArrayList<Float>();
        Y = new ArrayList<Float>();
        Z = new ArrayList<Float>();
        knn = new IBk(4);
        try{
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(path + File.separator + "acc.csv"); //dataset
                    data = source.getDataSet();
            if (data.classIndex() == -1)
                data.setClassIndex(data.numAttributes() - 1); //set baris pertama sendiri buat nama atribut
            knn.buildClassifier(data);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Attribute att = data.classAttribute();
        for(int i = 0; i < data.numClasses();i++) {
            Log.d("g",att.value(i));
        }
    }


    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            xVal = event.values[0];
            yVal = event.values[1];
            zVal = event.values[2];

            sx = "X Value : <font color = '#800080'> " + xVal + "</font>";
            sy = "Y Value : <font color = '#800080'> " + yVal + "</font>";
            sz = "Z Value : <font color = '#800080'> " + zVal + "</font>";

            if (started) {
                X.add(xVal);
                Y.add(yVal);
                Z.add(zVal);
            }

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
                double[] val = new double[] {meanX, meanY, meanZ, stdDevX, stdDevY, stdDevZ, Collections.max(X), Collections.max(Y), Collections.max(Z), Collections.min(X),  Collections.min(Y), Collections.min(Z)};
                Instance instance = new DenseInstance(12, val); //konvert ke instance dulu biar bisa dicompare sama dataset
                try {
                    aktivitas = knn.classifyInstance(instance);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("akt",String.valueOf(aktivitas));
                cond.setText(data.classAttribute().value((int)aktivitas)); //print teks dari kelas yang tadinya hasilnya angka jadi diambil teksnya
                X.clear();
                Y.clear();
                Z.clear();
            }

        }

        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {


            currentDis = event.values[0];


            if (currentDis < 5) {

                started = true;

                viewx.setText(Html.fromHtml(sx));
                viewy.setText(Html.fromHtml(sy));
                viewz.setText(Html.fromHtml(sz));


            } else {

                started = false;

                viewx.setText(getString(R.string.textView2));
                viewy.setText(getString(R.string.textView3));
                viewz.setText(getString(R.string.textView4));
                //cond.setText(getString(R.string.textView1));

            }
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