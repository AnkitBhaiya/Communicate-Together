package com.ankitsharma.communicatetogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ankitsharma.communicatetogether.Adapters.ChatAdapter;
import com.ankitsharma.communicatetogether.Models.MessageModel;
import com.ankitsharma.communicatetogether.databinding.ActivityGroupChattingBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatting extends AppCompatActivity {

    ActivityGroupChattingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding = ActivityGroupChattingBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());
        getSupportActionBar ().hide ();
        binding.backarrow.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (GroupChatting.this,MainActivity.class);
                startActivity (intent);
            }
        });
       final FirebaseDatabase database = FirebaseDatabase.getInstance ();
       final ArrayList<MessageModel> messageModels = new ArrayList<> ();

       final String senderId =FirebaseAuth.getInstance ().getUid ();
       binding.username.setText ("Friends Group");

       final ChatAdapter adapter = new ChatAdapter (messageModels,this);

       binding.recyclerView.setAdapter (adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager (this);
        binding.recyclerView.setLayoutManager (layoutManager);

        database.getReference ().child ("Group Chat")
                .addValueEventListener (new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear ();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren () ){
                            MessageModel model = dataSnapshot.getValue (MessageModel.class);
                            model.setMessageId (dataSnapshot.getKey ());
                            messageModels.add (model);
                        }
                        adapter.notifyDataSetChanged ();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.send.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                final String message = binding.etMessage.getText ().toString ();
                final MessageModel model = new MessageModel (senderId,message);
                model.setTimestamp (new Date ().getTime ());
                binding.etMessage.setText ("");
                database.getReference ().child ("Group Chats").push ()
                        .setValue (model).addOnSuccessListener (new OnSuccessListener<Void> () {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });

            }
        });

    }
}