package com.example.jclarens.chatapa.View;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jclarens.chatapa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AccountSettingActivity extends AppCompatActivity implements DialogEditProfileActivity.OnInputListener{
    private static final String TAG = "AccountSettingActivity";

    @Override
    public void sendInput(String input) {
        Log.d(TAG, "sendInput: got the input"+input);
        settingName.setText(input);

    }

    private final static int changeNameProfile = 1;
    private final static int changeStatusProfile = 2;
    private final static int changePhoneProfile= 3;
    private final static int changeWorkProfile = 4;
    private TextView settingName,settingStatus,settingPhone,settingEmail,settingWork;
    private Button btnCancelEditAccount,btnSaveEditAccount;
    private CircleImageView settingProfileImage;
    private final static int Gallery_Pick=1;

    private FirebaseAuth mAuth;
    private DatabaseReference getUserDataDb;
    private StorageReference storeImageIntoStorage;
    Bitmap thumb_bitmap = null;
    private StorageReference thumbImageRef;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);


        settingName = (TextView) findViewById(R.id.tv_name);
        settingStatus = (TextView) findViewById(R.id.tv_status);
        settingPhone = (TextView) findViewById(R.id.tv_phone);
        settingEmail = (TextView) findViewById(R.id.tv_email);
        settingWork = (TextView) findViewById(R.id.tv_work);
        settingProfileImage = (CircleImageView) findViewById(R.id.ci_profile_image);

        btnCancelEditAccount = (Button) findViewById(R.id.btn_cancelEditAccount);
        btnSaveEditAccount = (Button) findViewById(R.id.btn_saveEditAccount);
        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        String currentUser = mAuth.getCurrentUser().getUid();

        getUserDataDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
        getUserDataDb.keepSynced(true);
        storeImageIntoStorage = FirebaseStorage.getInstance().getReference().child("Profile_Images");
        thumbImageRef = FirebaseStorage.getInstance().getReference().child("Thumb_Images");
        getUserDataDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String email = dataSnapshot.child("user_email").getValue().toString();
                String phone = dataSnapshot.child("user_phone").getValue().toString();
                String work = dataSnapshot.child("user_current_work").getValue().toString();
                final String image = dataSnapshot.child("user_image").getValue().toString();
                String thumb_image = dataSnapshot.child("user_thumb_image").getValue().toString();

                settingName.setText(name);
                settingStatus.setText(status);
                settingEmail.setText(email);
                settingPhone.setText(phone);
                settingWork.setText(work);


                if (!image.equals("default_profile")){
                    //with(context) atau dimana lokasi activity
                    //.load kita mau load url image dari database yang mana sudah di get dari storage
                    //.placeholder dia load dulu default image baru dari dbFirebase jadi ketika create new account dia set default foto dulu
                    //Picasso.with(AccountSettingActivity.this).load(image).placeholder(R.drawable.default_profile).into(settingProfileImage);

                    //offline capability
                    Picasso.with(AccountSettingActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_profile).into(settingProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(AccountSettingActivity.this).load(image).placeholder(R.drawable.default_profile).into(settingProfileImage);
                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        settingProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i,Gallery_Pick);
            }
        });

            settingName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Opening Dialog");

                Bundle nameStatus = new Bundle();
                nameStatus.putInt("status_name", changeNameProfile);
                DialogEditProfileActivity dialog = new DialogEditProfileActivity();
                dialog.setArguments(nameStatus);
                dialog.show(getFragmentManager(), "Dialog Name");

            }
        });

        settingStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Opening Dialog");

                Bundle statusStatus = new Bundle();
                statusStatus.putInt("status_status", changeStatusProfile);
                DialogEditProfileActivity dialog = new DialogEditProfileActivity();
                dialog.setArguments(statusStatus);
                dialog.show(getFragmentManager(), "Dialog Status");
            }
        });
//
//        settingEmail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        settingPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle statusPhone = new Bundle();
                statusPhone.putInt("status_phone",changePhoneProfile);
                DialogEditProfileActivity dialog = new DialogEditProfileActivity();
                dialog.setArguments(statusPhone);
                dialog.show(getFragmentManager(),"Dialog Phone");
            }
        });

        settingWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle statusWork = new Bundle();
                statusWork.putInt("status_work",changeWorkProfile);
                DialogEditProfileActivity dialog = new DialogEditProfileActivity();
                dialog.setArguments(statusWork);
                dialog.show(getFragmentManager(),"Dialog Work");

            }
        });



        btnSaveEditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(AccountSettingActivity.this,DialogEditProfileActivity.class);
//                startActivity(i);
            }
        });

        btnCancelEditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    loadingBar.setTitle("Updating Profile Image");
                    loadingBar.setMessage("Please Wait....");
                    loadingBar.show();

                    Uri resultUri = result.getUri();

                    //tutor cideo 24 compress thumb image
                    File thumb_filepathUri = new File(resultUri.getPath());


                    String user_id = mAuth.getCurrentUser().getUid();

                    try{
                        thumb_bitmap = new Compressor(this).setMaxWidth(200).setMaxHeight(200).setQuality(50).compressToBitmap(thumb_filepathUri);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
                    final byte[] thumb_byte = byteArrayOutputStream.toByteArray();

                    StorageReference filePath = storeImageIntoStorage.child(user_id+".jpg");
                    //adding this too tutor 24

                    final StorageReference thumb_filepath = thumbImageRef.child((user_id+".jpg"));


                    filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(AccountSettingActivity.this,"Saving Your Profile Image Into Firebase Storage",Toast.LENGTH_LONG).show();

                                //variable untuk simpan foto dari storage firebase (download dari storage) ke database firebase
                                final String downloadUrl = task.getResult().getDownloadUrl().toString();
                                //add this too 24
                                UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        String thumb_downloadurl =  task.getResult().getDownloadUrl().toString();
                                        if (task.isSuccessful()){
                                            Map update_user_data = new HashMap();
                                            update_user_data.put("user_image", downloadUrl);
                                            update_user_data.put("user_thumb_images",thumb_downloadurl);

                                            getUserDataDb.updateChildren(update_user_data)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                Toast.makeText(AccountSettingActivity.this,"Image Updated Successfully",Toast.LENGTH_LONG).show();
                                                            loadingBar.dismiss();
                                                            }
                                                        }
                                                    });

                                        }
                                    }
                                });

// comment too 24 pindah ke atas
//                                getUserDataDb.child("user_image").setValue(downloadUrl)
//                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()){
//                                                    Toast.makeText(AccountSettingActivity.this,"Image Updated Successfully",Toast.LENGTH_LONG).show();
//                                                }
//                                            }
//                                        });
                            }
                            else
                            {
                                Toast.makeText(AccountSettingActivity.this,"Failed to Upload Your Profile Picture Into Firebase Storage",Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                            }
                        }
                    });

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
    }

}
