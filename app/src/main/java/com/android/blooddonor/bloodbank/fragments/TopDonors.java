package com.android.blooddonor.bloodbank.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.blooddonor.bloodbank.R;
import com.android.blooddonor.bloodbank.activities.Dashboard;
import com.android.blooddonor.bloodbank.activities.MyProgressDialog;
import com.android.blooddonor.bloodbank.adapters.BloodRequestAdapter;
import com.android.blooddonor.bloodbank.adapters.SearchTopDonorAdapter;
import com.android.blooddonor.bloodbank.adapters.SearchTopDonorAdapter;
import com.android.blooddonor.bloodbank.viewmodels.CustomUserData;
import com.android.blooddonor.bloodbank.viewmodels.DonorData;
import com.android.blooddonor.bloodbank.viewmodels.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;


public class TopDonors extends Fragment {


    private View view;
    private RecyclerView recentPosts;

    DatabaseReference db_ref,donor_ref;
    private int totalDonate,temp=1;
    private SearchTopDonorAdapter restAdapter;
    private List<DonorData> donorItem;
    private MyProgressDialog pd;
    Spinner bloodgroup, division;
    Button btnsearch;
    FirebaseAuth mAuth;
    FirebaseUser fuser;
    FirebaseDatabase fdb;

    String[] states = new String[]{
            "Andhra Pradesh", "Arunachal Pradesh","Assam","Bihar","Chhattisgarh","Goa","Gujarat","Haryana",
            "Himachal Pradesh","Mizoram","Jharkhand","Karnataka","Kerala","Madhya Pradesh","Maharashtra","Manipur","Meghalaya","Nagaland","Odisha","Punjab","Rajasthan","Sikkim","Tamil Nadu"
            ,"Tripura","Telangana","Uttar Pradesh","Uttarakhand","West Bengal"
    };

    public TopDonors() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle("Top Donors");
        pd = new MyProgressDialog(getActivity());
        pd.setMessage("Loading. Please wait...");
        pd.show();
        view = inflater.inflate(R.layout.topdonors, container, false);
        recentPosts = (RecyclerView) view.findViewById(R.id.toprecycle);
        btnsearch = view.findViewById(R.id.btnSearch);
        bloodgroup = view.findViewById(R.id.btngetBloodGroup);
        division = view.findViewById(R.id.btngetDivison);


        recentPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        donor_ref = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        fuser = mAuth.getCurrentUser();
        fdb = FirebaseDatabase.getInstance();
        db_ref = fdb.getReference("donors");

        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donorItem = new ArrayList<>();
                donorItem.clear();
                temp=1;
                restAdapter = new SearchTopDonorAdapter(donorItem);
                recentPosts = (RecyclerView) view.findViewById(R.id.toprecycle);
                recentPosts.setLayoutManager(new LinearLayoutManager(getContext()));
                RecyclerView.LayoutManager pmLayout = new LinearLayoutManager(getContext());
                recentPosts.setLayoutManager(pmLayout);
                recentPosts.setItemAnimator(new DefaultItemAnimator());
                recentPosts.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
                recentPosts.setAdapter(restAdapter);
                Query qpath = db_ref.child(division.getSelectedItem().toString())
                        .child(bloodgroup.getSelectedItem().toString());
                qpath.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot singleitem : dataSnapshot.getChildren()) {
                                int recyclersize = donorItem.size();
                                if (recyclersize <= 9) {
                                    DonorData donorData = singleitem.getValue(DonorData.class);
                                    totalDonate = donorData.getTotalDonate();
                                    System.out.println(totalDonate);
                                    if (donorData.getTotalDonate() >= temp) {
                                        temp = totalDonate;
                                        totalDonate = donorData.getTotalDonate();
                                        donorItem.add(donorData);
                                        restAdapter.notifyDataSetChanged();
                                    }

                                }else {
                                    Toast.makeText(getActivity(),"TOP 10 Blood Donators",Toast.LENGTH_SHORT).show();
                                }
                            }
                            Toast.makeText(getActivity(),"State:"+division.getSelectedItem().toString()+",Bloodgroup("+bloodgroup.getSelectedItem().toString()+")",Toast.LENGTH_LONG).show();


                        } else {

                            Toast.makeText(getActivity(), "TOP Blood Donators of INDIA",
                                    Toast.LENGTH_LONG).show();
                            if (division.getSelectedItem().toString().equals("Mark ALL")){
                                AddPosts();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("User", databaseError.getMessage());

                    }
                });
                pd.dismiss();
            }

        });
        AddPosts();
        return view;
    }
    private void AddPosts() {
        donorItem = new ArrayList<>();
        donorItem.clear();
        restAdapter = new SearchTopDonorAdapter(donorItem);
        recentPosts = (RecyclerView) view.findViewById(R.id.toprecycle);
        recentPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerView.LayoutManager pmLayout = new LinearLayoutManager(getContext());
        recentPosts.setLayoutManager(pmLayout);
        recentPosts.setItemAnimator(new DefaultItemAnimator());
        recentPosts.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recentPosts.setAdapter(restAdapter);
        for (int i = 0; i < states.length; i++) {
            Query qpath = db_ref.child(states[i])
                    .child(bloodgroup.getSelectedItem().toString());
            qpath.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot singleitem : dataSnapshot.getChildren()) {
                            int recyclersize = donorItem.size();
                            if (recyclersize <= 49) {
                                DonorData donorData = singleitem.getValue(DonorData.class);
                                totalDonate = donorData.getTotalDonate();
                                if (donorData.getTotalDonate() >= temp) {
                                    temp = totalDonate;
                                    totalDonate = donorData.getTotalDonate();
                                    donorItem.add(donorData);
                                    restAdapter.notifyDataSetChanged();
                                }
                            }else {
                                Toast.makeText(getActivity(),"TOP 50 Blood Donators",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("User", databaseError.getMessage());

                }
            });
            pd.dismiss();
        }
        Toast.makeText(getActivity(),"TOP Blood Donators of INDIA",Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}