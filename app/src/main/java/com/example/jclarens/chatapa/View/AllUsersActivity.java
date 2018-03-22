package com.example.jclarens.chatapa.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.jclarens.chatapa.Model.AllUsers;
import com.example.jclarens.chatapa.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {
    private RecyclerView recyclerViewAllUsers;

    private DatabaseReference allDatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        recyclerViewAllUsers = (RecyclerView) findViewById(R.id.rcv_allUsers);
        recyclerViewAllUsers.setHasFixedSize(true);
        recyclerViewAllUsers.setLayoutManager(new LinearLayoutManager(this));

        allDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<AllUsers,AllUsersViewHolder> adapter = new FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder>(
                AllUsers.class,
                R.layout.cardview_allusers, AllUsersViewHolder.class,allDatabaseUsers ) {
            @Override
            protected void populateViewHolder(AllUsersViewHolder viewHolder, AllUsers model, final int position) {
                //int position itu get posisi user berdasarkan id
                viewHolder.setUser_name(model.getUser_name());
                viewHolder.setUser_status(model.getUser_status());
                viewHolder.setUser_phone(model.getUser_phone());
            //    viewHolder.setUser_image(getApplicationContext(),model.getUser_image());
                viewHolder.setUser_thumb_image(getApplicationContext(),model.getUser_thumb_image());


                final String user_id = getRef(position).getKey();
                //26 click 1 cardview dari recyclerviewnya
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent = new Intent (AllUsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("user_id",user_id);
                        startActivity(profileIntent);
                    }
                });

            }
        };
        recyclerViewAllUsers.setAdapter(adapter);
    }

    public static class AllUsersViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public AllUsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUser_name(String user_name) {
            TextView displayName = (TextView) mView.findViewById(R.id.user_name);
            displayName.setText(user_name);
        }

        public void setUser_status(String user_status) {
            TextView displayStatus = (TextView) mView.findViewById(R.id.user_status);
            displayStatus.setText(user_status);
        }

        public void setUser_phone(String user_phone) {
            TextView displayPhone = (TextView) mView.findViewById(R.id.user_phone);
            displayPhone.setText(user_phone);
        }

//        public void setUser_image(Context context, String user_image) {
//            CircleImageView displayImages = (CircleImageView) mView.findViewById(R.id.ci_userimg);
//            if (!displayImages.equals("default_profile")){
//                Picasso.with(context).load(user_image).into(displayImages);
//            }


        public void setUser_thumb_image(Context context, String user_image) {
            CircleImageView thumbImages = (CircleImageView) mView.findViewById(R.id.ci_userimg);
            if (!thumbImages.equals("default_profile")){
                // 25/24 dan tambahan placeholder dari 26
                Picasso.with(context).load(user_image).placeholder(R.drawable.default_profile).into(thumbImages);
            }

        }
    }

}