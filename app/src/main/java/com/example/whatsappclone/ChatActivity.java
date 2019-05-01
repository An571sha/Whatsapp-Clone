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
    ArrayList<ChatForUser> chatSentToUser;
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

    //------RecyclerView is not displaying----------------
    public void updateRecyclerView(ArrayList<ChatForUser> list){
        mMessageRecycler =  findViewById(R.id.reyclerview);
        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
        mMessageAdapter = new MyAdapter(list);
        mMessageRecycler.setAdapter(mMessageAdapter);
        mMessageRecycler.setLayoutManager(layoutManager);


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
       /* testList = new ArrayList<>();
        testList.add(("Featured"));
        testList.add(("Categories"));
        testList.add(("Sell"));
        testList.add(("Settings"));
        testList.add(("Logout"));*/
        DatabaseReference userRef = userDatabase.child("chat").child(encodeString(userEmail));
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    chatList.clear();
                    keyAndEmailmap = new HashMap<>();
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        keyAndEmailmap.put(String.valueOf(userSnapshot.child(encodeString(clickedEmail)).getKey()),userSnapshot.child(encodeString(clickedEmail)).getValue());
                        chatList.add(userSnapshot.getValue().toString());

                    }
                    Log.i("chatList",chatList.toString());
                    updateRecyclerView(getMenulist(keyAndEmailmap));

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
        ArrayList<ChatForUser> mChatList;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView detailsTextView;
            public TextView contentTextView;
            public MyViewHolder(View v) {
                super(v);
                detailsTextView = v.findViewById(R.id.menu_details);
                contentTextView = v.findViewById(R.id.menu_title);
            }
        }

        public MyAdapter(ArrayList<ChatForUser> list){
            mChatList = list;
        }


        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            Context context = viewGroup.getContext();
            View v = LayoutInflater.from(context)
                    .inflate(R.layout.row, viewGroup, false);
            MyViewHolder vh = new MyViewHolder(v);
            Log.i("ChatListVh",vh.toString());
            return vh;
        }



        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder myViewHolder, int i) {
            ChatForUser chatForUser = mChatList.get(i);
            myViewHolder.contentTextView.setText( chatForUser.title);
            myViewHolder.detailsTextView.setText(chatForUser.details);
            Log.i("ChatListInAdapter",mChatList.toString());

        }

        @Override
        public int getItemCount() {
            Log.i("ChatList Size",String.valueOf(mChatList.size()));
            return mChatList.size();

        }
    }


    public ArrayList<ChatForUser> getMenulist(HashMap keyAndEmailmap) {

        chatSentToUser = new ArrayList<ChatForUser>();

        chatSentToUser.add( new ChatForUser(keyAndEmailmap.keySet().toString(), keyAndEmailmap.values().toString()) );
        return chatSentToUser;
    }

    public class ChatForUser {
        public String title, details;

        public ChatForUser(String title, String details){
            this.details = details;
            this.title = title;
        }

    }




}
