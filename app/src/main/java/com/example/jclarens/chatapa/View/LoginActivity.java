package com.example.jclarens.chatapa.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private Toolbar mToolbar;
    private EditText loginEmail,loginPassword;
    private Button loginButton;
    private ProgressDialog loadingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        mToolbar = (Toolbar)findViewById(R.id.toolbar_login);
//        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        //22
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        loadingbar = new ProgressDialog(this);
        loginEmail = (EditText) findViewById(R.id.et_email_login);
        loginPassword = (EditText) findViewById(R.id.et_password_login);
        loginButton = (Button) findViewById(R.id.btn_login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();
                LoginAccount(email,password);
            }
        });

    }

    private void LoginAccount(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginActivity.this, "Please Write Your Email Address", Toast.LENGTH_LONG).show();

        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Please Write Your Password", Toast.LENGTH_LONG).show();
        } else {
            loadingbar.setTitle("Login Account");
            loadingbar.setMessage("Please Wait...");
            loadingbar.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //22
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                String current_user_id = mAuth.getCurrentUser().getUid();
                                mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken);

                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Login Failed, Please Check Your Email or Password", Toast.LENGTH_LONG).show();
                            }
                            loadingbar.dismiss();
                        }
                    });
        }
    }
}
