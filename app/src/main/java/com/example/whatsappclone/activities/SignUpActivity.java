package com.example.whatsappclone.activities;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.User;
import com.example.whatsappclone.utility.Utility;
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
    private final int IMAGE_PICKER_RQUEST_CODE = 100;

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
    private ImageView addProfileImage;
    private ImageView profileImage;
    private Uri imageUri;

    DatabaseReference userDatabase;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        phoneNumberTextField = findViewById(R.id.phoneNumber);
        signUpButton = findViewById(R.id.button);
        signInButton = findViewById(R.id.signin);
        emailTextField = findViewById(R.id.email);
        passswordTextField = findViewById(R.id.password);
        meter = findViewById(R.id.passwordInputMeter);
        profileImage = findViewById(R.id.profilePic);
        addProfileImage = findViewById(R.id.iv_camera);

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

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (passswordTextField.getRight() - passswordTextField.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        if (isPasswordDisplayedAsDots.get()) {

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

        addProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imagePickerIntent = new Intent(Intent.ACTION_PICK);
                imagePickerIntent.setType("image/*");
                startActivityForResult(imagePickerIntent, IMAGE_PICKER_RQUEST_CODE);
            }
        });
    }

    public void signup(View view){
        email = emailTextField.getText().toString();
        password = passswordTextField.getText().toString();
        phoneNumber = phoneNumberTextField.getText().toString();

        if (Utility.isValidEmail(email) && !password.isEmpty()) {

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("LOGIN", "createUserWithEmail:success");

/*
                                if (imageUri!= null) {

                                    writeNewUserInDatabase(email, mAuth.getUid(),phoneNumber,imageUri);

                                } else {

                                    //load default profile image
                                    Resources resources = getApplicationContext().getResources();
                                    imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(R.drawable.ic_background) + '/' + resources.getResourceTypeName(R.drawable.ic_background) + '/' + resources.getResourceEntryName(R.drawable.ic_background));
                                    writeNewUserInDatabase(email, mAuth.getUid(),phoneNumber,imageUri);
                                }
*/
                                writeNewUserInDatabase(email, mAuth.getUid(),phoneNumber);
                                Utility.sendVerificationEmail(SignUpActivity.this, user, mAuth, SignUpActivity.this);

                            } else {

                                Log.i("LOGIN", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
        } else {

            Toast.makeText(this,"Password/email not valid", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_RQUEST_CODE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
    }

    public void writeNewUserInDatabase(String email, String userId, String number){
        User user = new User(email,userId,number);
        userDatabase.child("users").child(userId).setValue(user);

    }


}



