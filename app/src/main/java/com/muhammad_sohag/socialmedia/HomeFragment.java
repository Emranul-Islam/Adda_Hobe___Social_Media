package com.muhammad_sohag.socialmedia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.muhammad_sohag.socialmedia.Custom.LoadingDialog;
import com.muhammad_sohag.socialmedia.adapter.PostAdapter;
import com.muhammad_sohag.socialmedia.model.PostModel;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private CollectionReference databaseRaf = database.collection("POST");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.home_recycler);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        List<PostModel> postModelList = new ArrayList<>();
        PostAdapter adapter = new PostAdapter(getActivity(), postModelList);
        recyclerView.setAdapter(adapter);
        LoadingDialog dialog = new LoadingDialog(getActivity());
        dialog.startLoadingDialog();

        Query firstQuery = databaseRaf.limit(3).orderBy("timestamp", Query.Direction.DESCENDING);
        firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            PostModel postModel = documentChange.getDocument().toObject(PostModel.class);
                            postModelList.add(postModel);
                            adapter.notifyDataSetChanged();
                            dialog.dismissLoadingDialog();
                        }
                    }

                } else {
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismissLoadingDialog();
                }
            }
        });

    }
}