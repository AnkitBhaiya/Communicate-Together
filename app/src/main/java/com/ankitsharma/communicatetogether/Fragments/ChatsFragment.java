package com.ankitsharma.communicatetogether.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ankitsharma.communicatetogether.Adapters.UsersAdapter;
import com.ankitsharma.communicatetogether.Models.Users;
import com.ankitsharma.communicatetogether.R;
import com.ankitsharma.communicatetogether.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChatsFragment extends Fragment {


    public ChatsFragment() {



    }

    FragmentChatsBinding binding;
    ArrayList<Users> list = new ArrayList<> ();
    FirebaseDatabase database;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       binding = FragmentChatsBinding.inflate (inflater,container,false);

        UsersAdapter adapter = new UsersAdapter (list,getContext ());
        binding.chatRecyclerView.setAdapter (adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager (getContext ());
        binding.chatRecyclerView.setLayoutManager (layoutManager);
        database = FirebaseDatabase.getInstance ();
        database.getReference ().child ("Users").addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              list.clear ();
                for (DataSnapshot dataSnapshot : snapshot.getChildren ()){
                    Users users = dataSnapshot.getValue (Users.class);
                    users.setUserId (dataSnapshot.getKey ());
                    if (!users.getUserId ().equals (FirebaseAuth.getInstance ().getUid ())) {
                        list.add (users);
                    }
                }
               adapter.notifyDataSetChanged ();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot ();
    }
}