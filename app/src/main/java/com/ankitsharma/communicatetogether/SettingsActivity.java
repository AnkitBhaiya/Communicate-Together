package com.ankitsharma.communicatetogether;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ankitsharma.communicatetogether.Models.Users;
import com.ankitsharma.communicatetogether.databinding.ActivitySettingsBinding;
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

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding=ActivitySettingsBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());
        getSupportActionBar ().hide ();

        storage=FirebaseStorage.getInstance ();
        auth=FirebaseAuth.getInstance ();
        database=FirebaseDatabase.getInstance ();

        binding.backArrow.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent i=new Intent (SettingsActivity.this, MainActivity.class);
                startActivity (i);
            }
        });

        binding.button.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String status = binding.About.getText ().toString ();
                String username = binding.editTextTextPersonName.getText ().toString ();
                String phone = binding.editTextTextPhone.getText ().toString ();

                HashMap<String,Object> obj = new HashMap<> ();
                obj.put ("userName",username);
                obj.put ("status",status);
                obj.put ("Phone Number",phone);

                database.getReference ().child ("Users").child (FirebaseAuth.getInstance ().getUid ())
                        .updateChildren (obj);
                Toast.makeText (SettingsActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show ();

            }
        });

        database.getReference ().child ("Users").child (FirebaseAuth.getInstance ().getUid ())
                .addListenerForSingleValueEvent (new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue (Users.class);
                        Picasso.get ().load (users.getProfilePic ()).placeholder (R.drawable.profile).into (binding.profileImage);

                        binding.About.setText (users.getStatus ());
                        binding.editTextTextPersonName.setText (users.getUserName ());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.plus.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent ();
                intent.setAction (Intent.ACTION_GET_CONTENT);
                intent.setType ("image/*");
                startActivityForResult (intent, 33);

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
                          database.getReference ().child ("Users").child (FirebaseAuth.getInstance ().getUid ())
                                  .child ("profilePic").setValue (uri.toString ());


                      }
                  });


                }
            });

        }
    }
}