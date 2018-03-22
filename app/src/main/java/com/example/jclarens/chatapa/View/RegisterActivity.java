package com.example.jclarens.chatapa.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jclarens.chatapa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ProgressDialog loadingBar;

    private EditText registerName;
    private EditText registerEmail;
    private EditText registerPassword;
    private Button registerButton;

    private FirebaseAuth mAuth;
    private DatabaseReference storeIntoDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);


//        mToolbar = (Toolbar) findViewById(R.id.toolbar_register);
//      setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign Up");
        //supaya kalo back balik ke parent nya (startpageactvt)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        registerName = (EditText) findViewById(R.id.et_name_register);
        registerEmail = (EditText) findViewById(R.id.et_email_register);
        registerPassword = (EditText) findViewById(R.id.et_password_register);
        registerButton = (Button) findViewById(R.id.btn_register);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = registerName.getText().toString();
                String email = registerEmail.getText().toString();
                String password = registerPassword.getText().toString();

                    RegisterAccount(name, email, password);
            }
        });
    }

    private void RegisterAccount(final String name, final String email, String password) {
        //cek jika kolom name kosong
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(RegisterActivity.this, "Please Write Your Name", Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "Please Write Your Email Address", Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "Please Write your Password", Toast.LENGTH_LONG).show();
        } else {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please Wait, while we creating account for you");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                String currentUser = mAuth.getCurrentUser().getUid();
                                storeIntoDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
                                storeIntoDb.child("user_name").setValue(name);
                                storeIntoDb.child("user_email").setValue(email);
                                storeIntoDb.child("user_status").setValue("Hey There, i am using Kuy Chat!");
                                storeIntoDb.child("user_phone").setValue("Phone Number");
                                storeIntoDb.child("user_current_work").setValue("Current Work");
                                storeIntoDb.child("user_image").setValue("default_profile");
                                storeIntoDb.child("user_thumb_image").setValue("default_image");
                                storeIntoDb.child("device_token").setValue(deviceToken)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Intent i = new Intent(RegisterActivity.this,MainActivity.class);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(i);
                                                    finish();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(RegisterActivity.this, "Register Failed, Try Again...", Toast.LENGTH_LONG).show();
                            }
                            loadingBar.dismiss();
                        }
                    });
        }
    }
}
