package com.example.whatsappclone;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mMessageRecycler;
    private RecyclerView.Adapter mMessageAdapter;
    private Button sendButton;
    private EditText chatBox;
    private DatabaseReference userDatabase;
    private String clickedEmail;
    private String userEmail;
    String key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview);
        sendButton = findViewById(R.id.send);
        chatBox = findViewById(R.id.chatbox);
        clickedEmail = getIntent().getStringExtra("clickedEmail");
        Log.d("Logging clickedEmail", clickedEmail);
        userEmail = getIntent().getStringExtra("userEmail");
        Log.d("Logging userEmail", userEmail);
        userDatabase = FirebaseDatabase.getInstance().getReference();

        mMessageAdapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        };
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));

    }

    public void send(View view){
        DatabaseReference dbReference = userDatabase.child("chat").child(userEmail).push();
        key = dbReference.getKey();
        Map<String,Object> map = new HashMap<>();
        map.put(clickedEmail ,chatBox.getText().toString());
        dbReference.updateChildren(map);


    }
}
