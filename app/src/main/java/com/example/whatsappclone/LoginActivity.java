package com.example.whatsappclone;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

import se.aaro.gustav.passwordstrengthmeter.PasswordStrengthMeter;

public class LoginActivity extends AppCompatActivity {
    private EditText phoneNumberTextField;
    private Button signUpButton;
    private Button signInButton;
    private EditText emailTextField;
    private EditText passswordTextField;
    private PasswordStrengthMeter meter;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private String email;
    private String password;
    private String phoneNumber;
    private FirebaseUser user;
    Intent intent;
    DatabaseReference userDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneNumberTextField = findViewById(R.id.phoneNumber);
        signUpButton = findViewById(R.id.button);
        signInButton = findViewById(R.id.signin);
        emailTextField = findViewById(R.id.email);
        passswordTextField = findViewById(R.id.password);
        meter = findViewById(R.id.passwordInputMeter);

        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        meter.setEditText(passswordTextField);
    }

    private void startPhoneVerification(String no) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+49" + no,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null && mVerificationId != null) {
                intent = new Intent(LoginActivity.this,VerifyMobile.class);
                intent.putExtra("code",code);
                intent.putExtra("VerificationId",mVerificationId);
                verifyVerificationCode(code);
                startActivity(intent);

            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(verificationId, forceResendingToken);
            mVerificationId = verificationId;
        }
    };

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
    }

    public void signin(View view){
        email = emailTextField.getText().toString();
        password = passswordTextField.getText().toString();
        phoneNumber = phoneNumberTextField.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {

                    Toast.makeText(LoginActivity.this, "sign in error", Toast.LENGTH_SHORT).show();

                } else {
                    user = mAuth.getCurrentUser();
                    Log.i("result", task.getResult().toString());
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("userNameId", mAuth.getUid());
                    intent.putExtra("userEmail",user.getEmail());
                    startActivity(intent);

                }
            }
        });
    }

    public void signup(View view){
        email = emailTextField.getText().toString();
        password = passswordTextField.getText().toString();
        phoneNumber = phoneNumberTextField.getText().toString();

        if(!phoneNumber.isEmpty() && isValidEmail(email) && !password.isEmpty()){

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("LOGIN", "createUserWithEmail:success");
                                writeNewUserInDatabase(email,mAuth.getUid(),phoneNumber);
                                sendVerificationEmail(mAuth, LoginActivity.this);

                            } else {

                                Log.i("LOGIN", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
        } else {

            Toast.makeText(this,"Phone/email not valid", Toast.LENGTH_SHORT).show();
        }



    }

    public void writeNewUserInDatabase(String email, String userId, String number){
        User user = new User(email,userId,number);
        userDatabase.child("users").child(userId).setValue(user);

    }

    public void sendVerificationEmail(FirebaseAuth mAuth, final Context context){
        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context,
                            "Verification email sent to " + user.getEmail(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("Whatsapp-Error", "sendEmailVerification", task.getException());
                    Toast.makeText(context,
                            "Failed to send verification email.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static String encodeString(String string) {
        return string.replace(".", ",");
    }


}



