package com.example.whatsappclone;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.LinearLayout;
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
    LinearLayout linearLayout;
    String userEmail;
    String clickedEmail;
    ArrayList chatList;
    ArrayList<ChatBetweenUsers> chatSentToUser;
    HashMap<String,Object> keyAndEmailmap;
    String key;
    DatabaseReference user1AndUser2Reference;
    String chatId;
    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        clickedEmail = getIntent().getStringExtra("clickedEmail");
        this.setTitle(clickedEmail);
        sendButton = findViewById(R.id.send);
        chatBox = findViewById(R.id.chatbox);
        clickedId = getIntent().getStringExtra("clickedId");
        userEmail = getIntent().getStringExtra("userEmail");
        userId = getIntent().getStringExtra("userId");
        userDatabase = FirebaseDatabase.getInstance().getReference();
        chatId = generateChatId(userId,clickedId);
        onChatDataChanged();




    }

    public void updateRecyclerView(ArrayList<ChatBetweenUsers> list){
        mMessageRecycler =  findViewById(R.id.reyclerview);
        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.VERTICAL);
        mMessageAdapter = new MyAdapter(list);
        mMessageRecycler.setAdapter(mMessageAdapter);
        mMessageRecycler.setLayoutManager(layoutManager);


    }

    public String generateChatId(String userId, String clickedId){
        String[] s = new String[]{userId,clickedId};
        Arrays.sort(s);
        return String.join("_",s);
    }


    public void send(View view){
        Log.d("Logging userId",encodeString(userId));
        user1AndUser2Reference = userDatabase.child("chat").child(chatId).push();
        key = user1AndUser2Reference.getKey();
        com.example.whatsappclone.ChatBetweenUsers chatBetweenUsers = new com.example.whatsappclone.ChatBetweenUsers(key, userId, chatBox.getText().toString(),userEmail);
        user1AndUser2Reference.setValue(chatBetweenUsers);
        onChatDataChanged();
        hideKeybord(view);


        /* userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    user1AndUser2Reference = userDatabase.child("chat").child(encodeString(userId)+""+encodeString(clickedId)).push();
                    user2AndUser1Reference = userDatabase.child("chat").child(encodeString(clickedId)+""+encodeString(userId)).push();

                    key = user1AndUser2Reference.getKey();
                    ChatBetweenUsers  chatBetweenUsers = new ChatBetweenUsers(key, userId, chatBox.getText().toString());
                    user1AndUser2Reference.setValue(chatBetweenUsers);
                    onChatDataChanged();

                }else if( dataSnapshot.getValue() == user1AndUser2Reference){
                    key = user1AndUser2Reference.getKey();
                    ChatBetweenUsers  chatBetweenUsers = new ChatBetweenUsers(key, userId, chatBox.getText().toString());
                    user1AndUser2Reference.push().setValue(chatBetweenUsers);
                    onChatDataChanged();
                }else if( dataSnapshot.getValue() == user2AndUser1Reference){
                    key = user2AndUser1Reference.getKey();
                    ChatBetweenUsers  chatBetweenUsers = new ChatBetweenUsers(key, userId, chatBox.getText().toString());
                    user2AndUser1Reference.push().setValue(chatBetweenUsers);
                    onChatDataChanged();
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

    public void onChatDataChanged(){
        chatList = new ArrayList<>();
        DatabaseReference chatRef = userDatabase.child("chat").child(chatId);
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot chatDataSnapshot) {
                chatList.clear();
                if(chatDataSnapshot.exists()){
                    Iterable<DataSnapshot> chatMessagesRef = chatDataSnapshot.getChildren();

                    for( DataSnapshot chatMessageRef : chatMessagesRef ){
                        chatList.add(chatMessageRef.getValue(ChatBetweenUsers.class));
                    }


                    Log.i("chatList ",(chatList.toString()));
                    Log.i("DataSnapShot ",(chatDataSnapshot.getChildren().toString()));
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
        ArrayList<ChatBetweenUsers> mChatList;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView detailsTextView;
            public TextView contentTextView;
            public MyViewHolder(View v) {
                super(v);
                detailsTextView = v.findViewById(R.id.menu_details);
                contentTextView = v.findViewById(R.id.menu_title);
            }
        }

        public MyAdapter(ArrayList<ChatBetweenUsers> list){
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
            ChatBetweenUsers chatBetweenUsers = mChatList.get(i);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,3,10,3);
            if(chatBetweenUsers.getEmail().equals(userEmail)){
                layoutParams.gravity= Gravity.RIGHT;
                setTextViews(myViewHolder, layoutParams, chatBetweenUsers);

            }else{
                layoutParams.gravity= Gravity.LEFT;
                setTextViews(myViewHolder, layoutParams, chatBetweenUsers);
            }

            Log.i("ChatListInAdapter",mChatList.toString());

        }

        @Override
        public int getItemCount() {
            Log.i("ChatList Size",String.valueOf(mChatList.size()));
            return mChatList.size();

        }

        public void setTextViews(MyAdapter.MyViewHolder myViewHolder, LinearLayout.LayoutParams layoutParams, ChatBetweenUsers chatBetweenUsers ){
            myViewHolder.contentTextView.setLayoutParams(layoutParams);
            myViewHolder.detailsTextView.setLayoutParams(layoutParams);
            myViewHolder.contentTextView.setText(chatBetweenUsers.getEmail());
            myViewHolder.detailsTextView.setText(chatBetweenUsers.getMessage());
        }

    }

    public void hideKeybord(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }




}

