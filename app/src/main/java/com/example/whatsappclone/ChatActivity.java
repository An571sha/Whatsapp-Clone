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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mMessageRecycler;
    private RecyclerView.Adapter mMessageAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button sendButton;
    private EditText chatBox;
    private DatabaseReference userDatabase;
    private String clickedId;
    private String userId;
    ArrayList chatList;
    ArrayList testList;
    ArrayList<ChatForUser> chatSentToUser;
    HashMap<String,Object> keyAndEmailmap;
    String key;
    DatabaseReference user1AndUser2Reference;
    DatabaseReference user2AndUser1Reference;
    String combinedUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        sendButton = findViewById(R.id.send);
        chatBox = findViewById(R.id.chatbox);
        clickedId = getIntent().getStringExtra("clickedId");
        userId = getIntent().getStringExtra("userId");
        userDatabase = FirebaseDatabase.getInstance().getReference();
        onChatDatachanged();


    }

    public void updateRecyclerView(ArrayList<ChatForUser> list){
        mMessageRecycler =  findViewById(R.id.reyclerview);
        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
        mMessageAdapter = new MyAdapter(list);
        mMessageRecycler.setAdapter(mMessageAdapter);
        mMessageRecycler.setLayoutManager(layoutManager);


    }

    public String sortString(String userId, String clickedId){
        String[] s = new String[]{userId,clickedId};
        Arrays.sort(s);
        combinedUserId = String.join("_",s);
        return combinedUserId;
    }


    public void send(View view){
        Log.d("Logging userId",encodeString(userId));
        combinedUserId = userId + clickedId;
        user1AndUser2Reference = userDatabase.child("chat").child(sortString(userId,clickedId)).push();
        key = user1AndUser2Reference.getKey();
        ChatBetweenUsers  chatBetweenUsers = new ChatBetweenUsers(key, userId, chatBox.getText().toString());
        user1AndUser2Reference.setValue(chatBetweenUsers);
        onChatDatachanged();

        /* userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    user1AndUser2Reference = userDatabase.child("chat").child(encodeString(userId)+""+encodeString(clickedId)).push();
                    user2AndUser1Reference = userDatabase.child("chat").child(encodeString(clickedId)+""+encodeString(userId)).push();

                    key = user1AndUser2Reference.getKey();
                    ChatBetweenUsers  chatBetweenUsers = new ChatBetweenUsers(key, userId, chatBox.getText().toString());
                    user1AndUser2Reference.setValue(chatBetweenUsers);
                    onChatDatachanged();

                }else if( dataSnapshot.getValue() == user1AndUser2Reference){
                    key = user1AndUser2Reference.getKey();
                    ChatBetweenUsers  chatBetweenUsers = new ChatBetweenUsers(key, userId, chatBox.getText().toString());
                    user1AndUser2Reference.push().setValue(chatBetweenUsers);
                    onChatDatachanged();
                }else if( dataSnapshot.getValue() == user2AndUser1Reference){
                    key = user2AndUser1Reference.getKey();
                    ChatBetweenUsers  chatBetweenUsers = new ChatBetweenUsers(key, userId, chatBox.getText().toString());
                    user2AndUser1Reference.push().setValue(chatBetweenUsers);
                    onChatDatachanged();
                }

                Log.i("datasnap", dataSnapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

    }

    public static String encodeString(String string) {
        return string.replace(".", ",");
    }

    public static String decodeString(String string) {
        return string.replace(",", ".");
    }

    public void onChatDatachanged(){
        chatList = new ArrayList<>();
        DatabaseReference userRef = userDatabase.child("chat").child(encodeString(userId));
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    chatList.clear();
                    keyAndEmailmap = new HashMap<>();
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        keyAndEmailmap.put(String.valueOf(userSnapshot.child(encodeString(clickedId)).getKey()),
                                userSnapshot.child(encodeString(clickedId)).getValue());

                        chatList.add(userSnapshot.child(encodeString(clickedId)).toString());

                    }
                    updateRecyclerView(getMenulist(keyAndEmailmap));
                    Log.i("Map e",(chatList.toString()));


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

        chatSentToUser = new ArrayList<>();
        List<String> list1 = new ArrayList<String>(keyAndEmailmap.keySet());
        List<String> list2 = new ArrayList<String>(keyAndEmailmap.values());

        for(int i=0; i< list1.size(); i++) {
            chatSentToUser.add(new ChatForUser(list1.get(i), list2.get(i)));
        }

        Log.i("chatListsdfdsadsfasd",chatSentToUser.toString());
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

