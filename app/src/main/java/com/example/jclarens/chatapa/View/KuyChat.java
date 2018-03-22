package com.example.jclarens.chatapa.View;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by jclarens on 18/01/18.
 */

public class KuyChat extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//firebase offline capability add name juga di manifest
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //PICASSO
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }
}
