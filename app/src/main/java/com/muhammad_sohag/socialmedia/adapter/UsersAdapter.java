package com.muhammad_sohag.socialmedia.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.muhammad_sohag.socialmedia.R;
import com.muhammad_sohag.socialmedia.UserProfileActivity;
import com.muhammad_sohag.socialmedia.model.UsersModel;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private Context context;
    private List<UsersModel> usersModels;

    public UsersAdapter(Context context, List<UsersModel> usersModels) {
        this.context = context;
        this.usersModels = usersModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.name.setText(usersModels.get(position).getName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_launcher_background);
        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(usersModels.get(position).getProfileImage())
                .into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showProfileIntent = new Intent(context, UserProfileActivity.class);
                showProfileIntent.putExtra("userId",usersModels.get(position).getUserId());
                showProfileIntent.putExtra("name",usersModels.get(position).getName());
                showProfileIntent.putExtra("profile",usersModels.get(position).getProfileImage());
                context.startActivity(showProfileIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return usersModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView image;
        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.users_item_image);
            name = itemView.findViewById(R.id.users_item_name);
        }
    }
}
