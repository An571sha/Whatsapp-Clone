package com.example.whatsappclone.activities;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.atomic.AtomicBoolean;

import se.aaro.gustav.passwordstrengthmeter.PasswordStrengthMeter;

public class SignUpActivity extends AppCompatActivity {
    private EditText phoneNumberTextField;
    private Button signUpButton;
    private Button signInButton;
    private EditText emailTextField;
    private EditText passswordTextField;
    private PasswordStrengthMeter meter;
    private FirebaseAuth mAuth;
    private String email;
    private String password;
    private String phoneNumber;
    private FirebaseUser user;
    DatabaseReference userDatabase;


    @SuppressLint("ClickableViewAccessibility")
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

        int infoIcon = R.drawable.ic_info_black_20dp;
        passswordTextField.setCompoundDrawablesWithIntrinsicBounds(0,0,infoIcon,0);

        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        meter.setEditText(passswordTextField);


        passswordTextField.setOnTouchListener(new View.OnTouchListener() {
            AtomicBoolean isPasswordDisplayedAsDots = new AtomicBoolean();

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (passswordTextField.getRight() - passswordTextField.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        if(isPasswordDisplayedAsDots.get()){

                            Log.i("booleanTestShouldBeTrue", String.valueOf(isPasswordDisplayedAsDots.get()));
                            passswordTextField.setTransformationMethod(null);
                            isPasswordDisplayedAsDots.set(false);

                        } else {

                            Log.i("booleanTestShouldBeFalse", String.valueOf(isPasswordDisplayedAsDots.get()));
                            passswordTextField.setTransformationMethod(new PasswordTransformationMethod());
                            isPasswordDisplayedAsDots.set(true);
                        }

                        Toast.makeText(SignUpActivity.this,"Password Visibility toggled", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void signup(View view){
        email = emailTextField.getText().toString();
        password = passswordTextField.getText().toString();
        phoneNumber = phoneNumberTextField.getText().toString();

        if(isValidEmail(email) && !password.isEmpty()){

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("LOGIN", "createUserWithEmail:success");
                                writeNewUserInDatabase(email,mAuth.getUid(),phoneNumber);
                                sendVerificationEmail(mAuth, SignUpActivity.this);

                            } else {

                                Log.i("LOGIN", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
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

    public static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


}



