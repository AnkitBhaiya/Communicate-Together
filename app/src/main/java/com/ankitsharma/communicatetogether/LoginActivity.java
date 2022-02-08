package com.ankitsharma.communicatetogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ankitsharma.communicatetogether.Models.Users;
import com.ankitsharma.communicatetogether.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
   ActivityLoginBinding binding;
   ProgressDialog progressDialog;
   FirebaseAuth auth;
   GoogleSignInClient mGoogleSignInClient;
   FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding = ActivityLoginBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());
        auth = FirebaseAuth.getInstance ();
        database = FirebaseDatabase.getInstance ();
        getSupportActionBar ().hide ();
        progressDialog = new ProgressDialog (LoginActivity.this);
        progressDialog.setTitle ("Login");
        progressDialog.setMessage ("We are Logging to your account. Please wait.....");


        binding.textView.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (LoginActivity.this,SignUpActivity.class);
                startActivity (i);
            }
        });
        binding.signInButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                if (binding.email.getText ().toString ().isEmpty ()){
                    binding.email.setError ("Enter your Email");
                    return;
                }
                if (binding.password.getText ().toString ().isEmpty ()){
                    binding.password.setError ("Enter your Password");
                    return;
                }

                progressDialog.show ();
                auth.signInWithEmailAndPassword (binding.email.getText ().toString (),binding.password.getText ().toString ())
                        .addOnCompleteListener (new OnCompleteListener<AuthResult> () {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss ();
                                if (task.isSuccessful ()){
                                    Intent intent = new Intent (LoginActivity.this,MainActivity.class);
                                    startActivity (intent);
                                }
                                else {
                                    Toast.makeText (LoginActivity.this, task.getException ().getMessage (), Toast.LENGTH_SHORT).show ();

                                }
                            }
                        });

            }
        });

        if (auth.getCurrentUser ()!= null){
            Intent intent = new Intent (LoginActivity.this,MainActivity.class);
            startActivity (intent);
        }


    }


}