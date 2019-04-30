package com.example.whatsappclone;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mMessageRecycler;
    private RecyclerView.Adapter mMessageAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button sendButton;
    private EditText chatBox;
    private DatabaseReference userDatabase;
    private String clickedEmail;
    private String userEmail;
    ArrayList chatList;
    HashMap<String,Object> keyAndEmailmap;
    String key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        sendButton = findViewById(R.id.send);
        chatBox = findViewById(R.id.chatbox);
        clickedEmail = getIntent().getStringExtra("clickedEmail");
        Log.d("Logging clickedEmail", clickedEmail);
        userEmail = getIntent().getStringExtra("userEmail");
        userDatabase = FirebaseDatabase.getInstance().getReference();
        onChatDatachanged();
        //----------recyclerView--------------//


    }

    public void updateRecyclerView(ArrayList list){
        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview);
        layoutManager = new LinearLayoutManager(this);
        mMessageRecycler.setLayoutManager(layoutManager);
        mMessageAdapter = new MyAdapter(list);
        mMessageRecycler.setAdapter(mMessageAdapter);

    }



    public void send(View view){
        Log.d("Logging userEmail",encodeString(userEmail));
        DatabaseReference dbReference = userDatabase.child("chat").child(encodeString(userEmail)).push();
        key = dbReference.getKey();
        Map<String,Object> map = new HashMap<>();
        map.put(encodeString(clickedEmail) ,chatBox.getText().toString());
        dbReference.updateChildren(map);
        onChatDatachanged();


    }

    public static String encodeString(String string) {
        return string.replace(".", ",");
    }

    public static String decodeString(String string) {
        return string.replace(",", ".");
    }

    public void onChatDatachanged(){
        chatList = new ArrayList<>();
        DatabaseReference userRef = userDatabase.child("chat").child(encodeString(userEmail));
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    chatList.clear();
                    keyAndEmailmap = new HashMap<>();
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        keyAndEmailmap.put(userSnapshot.getKey(),userSnapshot.getValue());
                        chatList.add(userSnapshot.getValue());
                    }
                    Log.i("ChatList",chatList.toString());
                    updateRecyclerView(chatList);

                }else{
                    Toast.makeText(ChatActivity.this, "No chats were found",
                            Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
        ArrayList<String> chatList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView textView;
            public MyViewHolder(TextView v) {
                super(v);
                textView = v;
            }
        }

        public MyAdapter(ArrayList chatListConstructor){
            chatList = chatListConstructor;
            chatList.add("1");
            chatList.add("2");
            chatList.add("3");
        }


        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            TextView v = (TextView) LayoutInflater.from(ChatActivity.this)
                    .inflate(R.layout.chat_box, viewGroup, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder myViewHolder, int i) {
            myViewHolder.textView.setText(chatList.get(i));

        }

        @Override
        public int getItemCount() {
            return chatList.size();
        }
    }



}
