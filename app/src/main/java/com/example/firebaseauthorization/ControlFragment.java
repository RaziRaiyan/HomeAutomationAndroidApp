package com.example.firebaseauthorization;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ControlFragment extends Fragment {
    private static final String TAG = "ControlFragment";

    private TextView tv_ip_address;
    private Button mSwitch1,mSwitch2,mSwitch3;
    private static boolean sw1_flag = true;
    private static boolean sw2_flag = true;
    private static boolean sw3_flag = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_controls,container,false);
        mSwitch1 = view.findViewById(R.id.btn_sw1);
        mSwitch2 = view.findViewById(R.id.btn_sw2);
        mSwitch3 = view.findViewById(R.id.btn_sw3);
        tv_ip_address = view.findViewById(R.id.tv_ip_address);

        mSwitch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sw1_flag){
                    changeLEDState(1,1);
                    mSwitch1.setText(R.string.on);
                    sw1_flag = true;
                }else {
                    changeLEDState(1,0);
                    mSwitch1.setText(R.string.off);
                    sw1_flag = false;
                }
            }
        });

        mSwitch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sw2_flag){
                    changeLEDState(2,1);
                    mSwitch2.setText(R.string.on);
                    sw2_flag = true;
                }else {
                    changeLEDState(2,0);
                    mSwitch2.setText(R.string.off);
                    sw2_flag = false;
                }
            }
        });

        mSwitch3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sw3_flag){
                    changeLEDState(3,1);
                    mSwitch3.setText(R.string.on);
                    sw3_flag = true;
                }else {
                    changeLEDState(3,0);
                    mSwitch3.setText(R.string.off);
                    sw3_flag = false;
                }
            }
        });
        setUpIPAddressListener();
        setUpTemperatureListener();
        return view;
    }

    private void setUpTemperatureListener() {
        DatabaseReference dbTempRef = FirebaseDatabase.getInstance().getReference("Temperature");
        dbTempRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAdded: "+dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpIPAddressListener() {
        DatabaseReference dbIPRef = FirebaseDatabase.getInstance().getReference("IPAddress");
        dbIPRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String ipAddress = (String) dataSnapshot.getValue();
                if(null != ipAddress){
                    tv_ip_address.setText(ipAddress);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void changeLEDState(final int button, int state){
        DatabaseReference dbSwitchStatus = FirebaseDatabase.getInstance().getReference("LEDStatus"+button);
        dbSwitchStatus.setValue(state).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(),"Server data changed at "+button,Toast.LENGTH_SHORT).show();
                }else {
                    Log.d(TAG, "onComplete: changeLEDStatus"+task.getException().getMessage());
                }
            }
        });
    }
}
