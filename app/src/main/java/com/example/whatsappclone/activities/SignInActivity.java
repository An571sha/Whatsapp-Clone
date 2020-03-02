package com.example.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.atomic.AtomicBoolean;


public class SignInActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private String email;
    private String password;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    EditText passwordField;
    EditText emailField;
    Button signInbutton;
    Intent intent;
    DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        passwordField = (EditText) findViewById(R.id.password);
        emailField = (EditText) findViewById(R.id.email);
        signInbutton = findViewById(R.id.signin);

        int infoIcon = R.drawable.ic_info_black_20dp;
        passwordField.setCompoundDrawablesWithIntrinsicBounds(0,0,infoIcon,0);
        emailField.setCompoundDrawablesWithIntrinsicBounds(0,0,infoIcon,0);

        satTouchListenerForIcon();
        setupSharedPreferences();

        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

    }



    public void signin(View view) {
        email = emailField.getText().toString();
        password = passwordField.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {

                    Toast.makeText(SignInActivity.this, "sign in error", Toast.LENGTH_SHORT).show();
                    Log.e("ERROR", task.getException().toString());

                } else {

                    user = mAuth.getCurrentUser();

                    if (user!= null && user.isEmailVerified()) {

                        Log.i("result", task.getResult().toString());
                        intent = new Intent(SignInActivity.this, MainActivity.class);
                        intent.putExtra("userNameId", mAuth.getUid());
                        intent.putExtra("userEmail", user.getEmail());
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                        builder.setMessage("please verify your email.");
                        builder.setCancelable(true);
                        builder.setNegativeButton(
                                "Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder.create();
                        alert11.show();

                    }

                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(SignInActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

   @SuppressLint("ClickableViewAccessibility")
    public void satTouchListenerForIcon(){

        emailField.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
            final int DRAWABLE_RIGHT = 2;
            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (emailField.getRight() - emailField.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    Toast.makeText(SignInActivity.this,"Sign in with Google coming soon", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            return false;
        }
        });

        passwordField.setOnTouchListener(new View.OnTouchListener() {
            AtomicBoolean isPasswordDisplayedAsDots = new AtomicBoolean();

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (passwordField.getRight() - passwordField.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        if(isPasswordDisplayedAsDots.get()){

                            Log.i("booleanTestShouldBeTrue", String.valueOf(isPasswordDisplayedAsDots.get()));
                            passwordField.setTransformationMethod(null);
                            isPasswordDisplayedAsDots.set(false);

                        } else {

                            Log.i("booleanTestShouldBeFalse", String.valueOf(isPasswordDisplayedAsDots.get()));
                            passwordField.setTransformationMethod(new PasswordTransformationMethod());
                            isPasswordDisplayedAsDots.set(true);
                        }

                        Toast.makeText(SignInActivity.this,"Password Visibility toggled", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void signup(View view) {
        intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals("theme_display")) {
            setBackgroundColorWithPref(sharedPreferences.getBoolean("theme_display",true));
        }
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    // Method to set Visibility of Text.
    public void setBackgroundColorWithPref(boolean theme) {
        if (theme == true) {
            getWindow().getDecorView().setBackgroundColor(Color.DKGRAY);
            emailField.setVisibility(View.VISIBLE);
        } else {
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
            emailField.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }


}
