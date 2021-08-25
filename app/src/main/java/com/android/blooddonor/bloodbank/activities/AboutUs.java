package com.android.blooddonor.bloodbank.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.blooddonor.bloodbank.R;

public class AboutUs extends AppCompatActivity {
        ImageView gmail1,reva;
        TextView insta1,insta2,insta3,insta4;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.aboutus);
            getSupportActionBar().hide();
            reva=findViewById(R.id.re);
            insta1=findViewById(R.id.name1);
            insta2=findViewById(R.id.name2);
            insta3=findViewById(R.id.name3);
            insta4=findViewById(R.id.name4);
            gmail1=findViewById(R.id.gmail1);
            reva.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://reva.edu.in/"));
                    startActivity(intent);

                }

            });

            insta1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("https://instagram.com/rahul.rajpur0hit?igshid=1at0r5ta4au2y");
                    Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                    likeIng.setPackage("com.instagram.android");
                    try {
                        startActivity(likeIng);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://instagram.com/rahul.rajpur0hit?igshid=1at0r5ta4au2y")));
                    }
                }
            });

            insta2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("https://instagram.com/the_jitian?igshid=oqbdmkqlp4mo");
                    Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                    likeIng.setPackage("com.instagram.android");

                    try {
                        startActivity(likeIng);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://instagram.com/the_jitian?igshid=oqbdmkqlp4mo")));
                    }
                }
            });
            insta3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("https://instagram.com/deepak_h__3720?igshid=sik7qhdfu7be");
                    Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                    likeIng.setPackage("com.instagram.android");

                    try {
                        startActivity(likeIng);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://instagram.com/deepak_h__3720?igshid=sik7qhdfu7be")));
                    }
                }
            });
            insta4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("https://www.instagram.com/invites/contact/?i=nqdk3h01eo18&utm_content=3s8bkjx");
                    Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                    likeIng.setPackage("com.instagram.android");

                    try {
                        startActivity(likeIng);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.instagram.com/invites/contact/?i=nqdk3h01eo18&utm_content=3s8bkjx")));
                    }
                }
            });
            gmail1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] TO = {"yorahul2535@gmail.com"};
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.setType("text/plain");

                    emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regarding Blood Donation");


                    try {
                        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                        Toast.makeText(getApplicationContext(),"Redirecting to Email Client",Toast.LENGTH_SHORT);
                        finish();
                    }
                    catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getApplicationContext(),
                                "There is no email client installed.", Toast.LENGTH_SHORT).show();
                    }


                }

            });

        }

    public void backbb(View view) {

        onBackPressed();
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
    }
