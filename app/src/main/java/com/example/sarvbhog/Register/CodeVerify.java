package com.example.sarvbhog.Register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sarvbhog.NewSubmits.PreparePacket;
import com.example.sarvbhog.R;
import com.example.sarvbhog.SelectSHType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.sarvbhog.CommonFunctions.showToast;

public class CodeVerify extends AppCompatActivity implements View.OnKeyListener {

    private EditText et1,et2,et3,et4,et5,et6, codeEt;
    private Button verifyCodeBtn;
    private FirebaseAuth mAuth;
    private EditText[] editTexts;
    private TextView phone_tv;
    private String name="", phone="",entity="";

    private static final String TAG = "CodeVerify";

    Context thisContext = CodeVerify.this;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            updateUI(user);

                            DatabaseReference myref = database.getReference("users");
                            if(!name.equals("")){
                                myref.child(user.getUid()).child("entity").setValue(entity);
                                myref.child(user.getUid()).child("name").setValue(name);
                                myref.child(user.getUid()).child("phone").setValue(phone);
                                myref.child(user.getUid()).child("requests_served").setValue(0);
                                myref.child(user.getUid()).child("producers_connected").setValue(0);
                                database.getReference(entity).child(user.getUid());
                            }
//                            myref.child(user.getUid()).child("name").setValue(name);
//                            myref.child(user.getUid()).child("phone").setValue(phone);

