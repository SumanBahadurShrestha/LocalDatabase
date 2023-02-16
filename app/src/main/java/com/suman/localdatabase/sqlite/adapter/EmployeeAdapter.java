package com.suman.localdatabase.sqlite.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.suman.localdatabase.R;
import com.suman.localdatabase.sqlite.SQLiteDatabaseHandler;
import com.suman.localdatabase.sqlite.model.Country;

import java.util.ArrayList;
import java.util.List;

public class EmployeeAdapter extends BaseAdapter {
    public Context context;
    int layoutResources;
    SQLiteDatabaseHandler databaseHandler;
    ArrayList<Country> countryArrayList;
    LayoutInflater inflater;
    public EmployeeAdapter(Context context, ArrayList<Country> countryArrayList) {
        this.context = context;
        this.countryArrayList = countryArrayList;
        databaseHandler = new SQLiteDatabaseHandler(context.getApplicationContext());
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return countryArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        view = inflater.inflate(R.layout.sqlite_list_layout, null);
        view = inflater.inflate(R.layout.sqlite_list_layout, null);
        ImageView imageView = view.findViewById(R.id.imageImage);
        TextView textViewName = view.findViewById(R.id.textViewName);
        TextView textViewCode = view.findViewById(R.id.textViewDepartment);
        TextView textViewSalary = view.findViewById(R.id.textViewSalary);
        TextView textViewDate = view.findViewById(R.id.textViewJoiningDate);
        ImageView imageFav = view.findViewById(R.id.favImage);
        ImageView imageSelectedFav = view.findViewById(R.id.favedImage);
//        Button buttone = view.findViewById(R.id.buttonEditEmployee);
//        Button buttond = view.findViewById(R.id.buttonDeleteEmployee);
        Country country = countryArrayList.get(i);
        byte[] image = country.image;
        System.out.println("sdfghjklkjhgfdssdfghjk" + image);
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        imageView.setImageBitmap(bitmap);
        textViewName.setText(country.cName);
        textViewCode.setText(country.cCode);
        textViewSalary.setText(String.valueOf(country.cSalary));
        textViewDate.setText(country.cDate);
        if (country.fav == 0){
            imageFav.setVisibility(View.VISIBLE);
            imageSelectedFav.setVisibility(View.GONE);
        }else{
            imageFav.setVisibility(View.GONE);
            imageSelectedFav.setVisibility(View.VISIBLE);
        }

        imageFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEmployee(country);
            }
        });
        imageSelectedFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEmployee(country);
            }
        });
//        buttone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getSingleData(country.id);
//                updateEmployee(country);
//            }
//        });
//        buttond.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                deleteEmployee(country.id);
//            }
//        });
        return view;
    }

    private void updateEmployee(Country country) {
        boolean res;
        if (country.fav == 0) {
            res = databaseHandler.updateEmployee(country.cName, country.cCode, String.valueOf(country.cSalary), String.valueOf(country.id), country.image, 1, country.getImage_url());
        } else {
            res = databaseHandler.updateEmployee(country.cName, country.cCode, String.valueOf(country.cSalary), String.valueOf(country.id), country.image, 0, country.getImage_url());
        }
        if (res)
            reloadData();
        else
            Toast.makeText(context, "Some Thing Went Wrong", Toast.LENGTH_SHORT).show();

    }

    //extends ArrayAdapter<Country>---------------------------------------------------------------------------------------------
//    public EmployeeAdapter(@NonNull Context context, int resource, List<Country> objects, SQLiteDatabaseHandler sqLiteDatabaseHandler) {
//        super(context, resource, objects);
//        this.context = context;
//        this.layoutResources = resource;
//        this.countries = objects;
//        this.databaseHandler = sqLiteDatabaseHandler;
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        View itemView = LayoutInflater.from(context).inflate(layoutResources, null);
//        //binding
//        Country country = countries.get(position);
//        countryArrayList = new ArrayList<>();
//        //viewholder
//        ImageView imageView = itemView.findViewById(R.id.imageImage);
//        TextView textViewName = itemView.findViewById(R.id.textViewName);
//        TextView textViewCode = itemView.findViewById(R.id.textViewDepartment);
//        TextView textViewSalary = itemView.findViewById(R.id.textViewSalary);
//        TextView textViewDate = itemView.findViewById(R.id.textViewJoiningDate);
//        Button buttone = itemView.findViewById(R.id.buttonEditEmployee);
//        Button buttond = itemView.findViewById(R.id.buttonDeleteEmployee);
//        //binding
//        byte[] image = country.image;
//        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
//        imageView.setImageBitmap(bitmap);
//        textViewName.setText(country.cName);
//        textViewCode.setText(country.cCode);
//        textViewSalary.setText(String.valueOf(country.cSalary));
//        textViewDate.setText(country.cDate);
//
//        buttone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getSingleData(country.id);
//                updateEmployee(country);
//            }
//        });
//        buttond.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                deleteEmployee(country.id);
//            }
//        });
//        return itemView;
//    }

    private void reloadData() {
        countryArrayList = databaseHandler.fetchData();
        notifyDataSetChanged();
    }

    private void getSingleData(int id) {
//        countryArrayList = databaseHandler.getSingleData(id);
//        System.out.println(countryArrayList.get(0).cDate);
        Country country = databaseHandler.getSingleData(id);
        System.out.println(country.cName);
    }

    public void searchFilter(ArrayList<Country> searchList) {
        countryArrayList = new ArrayList<>();
        countryArrayList.addAll(searchList);
        notifyDataSetChanged();
    }


    //recycler ADapter-----------------------------------------------------------------------------------------------------------------
//        public EmployeeAdapter(Context context, ArrayList<Country> countries) {
//        this.context = context;
//        this.countries = countries;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.sqlite_list_layout, parent, false);
//        return new EmployeeAdapter.ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Country country = countries.get(position);
//        holder.textViewName.setText(country.cName);
//        holder.textViewCode.setText(country.cCode);
//        holder.textViewSalary.setText((int) country.cSalary);
//        holder.textViewDate.setText(country.cDate);
//    }
//
//    @Override
//    public int getItemCount() {
//        return countries.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        TextView textViewName, textViewCode, textViewSalary, textViewDate;
//        Button buttone, buttond;
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            textViewName = itemView.findViewById(R.id.textViewName);
//            textViewCode = itemView.findViewById(R.id.textViewDepartment);
//            textViewSalary = itemView.findViewById(R.id.textViewSalary);
//            textViewDate = itemView.findViewById(R.id.textViewJoiningDate);
//            buttone = itemView.findViewById(R.id.buttonEditEmployee);
//            buttond = itemView.findViewById(R.id.buttonDeleteEmployee);
//        }
//    }
}
