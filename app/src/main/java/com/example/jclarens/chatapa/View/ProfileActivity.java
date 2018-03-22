package com.example.jclarens.chatapa.View;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jclarens.chatapa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button btnSendReq,btnDeclineReq;
    private TextView tvProfileName,tvProfileStatus,tvProfilePhone,tvProfileEmail,tvProfileWork;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;

    private FirebaseUser mCurrentUser;

    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private String CURRENT_STATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //28
  //      friendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Request");

        //visit_user_id yg di klik. ke siapa yg kita klik
        final String user_id = getIntent().getStringExtra("user_id");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Request");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        imageView = (ImageView) findViewById(R.id.imgv_profile);
        tvProfileName = (TextView) findViewById(R.id.tv_name);
        tvProfileStatus = (TextView) findViewById(R.id.tv_status);
        tvProfilePhone = (TextView) findViewById(R.id.tv_phone);
        tvProfileEmail = (TextView) findViewById(R.id.tv_email);
        tvProfileWork = (TextView) findViewById(R.id.tv_work);
        btnSendReq = (Button) findViewById(R.id.btn_sendFriendReq);
        btnDeclineReq = (Button) findViewById(R.id.btn_declineFriendReq);

        CURRENT_STATE = "not_friends";
//        mProgressDialog.setTitle("Loading user data");
//        mProgressDialog.setMessage("Please Wait");
//        mProgressDialog.setCanceledOnTouchOutside(false);
//        mProgressDialog.show();


 //       tvProfileStatus.setText(user_id);
////
////        //get data dari firebase --child=get user id yg di klik
        mUsersDatabase.addValueEventListener(new ValueEventListener() {
           @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("user_name").getValue().toString();
               // String status = dataSnapshot.child("user_status").getValue().toString();
                String email = dataSnapshot.child("user_email").getValue().toString();
                String phone = dataSnapshot.child("user_phone").getValue().toString();
                String work = dataSnapshot.child("user_current_work").getValue().toString();
                String image = dataSnapshot.child("user_image").getValue().toString();


                tvProfileName.setText(name);
                //tvProfileStatus.setText(status);
                tvProfileEmail.setText(email);
                tvProfilePhone.setText(phone);
                tvProfileWork.setText(work);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_profile).into(imageView);

               //=================FRIEND LIST / REQUEST FEATURE===========
               mFriendReqDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       if (dataSnapshot.hasChild(user_id)){
                           String request_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                           if (request_type.equals("received")){
                               CURRENT_STATE = "request_received";
                               btnSendReq.setText("Accept Friend Request");

                               btnDeclineReq.setVisibility(View.VISIBLE);
                               btnDeclineReq.setEnabled(true);

                           }else if (request_type.equals("sent")){

                               CURRENT_STATE = "request_sent";
                               btnSendReq.setText("Cancel Friend Request");

                               btnDeclineReq.setVisibility(View.INVISIBLE);
                               btnDeclineReq.setEnabled(false);

                           }

                       }else {
                           mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {
                                   if (dataSnapshot.hasChild(user_id)){
                                       CURRENT_STATE = "friend";
                                       btnSendReq.setText("Unfriend");

                                       btnDeclineReq.setVisibility(View.INVISIBLE);
                                       btnDeclineReq.setEnabled(false);

                                   }
                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {

                               }
                           });
                       }
                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });
//
// mProgressDialog.dismiss();
////            //next 28
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Toast.makeText(ProfileActivity.this,user_id,Toast.LENGTH_LONG).show();
////17 lapit
        btnSendReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSendReq.setEnabled(false);

                //-------------------------------------------------NOT FRIENDS-----------
                if (CURRENT_STATE.equals("not_friends")){
                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_id).child("request_type").setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()){
                               mFriendReqDatabase.child(user_id).child(mCurrentUser.getUid()).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {

                                       HashMap<String,String> notificationData = new HashMap<>();
                                       notificationData.put("from",mCurrentUser.getUid());
                                       notificationData.put("type","request");
                                        //.push() = create ramdom id dan di dalam key id random itu mau store data notificationData yg isinya seperti di atas
                                       mNotificationDatabase.child(user_id).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {
                                               //L18
                                               CURRENT_STATE = "requeest_sent";
                                               btnSendReq.setText("Cancel Request");

                                               btnDeclineReq.setVisibility(View.INVISIBLE);
                                               btnDeclineReq.setEnabled(false);
                                           }
                                       });

 //                                      Toast.makeText(ProfileActivity.this,"Request Sent Success",Toast.LENGTH_SHORT).show();

                                   }
                               });

                           }else{
                               Toast.makeText(ProfileActivity.this,"Failed sent Request",Toast.LENGTH_SHORT).show();
                           }
                           //jika task sukses atau tidak sukses tetap di enable
                                btnSendReq.setEnabled(true);
                            }
                    });
                }
// 22 install nodejs install npm, npm install -g firebase-tools lalu create new foder open terminal -- firebase login, firebase init
                //----------------CANCEL FRIEND REQ-----
                if(CURRENT_STATE.equals("request_sent") ){
                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendReqDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            btnSendReq.setEnabled(true);
                                            CURRENT_STATE = "not_friends";
                                            btnSendReq.setText("Send Friend Request");

                                            btnDeclineReq.setVisibility(View.INVISIBLE);
                                            btnDeclineReq.setEnabled(false);

                                        }
                                    });
                                }
                            });
                }

                //=========================REQUEST RECEIVED STATE 19lapit
                if (mCurrentUser.equals("request_received")){
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    mFriendDatabase.child(mCurrentUser.getUid()).child(user_id).setValue(currentDate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendDatabase.child(mCurrentUser.getUid()).child(user_id).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            mFriendReqDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    btnSendReq.setEnabled(true);
                                                                    CURRENT_STATE = "friends";
                                                                    btnSendReq.setText("Unfriend");

                                                                    btnDeclineReq.setVisibility(View.INVISIBLE);
                                                                    btnDeclineReq.setEnabled(false);

                                                                }
                                                            });
                                                        }
                                                    });
                                        }
                                    });
                                }
                            });
                }
            }
        });
    }
}
