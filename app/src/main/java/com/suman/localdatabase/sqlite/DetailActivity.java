package com.suman.localdatabase.sqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.suman.localdatabase.R;
import com.suman.localdatabase.sqlite.model.Country;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Integer position = getIntent().getIntExtra("positionid", 1);
        System.out.println("sdghjkfdsasdfvbnm "+ position);
        Toast.makeText(this, "ID = " + position, Toast.LENGTH_SHORT).show();
        ImageView imageView = findViewById(R.id.detailimageView);
        SQLiteDatabaseHandler databaseHandler = new SQLiteDatabaseHandler(this);
        ArrayList<Country> arrayList = databaseHandler.fetchData();
//        Country country = arrayList.get(Integer.parseInt(position));
        Country country = databaseHandler.getSingleData(position);
        byte[] bytes = country.image;
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageView.setImageBitmap(bitmap);
    }
}