package com.android.blooddonor.bloodbank.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.android.blooddonor.bloodbank.R;
import com.android.blooddonor.bloodbank.activities.Dashboard;
import com.android.blooddonor.bloodbank.fragments.HomeView;
import com.android.blooddonor.bloodbank.viewmodels.CustomUserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.concurrent.LinkedTransferQueue;


public class BloodRequestAdapter extends RecyclerView.Adapter<BloodRequestAdapter.PostHolder> {

    private List<CustomUserData> postLists;
    private DatabaseReference donor_ref;
    FirebaseAuth mAuth;


    String phone;
    View listitem;


    public class PostHolder extends RecyclerView.ViewHolder
    {
        TextView Name, bloodgroup, Address,contact, posted;

        public PostHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.reqstUser);
            contact = itemView.findViewById(R.id.targetCN);
            bloodgroup = itemView.findViewById(R.id.targetBG);
            Address = itemView.findViewById(R.id.reqstLocation);
            posted = itemView.findViewById(R.id.posted);

        }
    }

    public BloodRequestAdapter(List<CustomUserData> postLists)
    {
        this.postLists = postLists;
    }



    @Override
    public PostHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {

        listitem  = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.request_list_item, viewGroup, false);



        return new PostHolder(listitem);
    }




    @Override
    public void onBindViewHolder(final PostHolder postHolder, int i) {
        mAuth = FirebaseAuth.getInstance();
        donor_ref = FirebaseDatabase.getInstance().getReference();

        if(i%2==0)
        {
            postHolder.itemView.setBackgroundColor(Color.parseColor("#C13F31"));
        }
        else
        {
            postHolder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        final CustomUserData customUserData = postLists.get(i);
        postHolder.Name.setText("Posted by: "+customUserData.getName());
        postHolder.Address.setText("From: "+customUserData.getAddress()+", "+customUserData.getDivision()+", "+customUserData.getLocality());
        postHolder.bloodgroup.setText("Needs "+customUserData.getBloodGroup());
        postHolder.posted.setText("Posted on:"+customUserData.getTime()+", "+customUserData.getDate());
        postHolder.contact.setText(customUserData.getContact());
        phone = customUserData.getContact();

        listitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone =  customUserData.getContact();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                view.getContext().startActivity(intent);
            }
        });

        final Query delpost = donor_ref.child("posts").child( mAuth.getCurrentUser().getUid());
        listitem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                delpost.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                         if (snapshot.exists()){
                             new AlertDialog.Builder(view.getContext())
                                     .setTitle("Delete request")
                                     .setIcon(R.drawable.requestlogo)
                                     .setMessage("Remove Blood Request?"+"\n"+"Name:"+snapshot.child("Name").getValue())
                                     .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                         @Override
                                         public void onClick(DialogInterface dialog, int which) {
                                             donor_ref.child("posts").child( mAuth.getCurrentUser().getUid()).removeValue();
                                             Intent restart = new Intent(view.getContext(), Dashboard.class);
                                             view.getContext().startActivity(restart);
                                         }
                                     }).setNegativeButton("No", null).show();

                         }else System.out.println("False");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                return false;
            }
        });

    }
    @Override
    public int getItemCount() {
        return postLists.size();
    }
}
