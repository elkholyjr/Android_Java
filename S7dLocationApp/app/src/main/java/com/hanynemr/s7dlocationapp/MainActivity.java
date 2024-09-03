package com.hanynemr.s7dlocationapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mumayank.com.airlocationlibrary.AirLocation;

public class MainActivity extends AppCompatActivity implements AirLocation.Callback {
    TextView locText;

    AirLocation airLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locText=findViewById(R.id.locText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        airLocation.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void where(View view) {
        airLocation=new AirLocation(this,this,true,0,"");
        airLocation.start();//request system
    }

    @Override
    public void onFailure(@NonNull AirLocation.LocationFailedEnum locationFailedEnum) {
        Toast.makeText(this, "error in getting location", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(@NonNull ArrayList<Location> locations) {
        double latitude = locations.get(0).getLatitude();
        double longitude = locations.get(0).getLongitude();

        locText.setText("lat="+latitude+"-long="+longitude);

        Geocoder geocoder=new Geocoder(this);

        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            locText.append("\n"+addressList.get(0).getAddressLine(0));
        } catch (IOException e) {
            Toast.makeText(this, "connection error", Toast.LENGTH_SHORT).show();
        }


        //show on map
//        Intent a=new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=cairo+stadium+egypt"));
//        startActivity(a);

    }
}