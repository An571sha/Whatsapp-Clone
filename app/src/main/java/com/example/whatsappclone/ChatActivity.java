package com.example.whatsappclone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
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
    ArrayList testList;
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


    }

    //------RecyclerView is not displaying 
    public void updateRecyclerView(ArrayList<String> list){
        mMessageRecycler =  findViewById(R.id.reyclerview);
        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
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
        testList = new ArrayList<>();
        testList.add(("Featured"));
        testList.add(("Categories"));
        testList.add(("Sell"));
        testList.add(("Settings"));
        testList.add(("Logout"));
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
                    updateRecyclerView(testList);

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
        ArrayList<String> mChatList;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView textView;
            public MyViewHolder(View v) {
                super(v);
                textView = v.findViewById(R.id.text_message_body);
            }
        }

        public MyAdapter(ArrayList<String> list){
            mChatList = list;
        }


        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            Context context = viewGroup.getContext();
            View v = LayoutInflater.from(context)
                    .inflate(R.layout.chat_box, viewGroup, false);
            MyViewHolder vh = new MyViewHolder(v);
            Log.i("ChatListVh",vh.toString());
            return vh;
        }



        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder myViewHolder, int i) {
            TextView mTextView = myViewHolder.textView;
            mTextView.setText(mChatList.get(i));
            Log.i("ChatList",mChatList.toString());

        }

        @Override
        public int getItemCount() {
            Log.i("ChatList Size",String.valueOf(mChatList.size()));
            return mChatList.size();

        }
    }



}
