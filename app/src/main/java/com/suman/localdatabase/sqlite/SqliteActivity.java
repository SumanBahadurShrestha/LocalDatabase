package com.suman.localdatabase.sqlite;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.suman.localdatabase.R;
import com.suman.localdatabase.sqlite.adapter.EmployeeAdapter;
import com.suman.localdatabase.sqlite.model.Country;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

public class SqliteActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    Context mContext;
    ListView listViews;
    ArrayList<Country> countryList;
    ArrayList<Country> searchList;
    EmployeeAdapter adapter;
    FloatingActionButton floatingActionButton;
    Dialog dialog;
    private SQLiteDatabaseHandler sqLiteDatabaseHandler;
    private static final int REQUEST_CODE_CAMERA = 200;
    private static final int REQUEST_CODE_READSTORAGE = 201;
    private static final int REQUEST_CODE_WSTORAGE = 202;
    private static final int REQUEST_CODE_WSTORAGED = 203;
    private static final int REQUEST_CODE_RSTORAGED = 204;
    private static final int SELECT_PICTURE = 205;
    String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
    //
    int pId;
    String pName;
    Country country;
    Button buttonbackup, buttonimport, buttoncsv, buttonfav;
    EditText editTextName, editTextSalary;
    Spinner spinnerDep;
    ImageView imageViewImage;
    Uri filepath;
    Bitmap bitmap;
    boolean searching = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite);
        mContext = this;
        sqLiteDatabaseHandler = new SQLiteDatabaseHandler(SqliteActivity.this);
        int scnt = sqLiteDatabaseHandler.count();
        countryList = new ArrayList<>();
        listViews = (ListView) findViewById(R.id.customListView);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        buttonbackup = (Button) findViewById(R.id.btbackup);
        buttonimport = (Button) findViewById(R.id.btimport);
        buttonfav = (Button) findViewById(R.id.lookfav);
        buttoncsv = (Button) findViewById(R.id.btcsv);
        showDataFromDatabase();
        listViews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SqliteActivity.this, DetailActivity.class);
                if (searching)
                    intent.putExtra("positionid", searchList.get(i).id);
                else
                    intent.putExtra("positionid", countryList.get(i).id);

                startActivity(intent);
            }
        });
        listViews.setMultiChoiceModeListener(multiChoiceModeListener);
        listViews.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);

        dialog = new Dialog(SqliteActivity.this);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.sqlite_add);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                dialog.setCancelable(false);
                dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                imageViewImage = dialog.findViewById(R.id.imageProfile);
                imageViewImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean pick = true;
                        if (pick){
                            if (ContextCompat.checkSelfPermission(SqliteActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(SqliteActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
                                ActivityCompat.requestPermissions(SqliteActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READSTORAGE);
                                return;
                            }else{
                                PickImage();
                            }
                        }else{
                            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(SqliteActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READSTORAGE);
                                return;
                            }else
                                PickImage();
                        }
                    }
                });
                editTextName = dialog.findViewById(R.id.editTextName);
                spinnerDep = dialog.findViewById(R.id.spinnerDepartment);
                editTextSalary = dialog.findViewById(R.id.editTextSalary);
                Button buttonAdd = dialog.findViewById(R.id.buttonAddEmployee);
                //generate Date
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                buttonAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = editTextName.getText().toString().trim();
                        String dep = spinnerDep.getSelectedItem().toString();
                        String salary = editTextSalary.getText().toString().trim();
                        String date = simpleDateFormat.format(calendar.getTime());
//                        String bpath = getPath(bitmap);
                        String ppath = getPath(filepath);
                        byte[] path = ImageViewToByte(imageViewImage);
                        if (inputsareCorrect(name, salary)){
                            boolean result = sqLiteDatabaseHandler.addEmployee(name, dep, date, salary, path, 0, ppath);
                            if (result) {
                                showDataFromDatabase();
                                Toast.makeText(SqliteActivity.this, "Add Success", Toast.LENGTH_SHORT).show();
                            }else
                                Toast.makeText(SqliteActivity.this, "SOrry", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();
            }
        });
        buttonbackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //for sdk between 23/29
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    requestPermissions(permissions, REQUEST_CODE_WSTORAGE);
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(SqliteActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WSTORAGE);
                    }
                }
                //for sdk above 30
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    if (!Environment.isExternalStorageManager()){
                        try {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                            startActivityIfNeeded(intent, REQUEST_CODE_WSTORAGED);
                        }catch (Exception e){
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            startActivityIfNeeded(intent, REQUEST_CODE_WSTORAGED);
                        }
                    }else{
                        backUpDatabase();
                    }
                }
