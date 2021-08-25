package com.android.blooddonor.bloodbank.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.blooddonor.bloodbank.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class NearestDonor extends AppCompatActivity {
    SupportMapFragment supportMapFragment;
    private FirebaseAuth mAuth;
    private DatabaseReference donor_ref;
    private FirebaseDatabase db_User;
    private double srcLat, srcLng;
    private TextView dist;
    LinearLayout distLayout;
    Spinner spinner1, spinner2;
    Button searchDonor;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearest_donor);
        spinner1 = findViewById(R.id.btngetBloodGroup);
        spinner2 = findViewById(R.id.btngetDivison);
        db_User = FirebaseDatabase.getInstance();
        searchDonor = findViewById(R.id.btnSearchdonor);
        distLayout = findViewById(R.id.distance_layout);
        dist = findViewById(R.id.distance);
        getSupportActionBar().hide();
        donor_ref = db_User.getReference("donors");
        mAuth = FirebaseAuth.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(NearestDonor.this);
        getLastLocation();
        searchDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                distLayout.setVisibility(View.GONE);
                supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.Map);
                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onMapReady(final GoogleMap googleMap) {
                        googleMap.setMyLocationEnabled(true);
                        googleMap.clear();
                        LatLng pos = new LatLng(srcLat, srcLng);
                        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.myloc);
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 250, 250, false);
                        MarkerOptions options = new MarkerOptions().position(pos).title("You").icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                        googleMap.addMarker(options);
                        final String filter_state = spinner2.getSelectedItem().toString();
                        final String filter_BloodGrp = spinner1.getSelectedItem().toString();
                        Query Profile = donor_ref.child(filter_state).child(filter_BloodGrp);
                        Profile.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        String name = ds.getKey();
                                        try {
                                            final Double latitude = (double) snapshot.child(name).child("Latitude").getValue();
                                            final Double longitude = (double) snapshot.child(name).child("Longitude").getValue();
                                            String Name = snapshot.child(name).child("Name").getValue().toString();
                                            String contact = snapshot.child(name).child("Contact").getValue().toString();
                                            LatLng latLng = new LatLng(latitude, longitude);
                                            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.requestlogo);
                                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
                                            MarkerOptions options = new MarkerOptions().position(latLng).title(filter_BloodGrp + "," + Name + "," + contact).icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7));
                                            googleMap.addMarker(options);
                                        } catch (Exception e) {
                                            Toast.makeText(NearestDonor.this, "Check your connection!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    getLastLocation();
                                } else {
                                    getLastLocation();
                                    LatLng latLng = new LatLng(srcLat, srcLng);
                                    Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.myloc);
                                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 250, 250, false);
                                    MarkerOptions options = new MarkerOptions().position(latLng).title("You").icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                                    googleMap.addMarker(options);
                                    Toast.makeText(NearestDonor.this, "Oops! no donors in" + filter_state, Toast.LENGTH_SHORT).show();
                                }
                                getLastLocation();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(NearestDonor.this, "Check your connection!", Toast.LENGTH_LONG).show();
                            }
                        });
                        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                try {
                                    getLastLocation();
                                    googleMap.addPolyline(new PolylineOptions()
                                            .add(new LatLng(srcLat, srcLng),
                                                    new LatLng(marker.getPosition().latitude, marker.getPosition().longitude))
                                            .width(8).color(Color.RED).geodesic(true));
                                    Location startPoint = new Location("locationA");
                                    startPoint.setLatitude(srcLat);
                                    startPoint.setLongitude(srcLng);
                                    Location endPoint = new Location("locationB");
                                    endPoint.setLatitude(marker.getPosition().latitude);
                                    endPoint.setLongitude(marker.getPosition().longitude);
                                    double distance = startPoint.distanceTo(endPoint);
                                    distLayout.setVisibility(View.VISIBLE);
                                    dist.setText((double) distance / 1000 + " KM");
                                    Toast.makeText(NearestDonor.this, "" + distance / 1000 + "KM", Toast.LENGTH_LONG).show();
                                } catch (NullPointerException e) {
                                    System.out.println(e.getMessage());
                                    Toast.makeText(NearestDonor.this, "Oops! no donors in" + filter_state, Toast.LENGTH_SHORT).show();
                                }
                                return false;
                            }
                        });
                        getLastLocation();
                    }
                });
            }
        });
        getLastLocation();
    }
    @SuppressLint("MissingPermission")
    private void getLastLocation () {
        // check if permissions are given
        if (checkPermissions()) {
            // check if location is enabled
            if (isLocationEnabled()) {
                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            //text2.setText(location.getLatitude() + "" + location.getLongitude() + "");
                            srcLat = location.getLatitude();
                            srcLng = location.getLongitude();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }
    private boolean checkPermissions () {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    private boolean isLocationEnabled () {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    private void requestPermissions () {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }
    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            srcLat = mLastLocation.getLatitude();
            srcLng = mLastLocation.getLongitude();
        }
    };
    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);
        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(NearestDonor.this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }
    public void backbtn(View view) {

        onBackPressed();
    }
}