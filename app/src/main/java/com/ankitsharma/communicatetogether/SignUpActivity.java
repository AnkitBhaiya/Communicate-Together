package com.ankitsharma.communicatetogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ankitsharma.communicatetogether.Models.Users;
import com.ankitsharma.communicatetogether.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
   private FirebaseAuth mAuth;
   ActivitySignUpBinding binding;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding = ActivitySignUpBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());
        getSupportActionBar ().hide ();
        binding.textView.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (SignUpActivity.this,LoginActivity.class);
                startActivity (i);
            }
        });


        mAuth = FirebaseAuth.getInstance ();
        database = FirebaseDatabase.getInstance ();
        progressDialog = new ProgressDialog (SignUpActivity.this);
        progressDialog.setTitle ("Creating Account");
        progressDialog.setMessage ("We are creating your account");
        binding.signUpButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                progressDialog.show ();
                mAuth.createUserWithEmailAndPassword
                        (binding.email.getText ().toString (),binding.password.getText ().toString ())
                        .addOnCompleteListener (new OnCompleteListener<AuthResult> () {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss ();
                                if (task.isSuccessful ()){
                                    Users user = new Users (binding.username.getText ().toString (),binding.email.getText ().toString (),
                                    binding.password.getText ().toString ());
                                    String id = task.getResult ().getUser ().getUid ();
                                    database.getReference ().child ("Users").child (id).setValue (user);
                                    Toast.makeText (SignUpActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show ();
                                    Intent i = new Intent (SignUpActivity.this,MainActivity.class);
                                    startActivity (i);
                                }
                                else {
                                    Toast.makeText (SignUpActivity.this,task.getException ().getMessage (),Toast.LENGTH_SHORT).show ();

                                }
                            }
                        });
            }
        });

    }

}