//                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
//                    Toast.makeText(mContext, "export", Toast.LENGTH_SHORT).show();
//                    backUpDatabase();
//                }else
//                    ActivityCompat.requestPermissions(SqliteActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WSTORAGE);
            }
        });
        buttonimport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //for sdk between 23/29
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    requestPermissions(permissions, REQUEST_CODE_WSTORAGE);
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(SqliteActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_RSTORAGED);
                    }
                }
                //for sdk above 30
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    if (!Environment.isExternalStorageManager()){
                        try {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                            startActivityIfNeeded(intent, REQUEST_CODE_RSTORAGED);
                        }catch (Exception e){
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            startActivityIfNeeded(intent, REQUEST_CODE_RSTORAGED);
                        }
                    }else{
                        ImportDatabase();
                        Toast.makeText(mContext, "kkml", Toast.LENGTH_SHORT).show();
                    }
                }
//                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
//                    Toast.makeText(mContext, "export", Toast.LENGTH_SHORT).show();
//                }else
//                    ActivityCompat.requestPermissions(SqliteActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_WSTORAGE);
            }
        });
        buttonfav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SqliteActivity.this, FavActivity.class));
            }
        });
    }

    private String getPath(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imageArray = byteArrayOutputStream.toByteArray();
        String encodeImage = Base64.encodeToString(imageArray, Base64.DEFAULT);
        return encodeImage;
    }

    private byte[] ImageViewToByte(ImageView imageView) {
        bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] bytes = stream.toByteArray();
        return bytes;
    }

    private void PickImage() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        //        LunchSaveMethod.launch(intent);
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_PICTURE);
    }
