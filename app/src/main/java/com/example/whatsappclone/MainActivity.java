package com.example.whatsappclone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    String email;
    String userId;

    Intent intent;
    ListView userListView;
    ArrayAdapter userListArrayAdapter;
    DatabaseReference userDatabase;
    ArrayList userArrayList;
    ArrayList userIdArrayList;
    HashMap<String,Object> keyAndEmailmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = getIntent().getStringExtra("userEmail");
        this.setTitle(email);
        userId = getIntent().getStringExtra("userNameId");
        userListView = findViewById(R.id.friendsListView);
        userDatabase = FirebaseDatabase.getInstance().getReference();
        onUserDataChange();

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedId = userIdArrayList.get(position).toString();
                intent = new Intent(MainActivity.this,ChatActivity.class);
                intent.putExtra("clickedId",clickedId);
                intent.putExtra("userId", userId);
                Log.d("Logging The Value", clickedId);
                startActivity(intent);
            }
        });


    }


    public void signout(View view){
        FirebaseAuth.getInstance().signOut();
        intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    public void onUserDataChange(){
        userArrayList = new ArrayList<>();
        userIdArrayList = new ArrayList<>();
        DatabaseReference userRef = userDatabase.child("users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    userArrayList.clear();
                    keyAndEmailmap = new HashMap<>();
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        keyAndEmailmap.put(userSnapshot.getKey(),userSnapshot.child("email").getValue().toString());
                        userArrayList.add(userSnapshot.child("email").getValue().toString());
                        userIdArrayList.add(userSnapshot.child("userId").getValue().toString());
                    }

                    userListArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, userArrayList);

                    userListView.setAdapter(userListArrayAdapter);

                }else{
                    Toast.makeText(MainActivity.this, "No users were found",
                            Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
