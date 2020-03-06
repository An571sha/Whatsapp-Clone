package com.example.whatsappclone.utility;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Utility {
    public static void sendVerificationEmail(Activity activity, final FirebaseUser user, FirebaseAuth mAuth, final Context context){
        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "Verification email sent to " + user.getEmail(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("Error", "sendEmailVerification", task.getException());
                    Toast.makeText(context, "Failed to send verification email.",
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
