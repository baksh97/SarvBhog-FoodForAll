package com.example.sarvbhog.Register;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sarvbhog.R;
import com.example.sarvbhog.SelectSHType;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
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

public class Login extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    EditText phoneEt;

    private static final int RESOLVE_HINT = 1000;

    private static final String TAG = "PhoneLogin";
    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    Button sendCodeBtn;
    //    private Scene scene1, scene2;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private GoogleApiClient mGoogleApiClient;
//    private LayoutInflater layoutInflater;
//    private ViewGroup root;

    Context thisContext = Login.this;

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");


                            FirebaseUser user = task.getResult().getUser();
                            CodeVerify.updateUI(user);
                            startActivity(new Intent(thisContext, SelectSHType.class));
                            finish();
//                            startActivity(new Intent(PhoneLogin.this, HomeScreen.class));
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            showToast(thisContext, "Login unsuccessful!");
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    void initViews(){
        mAuth = FirebaseAuth.getInstance();

//        root = (ViewGroup) findViewById(R.id.rootContainerPhoneAuth);

//        layoutInflater = LayoutInflater.from(this);
//        View phoneView = layoutInflater.inflate(R.layout.phone_enter,null);
//        View codeView = layoutInflater.inflate(R.layout.code_enter,null);


        phoneEt = (EditText) findViewById(R.id.phone_et_login);
        sendCodeBtn = (Button) findViewById(R.id.sendcode_btn_login);


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
                startActivity(intent);
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

//        startActivity(new);

//        Transition slide = new Slide(Gravity.RIGHT);
//        TransitionManager.go(scene2,slide);
    }


    @Override public void onConnected(@Nullable Bundle bundle) {
    }
    @Override public void onConnectionSuspended(int i) {
    }
    @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        layoutInflater.in



        initViews();


        requestHint();

        sendCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = phoneEt.getText().toString();
                DatabaseReference myref = database.getReference("users");
                myref.orderByChild("phone").equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null){
                            //it means user already registered
                            //Add code to show your prompt
                            sendCode();
                        }else{
                            //It is new users
                            //write an entry to your user table
                            showToast(thisContext,"You have not registered before! Please register first!");
                            //writeUserEntryToDB();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });



    }}
