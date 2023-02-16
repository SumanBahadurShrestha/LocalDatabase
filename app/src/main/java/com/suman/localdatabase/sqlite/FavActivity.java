package com.suman.localdatabase.sqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.suman.localdatabase.R;
import com.suman.localdatabase.sqlite.adapter.EmployeeAdapter;
import com.suman.localdatabase.sqlite.model.Country;

import java.util.ArrayList;

public class FavActivity extends AppCompatActivity {
    ListView listView;
    SQLiteDatabaseHandler sqLiteDatabaseHandler;
    EmployeeAdapter employeeAdapter;
    ArrayList<Country> arrayList;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        context = this;
        listView = (ListView) findViewById(R.id.customfavListView);
        sqLiteDatabaseHandler = new SQLiteDatabaseHandler(this);
        arrayList = new ArrayList<>();
        showDataFromDatabase();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(FavActivity.this, DetailActivity.class);
                intent.putExtra("positionid", arrayList.get(i).id);
                startActivity(intent);
            }
        });
    }

    private void showDataFromDatabase() {
        arrayList = sqLiteDatabaseHandler.getFavData();
        employeeAdapter = new EmployeeAdapter(context, arrayList);
        listView.setAdapter(employeeAdapter);
        employeeAdapter.notifyDataSetChanged();
    }
}