package com.muhammad_sohag.socialmedia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.muhammad_sohag.socialmedia.adapter.UsersAdapter;
import com.muhammad_sohag.socialmedia.model.UsersModel;

import java.util.ArrayList;
import java.util.List;


public class UsersFragment extends Fragment {

  public UsersFragment() {
    // Required empty public constructor
  }

  private RecyclerView userRecyclerView;
  private UsersAdapter usersAdapter;
  private FirebaseFirestore database = FirebaseFirestore.getInstance();
  private CollectionReference databaseRef = database.collection("USERS");


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_users, container, false);
    userRecyclerView = view.findViewById(R.id.users_recycler);
    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    userRecyclerView.setHasFixedSize(true);
    userRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    List<UsersModel> list = new ArrayList<>();

    databaseRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
      @Override
      public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (queryDocumentSnapshots != null) {
          for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
            list.add(new UsersModel(documentSnapshot.getString("profileUrl"), documentSnapshot.getString("coverUrl")
                    , documentSnapshot.getString("name"), documentSnapshot.getString("batch"), documentSnapshot.getString("department")
                    , documentSnapshot.getString("userId"),documentSnapshot.getString("bio")));
          }
          usersAdapter = new UsersAdapter(getActivity(),list);
          userRecyclerView.setAdapter(usersAdapter);
          usersAdapter.notifyDataSetChanged();
        }
      }
    });

  }
}
