package com.ankitsharma.communicatetogether;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.L;
import com.ankitsharma.communicatetogether.Adapters.ChatAdapter;
import com.ankitsharma.communicatetogether.Models.MessageModel;
import com.ankitsharma.communicatetogether.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;
    FirebaseDatabase databse;
    FirebaseAuth auth;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding = ActivityChatDetailBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());
        getSupportActionBar ().hide ();
        storage = FirebaseStorage.getInstance ();
        databse = FirebaseDatabase.getInstance ();
        auth = FirebaseAuth.getInstance ();
        final String senderId = auth.getUid ();
        String recieveId = getIntent ().getStringExtra ("userId");
        String userName = getIntent ().getStringExtra ("userName");
        String profilePic = getIntent ().getStringExtra ("profilePic");

        binding.username.setText (userName);
        Picasso.get ().load (profilePic).placeholder (R.drawable.profile).into (binding.profileImage);
        binding.backarrow.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (ChatDetailActivity.this,MainActivity.class);
                startActivity (intent);
            }
        });

        final ArrayList<MessageModel> messageModels = new ArrayList<> ();
        final ChatAdapter chatAdapter = new ChatAdapter (messageModels,this,recieveId);
        binding.recyclerView.setAdapter (chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager (this);
        binding.recyclerView.setLayoutManager (layoutManager);

        final  String senderRoom = senderId + recieveId;
        final String  recieverRoom = recieveId + senderId;

        databse.getReference ().child ("chats").child (senderRoom).addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageModels.clear ();
           for(DataSnapshot snapshot1 : snapshot.getChildren ()){
               MessageModel model = snapshot1.getValue (MessageModel.class);
               model.setMessageId (snapshot1.getKey ());


               messageModels.add (model);
           }
           chatAdapter.notifyDataSetChanged ();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.send.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String message = binding.etMessage.getText ().toString ();
                final MessageModel model = new MessageModel (senderId,message);
                model.setTimestamp (new Date ().getTime ());
                binding.etMessage.setText ("");
                databse.getReference ().child ("chats").child (senderRoom).push ()
                        .setValue (model).addOnSuccessListener (new OnSuccessListener<Void> () {
                    @Override
                    public void onSuccess(Void unused) {
                        databse.getReference ().child ("chats").child (recieverRoom).push ()
                                .setValue (model).addOnSuccessListener (new OnSuccessListener<Void> () {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });
                    }
                });
            }
        });
        binding.attach.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ();
                intent.setAction (Intent.ACTION_GET_CONTENT);
                intent.setType ("files/*");
                startActivityForResult (intent,33);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (data.getData () != null) {
            Uri sFile=data.getData ();
            binding.profileImage.setImageURI (sFile);
            final StorageReference reference=storage.getReference ().child ("Profile Picture")
                    .child (FirebaseAuth.getInstance ().getUid ());

            reference.putFile (sFile).addOnSuccessListener (new OnSuccessListener<UploadTask.TaskSnapshot> () {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl ().addOnSuccessListener (new OnSuccessListener<Uri> () {
                        @Override
                        public void onSuccess(Uri uri) {
                            databse.getReference ().child ("Users").child (FirebaseAuth.getInstance ().getUid ())
                                    .child ("profilePic").setValue (uri.toString ());


                        }
                    });


                }
            });

        }
    }
}