                            showToast(thisContext,"Registration Successful!");
                            startActivity(new Intent(CodeVerify.this, SelectSHType.class));
                            finish();
//                            startActivity(new Intent(PhoneLogin.this, HomeScreen.class));
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(CodeVerify.this, "Login unsuccessful!", Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    String mVerificationId;

    void verifyCode(){
        String code = getCode();
        Log.d(TAG, "verifyCode: mverifyid"+mVerificationId);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }


    void initViews(){
        verifyCodeBtn = (Button) findViewById(R.id.verifycode_btn_codeenter);
        mAuth = FirebaseAuth.getInstance();

        phone_tv = (TextView) findViewById(R.id.mobile_number_tv_codeverify);
        editTexts = new EditText[6];
        et1 = (EditText) findViewById(R.id.code1_et_codeverify);
        et2 = (EditText) findViewById(R.id.code2_et_codeverify);
        et3 = (EditText) findViewById(R.id.code3_et_codeverify);
        et4 = (EditText) findViewById(R.id.code4_et_codeverify);
        et5 = (EditText) findViewById(R.id.code5_et_codeverify);
        et6 = (EditText) findViewById(R.id.code6_et_codeverify);
        editTexts[0]=et1;
        editTexts[1]=et2;
        editTexts[2]=et3;
        editTexts[3]=et4;
        editTexts[4]=et5;
        editTexts[5]=et6;

//        et1.addTextChangedListener(new GenericTextWatcher(et1));
//        et2.addTextChangedListener(new GenericTextWatcher(et2));
//        et3.addTextChangedListener(new GenericTextWatcher(et3));
//        et4.addTextChangedListener(new GenericTextWatcher(et4));
//        et5.addTextChangedListener(new GenericTextWatcher(et5));
//        et6.addTextChangedListener(new GenericTextWatcher(et6));

//        et1.setOnKeyListener(this);
//        et2.setOnKeyListener(this);
//        et3.setOnKeyListener(this);
//        et4.setOnKeyListener(this);
//        et5.setOnKeyListener(this);
//        et6.setOnKeyListener(this);

        for(int i=0;i<6;i++){
            editTexts[i].addTextChangedListener(new PinTextWatcher(i));
            editTexts[i].setOnKeyListener(new PinOnKeyListener(i));
        }
//        editText1.addTextChangedListener(new PinTextWatcher(0));
//        editText2.addTextChangedListener(new PinTextWatcher(1));
//        editText3.addTextChangedListener(new PinTextWatcher(2));
//        editText4.addTextChangedListener(new PinTextWatcher(3));
//
//        editText1.setOnKeyListener(new PinOnKeyListener(0));
//        editText2.setOnKeyListener(new PinOnKeyListener(1));
//        editText3.setOnKeyListener(new PinOnKeyListener(2));
//        editText4.setOnKeyListener(new PinOnKeyListener(3));

    }

    static public void updateUI(FirebaseUser user){

    }

    String getCode(){
        String code="";
        code += et1.getText().toString()+et2.getText().toString()+et3.getText().toString()+et4.getText().toString()+et5.getText().toString()+et6.getText().toString();
        return code;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verify);

        initViews();

        Intent intent = getIntent();
        mVerificationId = intent.getStringExtra("mVerificationId");
        if(intent.hasExtra("name")) {
            name = intent.getStringExtra("name");
            phone = intent.getStringExtra("phone");
            entity=intent.getStringExtra("entity");
        }

        phone_tv.setText(phone);

        verifyCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getCode().equals("")){
                    Toast.makeText(CodeVerify.this, "Please enter the 6 digit code!", Toast.LENGTH_SHORT).show();
                }else
                    verifyCode();
            }
        });
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        EditText et = (EditText) view;
        int length = et.getText().toString().length();
        boolean backFocus=false, nextFocus=false, runKey=false;
        if(length==1 && i==67)backFocus=true;
        else if(length==0 && i==67){
            backFocus=true;
            runKey=true;
        }
        else if(length==1 && i>=7 && i<=16){
            nextFocus=true;
            runKey=true;
        }
        else if(length==0 && i>=7 && i<=16)nextFocus=true;

        switch (view.getId()){
            case R.id.code1_et_codeverify:
                if(nextFocus){
                    et2.requestFocus();
//                    if(runKey)et2.dispatchKeyEvent(keyEvent);
                }
                break;
            case R.id.code2_et_codeverify:
                if(nextFocus){
                    et3.requestFocus();
//                    if(runKey)et3.dispatchKeyEvent(keyEvent);
                }

                else if(backFocus){
                    et1.requestFocus();
                    if(runKey)et1.setText("");
                }
                break;
            case R.id.code3_et_codeverify:
                if(nextFocus){
                    et4.requestFocus();
//                    if(runKey)et4.dispatchKeyEvent(keyEvent);
                }

                else if(backFocus){
                    et2.requestFocus();
                    if(runKey)et2.setText("");
                }
                break;
            case R.id.code4_et_codeverify:
                if(nextFocus){
                    et5.requestFocus();
//                    if(runKey)et5.dispatchKeyEvent(keyEvent);
                }

                else if(backFocus){
                    et3.requestFocus();
                    if(runKey)et3.setText("");
                }
                break;
            case R.id.code5_et_codeverify:
                if(nextFocus){
                    et6.requestFocus();
//                    if(runKey)et6.dispatchKeyEvent(keyEvent);
                }

                else if(backFocus){
                    et4.requestFocus();
                    if(runKey)et4.setText("");
                }
                break;
            case R.id.code6_et_codeverify:
//                if(nextFocus)et3.requestFocus();
//                if(runKey)et3.dispatchKeyEvent(keyEvent);
                if(backFocus){
                    et5.requestFocus();
                    if(runKey)et5.setText("");
                }
                break;
        }
        return false;
    }

    class PinTextWatcher implements TextWatcher
    {
        private View view;

        private int currentIndex;
        private boolean isFirst = false, isLast = false;
        private String newTypedString = "";

        public PinTextWatcher(int currentIndex)
        {
//            this.view = view;
            this.currentIndex = currentIndex;

            if (currentIndex == 0)
                this.isFirst = true;
            else if (currentIndex == editTexts.length-1)
                this.isLast = true;
        }



        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method stub
//            String text = editable.toString();
//            switch(view.getId())
//            {
//
//                case R.id.code1_et_codeverify:
//                    if(text.length()==1)
//                        et2.requestFocus();
//                    break;
//                case R.id.code2_et_codeverify:
//                    if(text.length()==1)
//                        et3.requestFocus();
//                    else if(text.length()==0)
//                        et1.requestFocus();
//                    break;
//                case R.id.code3_et_codeverify:
//                    if(text.length()==1)
//                        et4.requestFocus();
//                    else if(text.length()==0)
//                        et2.requestFocus();
//                    break;
//                case R.id.code4_et_codeverify:
//                    if(text.length()==1)
//                        et5.requestFocus();
//                    else if(text.length()==0)
//                        et3.requestFocus();
//                    break;
//                case R.id.code5_et_codeverify:
//                    if(text.length()==1)
//                        et6.requestFocus();
//                    else if(text.length()==0)
//                        et4.requestFocus();
//                    break;
//                case R.id.code6_et_codeverify:
//                    if(text.length()==0)
//                        et5.requestFocus();
//                    break;
//            }
            String text = newTypedString;
//            Toast.makeText(CodeVerify.this, "charsequence: "+editable.toString(), Toast.LENGTH_LONG).show();
            /* Detect paste event and set first char */
            if (text.length() > 1)
                text = String.valueOf(text.charAt(0)); // TODO: We can fill out other EditTexts

            editTexts[currentIndex].removeTextChangedListener(this);
            editTexts[currentIndex].setText(text);
            editTexts[currentIndex].setSelection(text.length());
            editTexts[currentIndex].addTextChangedListener(this);

            if (text.length() == 1)
                moveToNext();
//            else if (text.length() == 0)
//                moveToPrevious();
        }

        private void moveToNext() {
            if (!isLast)
                editTexts[currentIndex + 1].requestFocus();

            if (isAllEditTextsFilled() && isLast) { // isLast is optional
                hideKeyboard();
                editTexts[currentIndex].clearFocus();
            }
        }

        private void moveToPrevious() {
            if (!isFirst)
                editTexts[currentIndex - 1].requestFocus();
        }

        private boolean isAllEditTextsFilled() {
            for (EditText editText : editTexts)
                if (editText.getText().toString().trim().length() == 0)
                    return false;
            return true;
        }

        private void hideKeyboard() {
//            if (getCurrentFocus() != null) {
//                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//            }
//            Activity activity = getCallingActivity();
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
//            if (view == null) {
//                view = new View(activity);
//            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub


        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            newTypedString = s.subSequence(start, start + count).toString().trim();

        }
    }

    public class PinOnKeyListener implements View.OnKeyListener {

        private int currentIndex;

        PinOnKeyListener(int currentIndex) {
            this.currentIndex = currentIndex;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (editTexts[currentIndex].getText().toString().isEmpty() && currentIndex != 0) {
//                    editTexts[currentIndex-1].setText("");
                    editTexts[currentIndex-1].dispatchKeyEvent(event);
                    editTexts[currentIndex - 1].requestFocus();
                }
            }
            else if(editTexts[currentIndex].getText().toString().length()==1 && currentIndex != editTexts.length-1){
                editTexts[currentIndex+1].dispatchKeyEvent(event);
                editTexts[currentIndex+1].requestFocus();
            }
            return false;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
