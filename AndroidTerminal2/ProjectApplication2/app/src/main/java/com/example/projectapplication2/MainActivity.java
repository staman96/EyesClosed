package com.example.projectapplication2;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.Manifest;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener , AdapterView.OnItemSelectedListener {

    private LocationManager locationManager;
    private LocationListener listener;

    private Sensor mySensor;
    private SensorManager SM;



    private Button btnpub;
    private Button btnsub;
    private Button btnunsub;
    private Button btndis;
    private Button btnclose;

    String lons ;
    String lats ;

    private static final int CAMERA_REQUEST = 50;
    private boolean flashLightStatus = false;

    NotificationCompat.Builder notification;


    private long lastUpdate = 0;
    private long gpsUpdate = 0;

    private long freq = 5000;

    Handler handler = new Handler();

    boolean play = true ;


    String clientId = MqttClient.generateClientId();
    final MqttAndroidClient client =
            new MqttAndroidClient(MainActivity.this , "tcp://192.168.1.8:1883",
                    clientId);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final MediaPlayer alert = MediaPlayer.create(this, R.raw.alertsound);

        Spinner spinner = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);



        ////////////////
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {

                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                Boolean hasconnection = false;

                if((cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting())==hasconnection){

                    //Toast.makeText(MainActivity.this,"foc",Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder nocon = new AlertDialog.Builder(MainActivity.this);
                    nocon.setTitle(R.string.app_name);
                    nocon.setIcon(R.mipmap.ic_launcher);
                    nocon.setMessage("No internet connection")
                            .setCancelable(false)
                            .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                                }
                            })
                            .setNegativeButton("Check if problem solved", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = nocon.create();
                    alert.show();
                }





                handler.postDelayed(this, 10000);
            }
        };

        handler.post(runnableCode);
        //////////////



        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(MainActivity.this,"connected",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(MainActivity.this,"not connected",Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }


        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                //new String(message.getPayload());
                String msg = message.toString();
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST);
                if (msg.equals("A")){
                    notification = new NotificationCompat.Builder(MainActivity.this,"1");
                    notification.setAutoCancel(true);
                    notification.setSmallIcon(R.drawable.ic_launcher_background);
                    notification.setTicker("Danger!!");
                    notification.setWhen(System.currentTimeMillis());
                    notification.setContentTitle("Danger!!");
                    notification.setContentText("Danger from own actions");
                    notification.setVibrate(new long[] { 0, 1000, 1000, 1000, 1000 });
                    notification.setLights(Color.GREEN, 300, 300);
                    Uri alarmSound = RingtoneManager.getActualDefaultRingtoneUri(MainActivity.this, RingtoneManager.TYPE_NOTIFICATION);
                    notification.setSound(alarmSound);


                    //Builds notification and issues it
                    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    alert.setLooping(true);
                    alert.start();

                }
                if (msg.equals("B")){
                    flashLightOn();
                    notification = new NotificationCompat.Builder(MainActivity.this,"1");
                    notification.setAutoCancel(true);
                    notification.setSmallIcon(R.drawable.ic_launcher_background);
                    notification.setTicker("Danger!!");
                    notification.setWhen(System.currentTimeMillis());
                    notification.setContentTitle("Danger!!");
                    notification.setContentText("Danger from other user's actions");
                    notification.setVibrate(new long[] { 0, 1000, 1000, 1000, 1000 });
                    notification.setLights(Color.GREEN, 300, 300);
                    Uri alarmSound = RingtoneManager.getActualDefaultRingtoneUri(MainActivity.this, RingtoneManager.TYPE_NOTIFICATION);
                    notification.setSound(alarmSound);


                    //Builds notification and issues it
                    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    alert.setLooping(true);
                    alert.start();

                }
                if (msg.equals("C")){
                    flashLightOff();
                    alert.pause();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });



        btnpub = (Button) findViewById(R.id.btnpub);
        btnpub.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View view){



                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);



                listener = new LocationListener() {


                    @Override
                    public void onLocationChanged(Location location) {


                        lons =  String.valueOf(location.getLongitude());
                        lats =  String.valueOf(location.getLatitude());



                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                        Intent iset = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(iset);

                    }
                };




                configure_button();

                // Create our Sensor Manager
                SM = (SensorManager)getSystemService(SENSOR_SERVICE);

                // Accelerometer Sensor
                mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

                // Register sensor Listener
                SM.registerListener(MainActivity.this,mySensor,0);



            }
        });

        btnsub = (Button) findViewById(R.id.btnsub);
        btnsub.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View view){



                String topic = "D";
                int qos = 1;
                try {
                    IMqttToken subToken = client.subscribe(topic, qos);
                    subToken.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // The message was published
                            Toast.makeText(MainActivity.this,"subbed",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken,
                                              Throwable exception) {
                            // The subscription could not be performed, maybe the user was not
                            // authorized to subscribe on the specified topic e.g. using wildcards
                            Toast.makeText(MainActivity.this,"not subbed",Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        btnunsub = (Button) findViewById(R.id.btnunsub);
        btnunsub.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View view){



                final String topic = "D";
                try {
                    IMqttToken unsubToken = client.unsubscribe(topic);
                    unsubToken.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // The subscription could successfully be removed from the client
                            Toast.makeText(MainActivity.this,"unsubbed",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken,
                                              Throwable exception) {
                            // some error occurred, this is very unlikely as even if the client
                            // did not had a subscription to the topic the unsubscribe action
                            // will be successfully
                            Toast.makeText(MainActivity.this,"not unsubbed",Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }


            }
        });


        btndis = (Button) findViewById(R.id.btndis);
        btndis.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View view) {



                try {
                    IMqttToken disconToken = client.disconnect();
                    disconToken.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // we are now successfully disconnected
                            Toast.makeText(MainActivity.this, "disconnected", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken,
                                              Throwable exception) {
                            // something went wrong, but probably we are disconnected anyway
                            Toast.makeText(MainActivity.this, "not disconnected", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        btnclose = (Button) findViewById(R.id.btnclose);
        btnclose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View view) {
                flashLightOff();
            }
        });

    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Do you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif: all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b: macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {}
        return "02:00:00:00:00:00";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                configure_button();
                break;
            case CAMERA_REQUEST :
                if (grantResults.length > 0  &&  grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied for the Camera", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    void configure_button(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{ android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not in use
    }

    @Override
    public void onSensorChanged(SensorEvent event) {


        final String xac =  String.valueOf(event.values[0]);
        final String yac =  String.valueOf(event.values[1]);
        final String zac =  String.valueOf(event.values[2]);

        String topic = "C";

        String plma = getMacAddr() ;

        long curTime = System.currentTimeMillis();

        if ((curTime - lastUpdate) > freq ) {

            Toast.makeText(MainActivity.this,"in osc",Toast.LENGTH_SHORT).show();



            readcsv();

            String sendx = "xac/"+xac;
            sendx = sendx + "/";
            sendx = sendx + plma ;
            byte[] encodedPayload1 = new byte[0];
            try {
                encodedPayload1 = sendx.getBytes("UTF-8");
                MqttMessage message1 = new MqttMessage(encodedPayload1);
                client.publish(topic, message1);
            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }

            String sendy = "yac/"+yac;
            sendy = sendy + "/";
            sendy = sendy + plma ;
            byte[] encodedPayload2 = new byte[0];
            try {
                encodedPayload2 = sendy.getBytes("UTF-8");
                MqttMessage message2 = new MqttMessage(encodedPayload2);
                client.publish(topic, message2);
            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }

            String sendz = "zac/"+zac;
            sendz = sendz + "/";
            sendz = sendz + plma ;
            byte[] encodedPayload3 = new byte[0];
            try {
                encodedPayload3 = sendz.getBytes("UTF-8");
                MqttMessage message3 = new MqttMessage(encodedPayload3);
                client.publish(topic, message3);
            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }



            String sendlon = "lon/"+lons;
            sendlon = sendlon + "/";
            sendlon = sendlon + plma ;
            byte[] encodedPayload4 = new byte[0];
            try {
                encodedPayload4 = sendlon.getBytes("UTF-8");
                MqttMessage message4 = new MqttMessage(encodedPayload4);
                client.publish(topic, message4);
            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }

            String sendlat = "lat/"+lats;
            sendlat = sendlat + "/";
            sendlat = sendlat + plma ;
            byte[] encodedPayload5 = new byte[0];
            try {
                encodedPayload5 = sendlat.getBytes("UTF-8");
                MqttMessage message5 = new MqttMessage(encodedPayload5);
                client.publish(topic, message5);
            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }

            String sendtop = "top/"+"D";
            sendtop = sendtop + "/";
            sendtop = sendtop + plma ;
            byte[] encodedPayload6 = new byte[0];
            try {
                encodedPayload6 = sendtop.getBytes("UTF-8");
                MqttMessage message6 = new MqttMessage(encodedPayload6);
                client.publish(topic, message6);
            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }

            long diffTime = (curTime - lastUpdate);
            lastUpdate = curTime;
        }



    }


    private void flashLightOn() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
            flashLightStatus = true;
        } catch (CameraAccessException e) {
        }
    }

    private void flashLightOff() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
            flashLightStatus = false;
        } catch (CameraAccessException e) {
        }
    }

    private  void  readcsv(){


        Random r = new Random();
        int csch = r.nextInt(36) ;

        String line = "";
        String csvf = "";


        String csvname = "";

        if (csch == 0) {
            InputStream is = getResources().openRawResource(R.raw.eyesclosedchristos1_2);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesclosedchristos1_2";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }
        if (csch == 1) {
            InputStream is = getResources().openRawResource(R.raw.eyesclosedchristos2_3);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesclosedchristos2_3";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 2) {
            InputStream is = getResources().openRawResource(R.raw.eyesclosedchristos_1);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesclosedchristos_1";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 3) {
            InputStream is = getResources().openRawResource(R.raw.eyesclosedlina1_5);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesclosedlina1_5";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 4) {
            InputStream is = getResources().openRawResource(R.raw.eyesclosedlina2_6);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesclosedlina2_6";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 5) {
            InputStream is = getResources().openRawResource(R.raw.eyesclosedlina_4);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesclosedlina_4";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 6) {
            InputStream is = getResources().openRawResource(R.raw.eyesclosedmichalis1_8);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesclosedmichalis1_8";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 7) {
            InputStream is = getResources().openRawResource(R.raw.eyesclosedmichalis2_9);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesclosedmichalis2_9";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line+ "," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 8) {
            InputStream is = getResources().openRawResource(R.raw.eyesclosedmichalis_7);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesclosedmichalis_7";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 9) {
            InputStream is = getResources().openRawResource(R.raw.eyesclosednickolas1_11);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesclosednickolas1_11";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 10) {
            InputStream is = getResources().openRawResource(R.raw.eyesclosednickolas2_12);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesclosednickolas2_12";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 11) {
            InputStream is = getResources().openRawResource(R.raw.eyesclosednickolas_10);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesclosednickolas_10";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 12) {
            InputStream is = getResources().openRawResource(R.raw.eyesclosedpanos1_14);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesclosedpanos1_14";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 13) {
            InputStream is = getResources().openRawResource(R.raw.eyesclosedpanos2_15);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesclosedpanos2_15";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 14) {
            InputStream is = getResources().openRawResource(R.raw.eyesclosedpanos_13);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesclosedpanos_13";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 15) {
            InputStream is = getResources().openRawResource(R.raw.eyesclosedtakis1_17);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesclosedtakis1_17";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 16) {
            InputStream is = getResources().openRawResource(R.raw.eyesclosedtakis2_18);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesclosedtakis2_18";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 17) {
            InputStream is = getResources().openRawResource(R.raw.eyesclosedtakis_16);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesclosedtakis_16";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 18) {
            InputStream is = getResources().openRawResource(R.raw.eyesopenedchristos1_2);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesopenedchristos1_2";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 19) {
            InputStream is = getResources().openRawResource(R.raw.eyesopenedchristos2_3);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesopenedchristos2_3";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 20) {
            InputStream is = getResources().openRawResource(R.raw.eyesopenedchristos_1);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesopenededchristos_1" ;

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 21) {
            InputStream is = getResources().openRawResource(R.raw.eyesopenedlina1_5);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesclosedlina1_5";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 22) {
            InputStream is = getResources().openRawResource(R.raw.eyesopenedlina2_6);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesopenedlina2_6";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 23) {
            InputStream is = getResources().openRawResource(R.raw.eyesopenedlina_4);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesclosedlina_4";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 24) {
            InputStream is = getResources().openRawResource(R.raw.eyesopenedmichalis1_8);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesopenedmichalis1_8";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 25) {
            InputStream is = getResources().openRawResource(R.raw.eyesopenedmichalis2_9);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesopenedmichalis2_9";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 26) {
            InputStream is = getResources().openRawResource(R.raw.eyesopenedmichalis_7);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesopenedmichalis_7";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 27) {
            InputStream is = getResources().openRawResource(R.raw.eyesopenednickolas1_11);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesopenednickolas1_11";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 28) {
            InputStream is = getResources().openRawResource(R.raw.eyesopenednickolas2_12);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesopenednicholas2_12";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 29) {
            InputStream is = getResources().openRawResource(R.raw.eyesopenednickolas_10);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesopenednickolas_10";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 30) {
            InputStream is = getResources().openRawResource(R.raw.eyesopenedpanos1_14);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesopenedpanos1_14";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 31) {
            InputStream is = getResources().openRawResource(R.raw.eyesopenedpanos2_15);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesopenedpanos2_15";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 32) {
            InputStream is = getResources().openRawResource(R.raw.eyesopenedpanos_13);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesopenedpanos_13";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 33) {
            InputStream is = getResources().openRawResource(R.raw.eyesopenedtakis1_17);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesopenedtakis1_17";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 34) {
            InputStream is = getResources().openRawResource(R.raw.eyesopenedtakis2_18);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesopenedtakis2_18";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }if (csch == 35) {
            InputStream is = getResources().openRawResource(R.raw.eyesopenedtakis_16);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            csvname = "/eyesopenedtakis_16";

            try{
                while ( (line = reader.readLine()) != null ){


                    csvf = csvf + line +"," ;

                }
            }
            catch (IOException e){
            }
        }


        String plma = getMacAddr();

        String secsv = "csv/" + csvf;
        secsv = secsv + "/";
        secsv = secsv + plma ;
        secsv = secsv + csvname;


        String topic = "C";


        byte[] encodedPayload1 = new byte[0];
        try {
            encodedPayload1 = secsv.getBytes("UTF-8");
            MqttMessage message1 = new MqttMessage(encodedPayload1);
            client.publish(topic, message1);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }



    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        int seconds = Integer.parseInt(text);
        freq = seconds * 1000 ;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}


