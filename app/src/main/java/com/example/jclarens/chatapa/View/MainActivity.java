package com.example.jclarens.chatapa.View;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jclarens.chatapa.Adapter.TabsPagerAdapter;
import com.example.jclarens.chatapa.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabsPagerAdapter mTabsPagerAdapter;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //cek uses exist and already signin or not
        mAuth = FirebaseAuth.getInstance();
        //tabs mainactvt
        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        mTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        viewPager.setAdapter(mTabsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.main_tablayout);
        tabLayout.setupWithViewPager(viewPager);

//
//        mToolbar = (Toolbar)findViewById(R.id.toolbar_main);
//        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Kuy Chat!");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

//jika user tidak loginkirim ke halaman startpage utk login
        if (currentUser == null) {
//            Intent startPageIntent = new Intent(MainActivity.this,StartPageActivity.class);
//            //not allow the user / send user from main actvt to the start page actvt if he is not login
//            startPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(startPageIntent);
//            finish();
            LogoutUser();
        }
    }

    private void LogoutUser() {
        Intent startPageIntent = new Intent(MainActivity.this, StartPageActivity.class);
        //not allow the user / send user from main actvt to the start page actvt if he is not login
        startPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startPageIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        //jika seseorang mengklik button logout
        if (item.getItemId() == R.id.btn_logout) {
            mAuth.signOut();
            LogoutUser();
        }
        if (item.getItemId() == R.id.btn_setting) {
            Intent i = new Intent(MainActivity.this, AccountSettingActivity.class);
            //not allow the user / send user from main actvt to the start page actvt if he is not login
            startActivity(i);

        }
        if (item.getItemId() == R.id.btn_allusers) {
            Intent i = new Intent(MainActivity.this, AllUsersActivity.class);
            startActivity(i);

        }
        return true;

    }
}