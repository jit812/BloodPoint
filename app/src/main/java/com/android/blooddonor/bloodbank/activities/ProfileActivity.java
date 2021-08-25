package com.android.blooddonor.bloodbank.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.BaseMenuPresenter;
import androidx.core.app.ActivityCompat;

import com.android.blooddonor.bloodbank.R;
import com.android.blooddonor.bloodbank.viewmodels.UserData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ProfileActivity extends AppCompatActivity {

    private EditText inputemail, inputpassword, retypePassword, fullName, contact;
    TextView division,locality,address,otpbtn,Verify_userotp;
    private TextInputLayout e1,e2;
    EditText otptext;
    private FirebaseAuth mAuth;
    private Button btnSignup;
    private MyProgressDialog pd;
    private int verifyed = 0;
    private Spinner gender, bloodgroup;
    private String cityName,stateName;
    private String verificationId;
    ImageView CurrentLoc;
    LinearLayout otplayout,emailLayout;
    Location location;

    private boolean isUpdate = true;

    private DatabaseReference db_ref, donor_ref;
    private FirebaseDatabase db_User;
    private CheckBox isDonor;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pd = new MyProgressDialog(ProfileActivity.this);
        pd.setMessage("Loading. Please wait...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();


        db_User = FirebaseDatabase.getInstance();
        db_ref = db_User.getReference("users");
        donor_ref = db_User.getReference("donors");
        mAuth = FirebaseAuth.getInstance();

        inputemail = findViewById(R.id.input_userEmail);
        inputpassword = findViewById(R.id.input_password);
        retypePassword = findViewById(R.id.input_password_confirm);
        fullName = findViewById(R.id.input_fullName);
        gender = findViewById(R.id.gender);
        address = findViewById(R.id.inputAddress);
        division = findViewById(R.id.state);
        bloodgroup = findViewById(R.id.inputBloodGroup);
        contact = findViewById(R.id.inputMobile);
        isDonor = findViewById(R.id.checkbox);
        locality = findViewById(R.id.locality);
        CurrentLoc = findViewById(R.id.currloc);
        btnSignup = findViewById(R.id.button_register);
        otplayout = findViewById(R.id.otp_layout);
        otpbtn = findViewById(R.id.verify_otp);
        Verify_userotp = findViewById(R.id.verify_userotp);
        otptext = findViewById(R.id.verify_otptext);
        e1=findViewById(R.id.eye1);
        e2=findViewById(R.id.eye2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (mAuth.getCurrentUser() != null) {
            isUpdate=false;
            verifyed = 1;
            inputemail.setVisibility(View.GONE);
            inputpassword.setVisibility(View.GONE);
            retypePassword.setVisibility(View.GONE);
            e1.setVisibility(View.GONE);
            e2.setVisibility(View.GONE);
            otpbtn.setText("Verified");
            otpbtn.setEnabled(false);
            btnSignup.setText("Update Profile");
            pd.dismiss();
            getSupportActionBar().setTitle("Profile");
            findViewById(R.id.kar39).setVisibility(View.GONE);
            findViewById(R.id.image_logo).setVisibility(View.GONE);
            final Query Profile = db_ref.child(mAuth.getCurrentUser().getUid());
            Profile.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserData userData = dataSnapshot.getValue(UserData.class);
                    if (userData != null) {
                        pd.show();
                        fullName.setText(userData.getName());
                        gender.setSelection(userData.getGender());
                        address.setText(userData.getAddress());
                        contact.setText(userData.getContact());
                        bloodgroup.setSelection(userData.getBloodGroup());
                        division.setText(userData.getDivision());
                        locality.setText(userData.getLocality());
                        Query donor =  donor_ref.child((String) division.getText()).child(bloodgroup.getSelectedItem().toString()).child(mAuth.getCurrentUser().getUid());
                        donor.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    isDonor.setChecked(true);
                                    isDonor.setText("Unmark this to leave from donors");
                                } else {
                                    Toast.makeText(ProfileActivity.this, "Your are not a donor! Be a donor and save life by donating blood.",
                                            Toast.LENGTH_LONG).show();
                                }
                                pd.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("User", databaseError.getMessage());
                            }

                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("User", databaseError.getMessage());
                }
            });

        } else
            pd.dismiss();
        otpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String Contact = contact.getText().toString();
                if (Contact.length() < 9) {
                    ShowError("Contact Number");
                    contact.requestFocusFromTouch();
                }else {
                    otplayout.setVisibility(View.VISIBLE);
                    Toast.makeText(ProfileActivity.this,"OTP Sent.",Toast.LENGTH_SHORT).show();
                    String phone = "+91" + Contact;
                    sendVerificationCode(phone);
                }
                Verify_userotp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(otptext.getText().toString())) {
                            Toast.makeText(ProfileActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                        } else {
                            verifyCode(otptext.getText().toString());
                        }
                    }
                });
            }
        });
        CurrentLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                // method to get the location
                getLastLocation();
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputemail.getText().toString();
                final String password = inputpassword.getText().toString();
                final String ConfirmPassword = retypePassword.getText().toString();
                final String Name = fullName.getText().toString();
                final int Gender = gender.getSelectedItemPosition();
                final String Contact = contact.getText().toString();
                final int BloodGroup = bloodgroup.getSelectedItemPosition();
                final String Address = address.getText().toString();
                final String Division = division.getText().toString();
                final String blood = bloodgroup.getSelectedItem().toString();
                final String div = division.getText().toString();
                final String Locality = (String) locality.getText();
                //final String loc = locality.getSelectedItem().toString();
                try {
                    if (Name.length() <= 2) {
                        ShowError("Name");
                        fullName.requestFocusFromTouch();
                    } else if (Contact.length() < 9) {
                        ShowError("Contact Number");
                        contact.requestFocusFromTouch();
                    } else if (Address.length() <= 2) {
                        ShowError("Address");
                        address.requestFocusFromTouch();
                    }else if(verifyed == 0){
                        ShowError("Phone verification");
                        address.requestFocusFromTouch();
                    }
                    else {
                        if (isUpdate) {
                            if (email.length() == 0) {
                                ShowError("Email ID");
                                inputemail.requestFocusFromTouch();
                            } else if (password.length() <= 5) {
                                ShowError("Password");
                                inputpassword.requestFocusFromTouch();
                            } else if (password.compareTo(ConfirmPassword) != 0) {
                                Toast.makeText(ProfileActivity.this, "Password did not match!", Toast.LENGTH_LONG)
                                        .show();
                                retypePassword.requestFocusFromTouch();
                            } else {
                                pd.show();
                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(ProfileActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {

                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(ProfileActivity.this, "Registration failed! try agian.", Toast.LENGTH_LONG)
                                                            .show();
                                                    Log.v("error", task.getException().getMessage());
                                                } else {
                                                    String id = mAuth.getCurrentUser().getUid();
                                                    db_ref.child(id).child("Name").setValue(Name);
                                                    db_ref.child(id).child("Gender").setValue(Gender);
                                                    db_ref.child(id).child("Contact").setValue(Contact);
                                                    db_ref.child(id).child("BloodGroup").setValue(BloodGroup);
                                                    db_ref.child(id).child("Address").setValue(Address);
                                                    db_ref.child(id).child("Division").setValue(Division);
                                                    db_ref.child(id).child("Locality").setValue(Locality);
                                                    db_ref.child(id).child("Latitude").setValue(location.getLatitude());
                                                    db_ref.child(id).child("Longitude").setValue(location.getLongitude());

                                                    if (isDonor.isChecked()) {
                                                        donor_ref.child(div).child(blood).child(id).child("UID").setValue(id).toString();
                                                        donor_ref.child(div).child(blood).child(id).child("LastDonate").setValue("Don't donate yet!");
                                                        donor_ref.child(div).child(blood).child(id).child("TotalDonate").setValue(0);
                                                        donor_ref.child(div).child(blood).child(id).child("Name").setValue(Name);
                                                        donor_ref.child(div).child(blood).child(id).child("Contact").setValue(Contact);
                                                        donor_ref.child(div).child(blood).child(id).child("Address").setValue(Address);
                                                        donor_ref.child(div).child(blood).child(id).child("Locality").setValue(Locality);
                                                        donor_ref.child(div).child(blood).child(id).child("Latitude").setValue(location.getLatitude());
                                                        donor_ref.child(div).child(blood).child(id).child("Longitude").setValue(location.getLongitude());
                                                    }
                                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(getApplicationContext(), "Please check your email for verification", Toast.LENGTH_LONG)
                                                                        .show();
                                                            }
                                                            else {
                                                                Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_LONG)
                                                                        .show();
                                                            }
                                                        }
                                                    });
                                                    finish();
                                                }
                                                pd.dismiss();

                                            }

                                        });
                            }

                        } else {
                            String id = mAuth.getCurrentUser().getUid();
                            db_ref.child(id).child("Name").setValue(Name);
                            db_ref.child(id).child("Gender").setValue(Gender);
                            db_ref.child(id).child("Contact").setValue(Contact);
                            db_ref.child(id).child("BloodGroup").setValue(BloodGroup);
                            db_ref.child(id).child("Address").setValue(Address);
                            db_ref.child(id).child("Division").setValue(Division);
                            db_ref.child(id).child("Locality").setValue(Locality);
                            donor_ref.child(div).child(blood).child(id).child("Latitude").setValue(location.getLatitude());
                            donor_ref.child(div).child(blood).child(id).child("Longitude").setValue(location.getLongitude());

                            if (isDonor.isChecked()) {
                                donor_ref.child(div).child(blood).child(id).child("UID").setValue(id).toString();
                                donor_ref.child(div).child(blood).child(id).child("Name").setValue(Name);
                                donor_ref.child(div).child(blood).child(id).child("Contact").setValue(Contact);
                                donor_ref.child(div).child(blood).child(id).child("Address").setValue(Address);
                                donor_ref.child(div).child(blood).child(id).child("Locality").setValue(Locality);
                                donor_ref.child(div).child(blood).child(id).child("Latitude").setValue(location.getLatitude());
                                donor_ref.child(div).child(blood).child(id).child("Longitude").setValue(location.getLongitude());
                            } else {
                                donor_ref.child(div).child(blood).child(id).removeValue();
                            }
                            Toast.makeText(getApplicationContext(), "Your account has been updated!", Toast.LENGTH_LONG)
                                    .show();
                            Intent intent = new Intent(ProfileActivity.this, Dashboard.class);
                            startActivity(intent);
                            finish();
                        }
                        pd.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        // method to get the location
        getLastLocation();
    }
    private void sendVerificationCode(String number) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth).setPhoneNumber(number).setTimeout(60L, TimeUnit.SECONDS).setActivity(this).setCallbacks(mCallBack).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Verify_userotp.setVisibility(View.VISIBLE);
            verificationId = s;
        }
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            final String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                otptext.setText(code);
                verifyCode(code);
            }
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }
    private void signInWithCredential(PhoneAuthCredential credential) {
        ProgressDialog pd = new ProgressDialog(ProfileActivity.this);
        pd.setTitle("Verification");
        pd.setMessage("Verifying OTP");
        pd.show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    verifyed = 1;
                                    otplayout.setVisibility(View.GONE);
                                    contact.setEnabled(false);
                                    otpbtn.setEnabled(false);
                                    otpbtn.setText("Verified");
                                    Toast.makeText(ProfileActivity.this,"Verified",Toast.LENGTH_LONG).show();
                                    pd.dismiss();
                                    btnSignup.performClick();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Toast.makeText(ProfileActivity.this,"Not Valid",Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            pd.dismiss();
                            Toast.makeText(ProfileActivity.this, "Enter a valid OTP!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
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
                        location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            address.setText(location.getLatitude() + "" + location.getLongitude() + "");
                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            List<Address> addresses = null;
                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                cityName = addresses.get(0).getAddressLine(0);
                                stateName = addresses.get(0).getAdminArea();
                                String countryName = addresses.get(0).getAddressLine(2);
                                address.setText(cityName + stateName + countryName);
                                locality.setText(cityName);
                                division.setText(stateName);
                            } catch (Exception e) {
                                Toast.makeText(ProfileActivity.this, "Check your Connection", Toast.LENGTH_LONG).show();
                            }
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

    @SuppressLint("MissingPermission")
    private void requestNewLocationData () {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            address.setText("Latitude: " + mLastLocation.getLatitude() + "" + "Longitude: " + mLastLocation.getLongitude() + "");
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            cityName = addresses.get(0).getSubLocality();
            stateName = addresses.get(0).getAdminArea();
            address.setText(cityName + "\n" + stateName);
            division.setText(stateName);
            locality.setText(cityName);
        }
    };

    // method to check for permissions
    private boolean checkPermissions () {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions () {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled () {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult( int requestCode, @NonNull String[] permissions,
                                @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }

        }
    }

    private void ShowError(String error) {

        Toast.makeText(ProfileActivity.this, "Please, verify "+error,
                Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void backbb(View view) {

        onBackPressed();
    }

}