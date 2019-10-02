package com.akash.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    Button button;
    TextView textView1, textView2, textView3,textView4,textView5;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListening();
            }
        }
    }
    public void startListening(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=findViewById(R.id.button);



        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener= new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            Location lastKnownLocation= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastKnownLocation!=null){
                updateLocationInfo(lastKnownLocation);
            }
        }


    }
    public void goToMap(View view){
        Intent intent= new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }


    public void updateLocationInfo(Location location){
        textView1 = findViewById(R.id.textView2);
        textView2 = findViewById(R.id.textView3);
        textView3 = findViewById(R.id.textView4);
        textView4 = findViewById(R.id.textView5);
        textView5 = findViewById(R.id.textView6);

        textView1.setText("Latitude: "+Double.toString(location.getLatitude()));
        textView2.setText("Longitude: "+ Double.toString(location.getLongitude()));
        textView3.setText("Accuracy: "+ Float.toString(location.getAccuracy()));
        textView4.setText("Altitude: "+ Double.toString(location.getAltitude()));
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = "Address:\n";
            if (addresses != null && addresses.size() > 0) {
                Log.i("Address", addresses.get(0).toString());

               if (addresses.get(0).getThoroughfare() != null) {
                    address += addresses.get(0).getThoroughfare() + " ";
                }
                if (addresses.get(0).getLocality() != null) {
                    address += addresses.get(0).getLocality() + "\r\n";
                }
                if (addresses.get(0).getPostalCode() != null) {
                    address += addresses.get(0).getPostalCode() + "\r\n";
                }

                if (addresses.get(0).getAdminArea() != null) {
                    address += addresses.get(0).getAdminArea() + "\r\n";
                }
                if (addresses.get(0).getCountryName() != null) {
                    address += addresses.get(0).getCountryName();
                }
                }
            textView5.setText(address);
            /*try {
                String place = URLEncoder.encode(addresses.get(0).getLocality(), "UTF-8");
                DownloadTask task = new DownloadTask();
                task.execute("https://openweathermap.org/data/2.5/weather?q=" + place + "&appid=b6907d289e10d714a6e88b30761fae22");

            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
            }*/
        }catch (Exception e){
            e.printStackTrace();
        }




}
   /* public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpsURLConnection urlConnection=null;
            try{

                url = new URL(urls[0]);
                urlConnection = (HttpsURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data= reader.read();
                while(data!= -1){
                    char current = (char) data;
                    result += current;
                    data= reader.read();
                }
                return result;
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                return null;
            }



        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");

                JSONArray jsonArray = new JSONArray(weatherInfo);
                String message = "";
                for(int i=0 ; i<jsonArray.length();i++){
                    JSONObject jsonPart = jsonArray.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String description= jsonPart.getString("description");
                    if(!main.equals("") && !description.equals(""))
                        message+= main+ ": " + description+"\r\n";
                }
                //message+="Temperature:"+temp+"°C";
                if (!message.equals("")) {
                    String finalmsg="Weather:\n";
                    finalmsg+=message;

                    textView6.setText(finalmsg);
                }
                else
                    Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }*/
}