//    ActivityResultLauncher<Intent> LunchSaveMethod = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == Activity.RESULT_OK){
//                    Intent data = result.getData();
//                    if (data != null && data.getData() != null){
//                        Uri uri = data.getData();
////                        imageURL = data.get);
//                        Bitmap selecedbitmap = null;
//                        try {
//                            selecedbitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        imageViewImage.setImageBitmap(selecedbitmap);
//                    }
//                }
//            }
//    );

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK ){
            if (requestCode == SELECT_PICTURE){
                filepath = data.getData();
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
////                    imageViewImage.setImageBitmap(bitmap);
                    imageViewImage.setImageURI(data.getData());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }


    private String getPath(Uri fileuri) {
        if (fileuri == null) return null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(fileuri, projection, null, null, null);
        if (cursor!=null){
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(columnIndex);
        }
        return fileuri.getPath();
//        Cursor cursor = getContentResolver().query(fileuri, null, null, null, null);
//        cursor.moveToFirst();
//        String document_id = cursor.getString(0);
//        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
//        cursor = getContentResolver().query(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + "=?", new String[]{document_id}, null
//        );
//        cursor.moveToFirst();
//        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//        cursor.close();
//        return path;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ){
                        PickImage();
                    }else
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
                }
                break;
            case REQUEST_CODE_READSTORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        PickImage();
                    }else
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READSTORAGE);
                }
                break;
            case REQUEST_CODE_WSTORAGE:
                if (grantResults.length > 0 &&  grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
//                        backUpDatabase();
//                    }else
//                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WSTORAGE);
                    Toast.makeText(mContext, "Granted", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(mContext, "Denay", Toast.LENGTH_SHORT).show();
            case REQUEST_CODE_WSTORAGED:
                if (grantResults.length > 0 &&  grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(mContext, "Granted sdk 30", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(mContext, "denay sdk 30", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void backUpDatabase() {
        File sdcard = new File(Environment.getExternalStorageDirectory() + "/" + "StudentBackup");
        File file = Environment.getExternalStorageDirectory();
        if (file.canWrite()){
            Toast.makeText(mContext, "Write" + file, Toast.LENGTH_SHORT).show();
        }
        if (!sdcard.exists())
            sdcard.mkdir();
        String csvfilename = "student.csv";
        String csvfilepath = sdcard.toString() + "/" + csvfilename;
        ArrayList<Country> countries = new ArrayList<>();
        countries = sqLiteDatabaseHandler.fetchData();
        FileWriter fileWriter = null;
        Toast.makeText(mContext, "size"+countries.size(), Toast.LENGTH_SHORT).show();
        try {
            fileWriter = new FileWriter(csvfilepath);
            for (int i = 0; i < countries.size(); i++){
                fileWriter.append("" + countries.get(i).getId());
                fileWriter.append(",");
                fileWriter.append("" + countries.get(i).getcName());
                fileWriter.append(",");
                fileWriter.append("" + countries.get(i).getcCode());
                fileWriter.append(",");
                fileWriter.append("" + countries.get(i).getcSalary());
                fileWriter.append(",");
                fileWriter.append("" + countries.get(i).getcDate());
                fileWriter.append(",");
                fileWriter.append("" + countries.get(i).getImage());
                fileWriter.append(",");
                fileWriter.append("" + countries.get(i).getFav());
                fileWriter.append(",");
                fileWriter.append("" + countries.get(i).getImage_url());
                fileWriter.append("\n");
                
                //
                String image_uri = countries.get(i).getImage_url();
                Uri uri = Uri.parse(image_uri);
                String imageName = uri.getLastPathSegment();
//                String imagePath = this.getCacheDir().toString();
                String imgpath = image_uri.replace(imageName, "");
                String newPath = Environment.getExternalStorageDirectory()+"/StudentBackup/" + "images";
                System.out.println( imageName + " aaaaaaaaaaaaaaa " + imgpath + " bbbbbbbbbbbb " + newPath);
                copyImages(imgpath, imageName, newPath);
            }
            fileWriter.flush();
            fileWriter.close();
            Toast.makeText(mContext, "data export", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyImages(String imagePath, String imageName, String newPath) {
        File file = new File(newPath);
        if (!file.exists()){
            file.mkdir();
            Toast.makeText(mContext, "created" + file, Toast.LENGTH_SHORT).show();
        }
        try {
            Toast.makeText(mContext, "inside" + file, Toast.LENGTH_SHORT).show();
            InputStream inputStream = new FileInputStream(imagePath+"/"+imageName);
            OutputStream outputStream = new FileOutputStream(newPath+"/"+imageName);
            byte[] bytes = new byte[1024];
            int len;
            while((len = inputStream.read(bytes)) != -1){
                outputStream.write(bytes, 0, len);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
            Toast.makeText(mContext, "Copied", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ImportDatabase() {
        sqLiteDatabaseHandler.resetData();
        File file = new File(Environment.getExternalStorageDirectory() + "/StudentBackup/"+ "student.csv");
        if (file.canWrite()){
            Toast.makeText(mContext, "Write" + file, Toast.LENGTH_SHORT).show();
        }
        try {
            CSVReader csvReader = new CSVReader(new FileReader(file));
            String[] nextline;
            while ((nextline = csvReader.readNext()) != null){
                String id = nextline[0];
                String name = nextline[1];
                String code = nextline[2];
                String date = nextline[3];
                String salary = nextline[4];
                String image = nextline[5];
                String fav = nextline[6];
                String imageURL = nextline[7];

                Uri imageuri = Uri.parse(imageURL);
                String imageName = imageuri.getLastPathSegment();
                String oldimgpath = imageURL.replace(imageName, "");
                String newimgpath = Environment.getExternalStorageDirectory() + "/StudentBackup/images/";
                String finalPath = imageURL.replace(oldimgpath, newimgpath);
                System.out.println(imageuri + " sdfghjklkjhgfdssdfghjk " + imageURL+"/"+finalPath);
//                //convert uri to byte
                byte[] bbytes = null;
//                InputStream fis = getContentResolver().openInputStream(imageuri);
                try {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    FileInputStream fileInputStream = new FileInputStream(new File(String.valueOf(imageuri)));
                    byte[] bytes = new byte[1024];
                    int lan;
                    while ((lan = fileInputStream.read(bytes)) != -1){
                        byteArrayOutputStream.write(bytes, 0, lan);
                    }
                    bbytes = byteArrayOutputStream.toByteArray();

                    System.out.println(imageuri+"sdfghjklkjhgfdssdfghjk" + Arrays.toString(bbytes));
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
                sqLiteDatabaseHandler.addEmployee(name, code, date, salary, bbytes, Integer.parseInt(fav), imageURL);
            }
        } catch (CsvValidationException | IOException e) {
            e.printStackTrace();
        }
    }

//    private byte[] getBytes(InputStream inputStream) throws IOException {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        byte[] bytes = new byte[1024];
//        int lan;
//        while ((lan = inputStream.read(bytes)) != -1){
//            byteArrayOutputStream.write(bytes, 0, lan);
//        }
//        return byteArrayOutputStream.toByteArray();
//    }

    private void showDataFromDatabase() {
        countryList = sqLiteDatabaseHandler.fetchData();
        adapter = new EmployeeAdapter(mContext, countryList);
        listViews.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Toast.makeText(mContext, "gg " + countryList.size(), Toast.LENGTH_SHORT).show();
    }

    private boolean inputsareCorrect(String name, String salary) {
        if (name.isEmpty()) {
            editTextName.setError("Please Enter Name.");
            editTextName.requestFocus();
            return false;
        }
        if (salary.isEmpty()){
            editTextSalary.setError("no salary");
            return false;
        }
        return true;
    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.first_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_menu:
                break;
            case R.id.sort_name:
                Collections.sort(countryList, Country.Name);
                Refresh();
                break;
            case R.id.sort_iddec:
                Collections.sort(countryList, Country.iddec);
                Refresh();
                break;
            case R.id.sort_idasc:
                Collections.sort(countryList, Country.idasc);
                Refresh();
                break;
            case R.id.sort_role:
                Collections.sort(countryList,Country.role);
                Refresh();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void Refresh() {
        searchList = new ArrayList<>();
        searchList.addAll(countryList);
        adapter.searchFilter(searchList);
//        adapter = new EmployeeAdapter(mContext, searchList);
//        listViews.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
    }

    //long press show menu
    AbsListView.MultiChoiceModeListener multiChoiceModeListener = new AbsListView.MultiChoiceModeListener() {
        @Override
        public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
            country = countryList.get(i);
            pId = country.id;
            pName = country.cName;
            actionMode.setTitle(pName);
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            getMenuInflater().inflate(R.menu.sec_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.delete:
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Delete" + pName)
                            .setMessage("Are you sure?")
                            .setIcon(R.drawable.ic_add_24)
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    int res = sqLiteDatabaseHandler.deleteData(pId);
                                    if (res>0) {
                                        showDataFromDatabase();
                                        actionMode.finish();
//                                        adapter.notifyDataSetChanged();
                                    }
                                    else
                                        Toast.makeText(mContext, "not deleted", Toast.LENGTH_SHORT).show();
                                }
                            });
                    builder.create().show();
                    break;
                case R.id.update:
                    Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.sqlite_add);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

                    editTextName = dialog.findViewById(R.id.editTextName);
                    spinnerDep = dialog.findViewById(R.id.spinnerDepartment);
                    editTextSalary = dialog.findViewById(R.id.editTextSalary);
                    imageViewImage = dialog.findViewById(R.id.imageProfile);
                    Button buttonAdd = dialog.findViewById(R.id.buttonAddEmployee);
                    byte[] oldImage = country.image;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(oldImage, 0, oldImage.length);
                    imageViewImage.setImageBitmap(bitmap);
                    editTextName.setText(country.cName);
                    editTextSalary.setText(String.valueOf(country.cSalary));
                    buttonAdd.setText("UPDATE");
                    imageViewImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            boolean pick = true;
                            if (pick){
                                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                                        ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                                    ActivityCompat.requestPermissions(SqliteActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
                                    ActivityCompat.requestPermissions(SqliteActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READSTORAGE);
                                    return;
                                }else{
                                    PickImage();
                                }
                            }else{
                                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                                    ActivityCompat.requestPermissions(SqliteActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READSTORAGE);
                                    return;
                                }else
                                    PickImage();
                            }
                        }
                    });
                    buttonAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String name = editTextName.getText().toString().trim();
                            String code = spinnerDep.getSelectedItem().toString();
                            String salary = editTextSalary.getText().toString().trim();
                            String id = String.valueOf(country.id);
                            System.out.println(name+code+salary+id);
                            boolean res = sqLiteDatabaseHandler.updateEmployee(name, code, salary, id, ImageViewToByte(imageViewImage), country.fav, filepath.toString());
                            if (!res)
                                Toast.makeText(mContext, "went wrong", Toast.LENGTH_SHORT).show();
                            else {
                                showDataFromDatabase();
                                actionMode.finish();
                            }
                        }
                    });
                    dialog.show();
                    break;

            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {}
    };

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }
    @Override
    public boolean onQueryTextChange(String s) {
        s = s.toLowerCase();
        searchList = new ArrayList<>();
        searching = true;
        for (Country country : countryList){
            String name = country.getcName().toLowerCase();
            if (name.contains(s)){
                searchList.add(country);
            }
        }
        adapter.searchFilter(searchList);
//        adapter = new EmployeeAdapter(mContext, searchList);
//        listViews.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
        return false;
    }
}