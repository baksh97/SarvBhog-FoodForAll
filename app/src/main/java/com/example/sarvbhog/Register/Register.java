package com.example.sarvbhog.Register;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
//import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sarvbhog.MyActivity;
import com.example.sarvbhog.R;
import com.example.sarvbhog.SelectSHType;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.credentials.Credential;

import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import static com.example.sarvbhog.CommonFunctions.showToast;

public class Register extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    EditText phoneEt,nameET;
    ProgressBar pb;
    TextView login_tv;
    String entity;

//    Class c;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    Context thisContext = Register.this;


    private static final int RESOLVE_HINT = 1000;

    private static final String TAG = "PhoneLogin";
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private String name, phone;
    public static PhoneAuthProvider.ForceResendingToken mResendToken;

    RadioGroup rg;
    Button sendCodeBtn;
    //    private Scene scene1, scene2;
    public static PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private GoogleApiClient mGoogleApiClient;
//    private LayoutInflater layoutInflater;
//    private ViewGroup root;

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pb.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");



                            FirebaseUser user = task.getResult().getUser();
//                            CodeVerify.updateUI(user);
                            DatabaseReference myref = database.getReference("users");
                            myref.child(user.getUid()).child("name").setValue(name);
                            myref.child(user.getUid()).child("phone").setValue(phone);
                            myref.child(user.getUid()).child("requests_served").setValue(0);
                            myref.child(user.getUid()).child("producers_connected").setValue(0);
                            myref.child(user.getUid()).child("entity").setValue(entity);

                            database.getReference(entity).push().setValue(user.getUid());
                            showToast(thisContext,"Registration Successful!");
//                            pb.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(thisContext, MyActivity.class));
                            finish();
//                            startActivity(new Intent(PhoneLogin.this, HomeScreen.class));
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            showToast(thisContext,"Registration unsuccessful! Error: "+task.getException().getMessage());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }


    void initViews(){
        mAuth = FirebaseAuth.getInstance();

        rg = (RadioGroup) findViewById(R.id.rg_register);

//        root = (ViewGroup) findViewById(R.id.rootContainerPhoneAuth);

//        layoutInflater = LayoutInflater.from(this);
//        View phoneView = layoutInflater.inflate(R.layout.phone_enter,null);
//        View codeView = layoutInflater.inflate(R.layout.code_enter,null);


        pb = (ProgressBar) findViewById(R.id.pb_register);
        pb.setVisibility(View.INVISIBLE);
        login_tv = (TextView) findViewById(R.id.login_tv_register);
        nameET = ((TextInputLayout) findViewById(R.id.name_til_register)).getEditText();
        phoneEt = ((TextInputLayout) findViewById(R.id.phone_til_phoneenter)).getEditText();
        sendCodeBtn = (Button) findViewById(R.id.sendcode_btn_register);


        login_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(thisContext, Login.class));
            }
        });

//        scene1 = Scene.getSceneForLayout(root, R.layout.phone_enter, this);
//        scene2 = Scene.getSceneForLayout(root, R.layout.code_enter, this);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                Intent intent = new Intent(thisContext, CodeVerify.class);
                intent.putExtra("mVerificationId", mVerificationId);
                intent.putExtra("name",name);
                intent.putExtra("phone",phone);
                intent.putExtra("entity",entity);
                intent.putExtra("c_act", "register");
                pb.setVisibility(View.INVISIBLE);
                startActivity(intent);
//                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


                // ...
            }
        };
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).enableAutoManage(this, this).addApi(Auth.CREDENTIALS_API).build();

//        scene1.enter();
    }


    private void requestHint() {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();



        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                mGoogleApiClient, hintRequest);

        try {
            startIntentSenderForResult(intent.getIntentSender(),
                    RESOLVE_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                // credential.getId();  <-- will need to process phone number string
                phoneEt.setText(credential.getId());
            }
        }
    }





    void sendCode(){
        String phoneNumber = phoneEt.getText().toString();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks
        );

//        startActivity(new Intent());

//        Transition slide = new Slide(Gravity.RIGHT);
//        TransitionManager.go(scene2,slide);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();


        requestHint();

        sendCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rg.getCheckedRadioButtonId()==-1 || phoneEt.getText().toString().equals("") || nameET.getText().toString().equals("")){
                    showToast(thisContext,"Please complete all the information!");
                }else{
                    pb.setVisibility(View.VISIBLE);
                    name = nameET.getText().toString();
                    phone = phoneEt.getText().toString();
                    int selectedId=rg.getCheckedRadioButtonId();
                    entity = ((RadioButton) findViewById(selectedId)).getText().toString();
                    DatabaseReference myref = database.getReference("users");
                    myref.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null){
                                //it means user already registered
                                //Add code to show your prompt

                                pb.setVisibility(View.INVISIBLE);
                                showToast(thisContext,"User already exists! Please log in!");
                            }else{
                                //It is new users
                                //write an entry to your user table
                                sendCode();
                                //writeUserEntryToDB();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            pb.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });

    }

    @Override public void onConnected(@Nullable Bundle bundle) {
    }
    @Override public void onConnectionSuspended(int i) {
    }
    @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }}
