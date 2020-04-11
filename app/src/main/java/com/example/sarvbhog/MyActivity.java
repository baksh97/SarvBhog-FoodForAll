package com.example.sarvbhog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.sarvbhog.NewSubmits.PreparePacket;
import com.example.sarvbhog.NewSubmits.RequestPacket2;
import com.example.sarvbhog.Register.Login;
import com.example.sarvbhog.Register.Register;
import com.example.sarvbhog.Status.PrepareStatus;
import com.example.sarvbhog.Status.RequestStatus;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.sarvbhog.CommonFunctions.showToast;

public class MyActivity extends AppCompatActivity {

    Button register_btn, login_btn, sh1_btn,sh2_btn,sh3_btn;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    Context thisContext = MyActivity.this;

    public static String name="",phone="";

    void initViews(){
        register_btn = (Button) findViewById(R.id.register_btn_my);
        login_btn = (Button) findViewById(R.id.login_btn_my);
        sh1_btn = (Button) findViewById(R.id.sh1_btn_my);
        sh2_btn = (Button) findViewById(R.id.sh2_btn_my);
        sh3_btn = (Button) findViewById(R.id.sh3_btn_my);

        sh1_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(thisContext,SelectLocation.class );
                intent.putExtra("Class", RequestPacket2.class);
                startActivity(intent);
            }
        });

        sh2_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth.getCurrentUser()==null){
                    showToast(thisContext,"Please login first!", Color.DKGRAY);
                }else {
                    Intent intent = new Intent(thisContext, SelectLocation.class);
                    intent.putExtra("Class", PreparePacket.class);
                    startActivity(intent);
                }
            }
        });

        sh3_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth.getCurrentUser()==null){
                    showToast(thisContext,"Please login first!", Color.DKGRAY);
                }else {
                    Intent intent = new Intent(thisContext, SelectLocation.class);
                    intent.putExtra("Class", SelectSHType.class);
                    startActivity(intent);
                }
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(thisContext, Register.class));
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(thisContext, Login.class));
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        initViews();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (mAuth.getCurrentUser() != null || id==R.id.request_status) {
            switch (id) {
                case R.id.profile:
                    startActivity(new Intent(thisContext, Profile.class));
                    return true;
                case R.id.request_status:
                    startActivity(new Intent(thisContext, RequestStatus.class));
                    return true;
                case R.id.prepare_status:
                    startActivity(new Intent(thisContext, PrepareStatus.class));
                    return true;
                case R.id.sign_out:
                    mAuth.signOut();
                    name="";
                    phone="";
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        else{
            showToast(thisContext, "You need to login to access this!");
            return true;
        }
    }}
