package com.suman.localdatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.suman.localdatabase.room.RoomActivity;
import com.suman.localdatabase.sqlite.ExternalStoragePermissions;
import com.suman.localdatabase.sqlite.SqliteActivity;

public class MainActivity extends AppCompatActivity {

    Button buttonRoom, buttonSqlite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ExternalStoragePermissions.verifyStorage((Activity) mContext);
        buttonRoom = (Button) findViewById(R.id.room);
        buttonSqlite = (Button) findViewById(R.id.sqlite);

        buttonRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RoomActivity.class));
            }
        });
        buttonSqlite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SqliteActivity.class));
            }
        });
    }
}