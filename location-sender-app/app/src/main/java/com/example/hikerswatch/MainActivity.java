package com.example.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;

    Button send;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        }
    }

    public void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void updateLocationInfo(Location location) {
        Log.i("Location", location.toString());

        TextView latTextView = (TextView) findViewById(R.id.latTextView);
        TextView lonTextView = (TextView) findViewById(R.id.lonTextView);
        TextView altTextView = (TextView) findViewById(R.id.altTextView);
        TextView accTextView = (TextView) findViewById(R.id.accTextView);

        latTextView.setText("Latitude= " + location.getLatitude());
        lonTextView.setText("Longitude= " + location.getLongitude());
        altTextView.setText("Altitude= " + location.getAltitude());
        accTextView.setText("Accuracy= " + location.getAccuracy());

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            String address = "Could not find address";
            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (listAddresses != null && listAddresses.size() > 0) {
                Log.i("PlaceInfo", listAddresses.get(0).toString());

                address = "Address= ";

                if (listAddresses.get(0).getThoroughfare() != null) {
                    address += listAddresses.get(0).getThoroughfare() + "\n";
                }
                if (listAddresses.get(0).getLocality() != null) {
                    address += listAddresses.get(0).getLocality() + "\n";
                }
                if (listAddresses.get(0).getPostalCode() != null) {
                    address += listAddresses.get(0).getPostalCode() + "\n";
                }
                if (listAddresses.get(0).getCountryName() != null) {
                    address += listAddresses.get(0).getCountryName() + "\n";
                }

                TextView addressTextView = (TextView) findViewById(R.id.addressTextView);
                addressTextView.setText(address);

                /*String s= addressTextView.getText().toString() + location.getLongitude() + location.getLatitude();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("9638285178",null, address, null, null);*/

                Toast.makeText(MainActivity.this,addressTextView.getText().toString(),Toast.LENGTH_SHORT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener= new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                updateLocationInfo(location);
                //send_sms(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(Build.VERSION.SDK_INT<23){
            startListening();
        }
        else{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location location= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location!=null){
                    updateLocationInfo(location);
                }
            }
        }

        send = findViewById(R.id.b1);

        send.setEnabled(false);
        if(checkPermission(Manifest.permission.SEND_SMS)){
            send.setEnabled(true);
        }else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
        }
    }
    public void onSend(View v){
        TextView lati= (TextView) findViewById(R.id.latTextView);
        TextView longi=(TextView) findViewById(R.id.lonTextView);
        TextView acc=(TextView) findViewById(R.id.accTextView);
        TextView alt=(TextView)findViewById(R.id.altTextView);
        TextView address=(TextView) findViewById(R.id.addressTextView);

        String s= lati.getText().toString() + longi.getText().toString() + acc.getText().toString() + alt.getText().toString() + address.getText().toString();

        if(checkPermission(Manifest.permission.SEND_SMS)){
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("9638285178", null,s, null, null);
            Toast.makeText(this, "Message Sent!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkPermission(String permission){
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

}
