package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import taimoor.sultani.sweetalert2.Sweetalert;

public class MainActivity2 extends AppCompatActivity {
    RecyclerView files;
    Button addfiles;
    Sweetalert pDialog;
    String directoryname;
    ArrayList<set_file_data> data;
    TextView nofiles;
    file_adapter adapter;
    Uri uri;
    private void closeNow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            finishAffinity();
        }

        else
        {
            finish();
        }
    }
    public static String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }
    public boolean createDirectory(String name){
        File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"ReminderAndTodo/Reminders");
        if(!file.exists()){
          file.mkdirs();
        }
         file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/ReminderAndTodo/Reminders",name);
            if(!file.exists())
            {
                file.mkdirs();
            }

      return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MainActivity2.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity2.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            } else {
                //do something
            }
        }

        files=findViewById(R.id.files);
        addfiles=findViewById(R.id.add_files);
        directoryname="reminder1";
        nofiles=findViewById(R.id.no_files);
        createDirectory(directoryname);
        pDialog = new Sweetalert(this, Sweetalert.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);


        data=new ArrayList<set_file_data>();
      adapter= new file_adapter(files,nofiles,data,MainActivity2.this);

        files.setHasFixedSize(true);
        files.setLayoutManager(new LinearLayoutManager(this));
        files.setAdapter(adapter);
        addfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, 100);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && data!=null) {
            uri = data.getData();
            String type = uri.getPath();
            String filename=getFileName(uri);
            String size="0kb";
            String scheme = uri.getScheme();
            File f = null;
            if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                int dataSize = 0;
                try {
                    InputStream fileInputStream = getApplicationContext().getContentResolver().openInputStream(uri);
                    dataSize = fileInputStream.available();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                size=humanReadableByteCountSI(dataSize);

            } else if (scheme.equals(ContentResolver.SCHEME_FILE)) {
                String path = uri.getPath();
                try {
                    f = new File(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                size=humanReadableByteCountSI(f.length());
            }
            Log.d("data","file name "+filename+" size :"+size);
            File source=new File(data.getData().getPath());
            ParcelFileDescriptor descriptor=null;
            try {
               descriptor =getApplicationContext().getContentResolver().openFileDescriptor(uri,"r", null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            File Destination=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/ReminderAndTodo/Reminders/"+directoryname+"/"+filename);
            Log.d("data",Destination.getAbsolutePath());
            String[] bits = filename.split("[.]");
            String extension = bits[bits.length-1];
            copy(descriptor,Destination);
            nofiles.setVisibility(View.GONE);

            files.setVisibility(View.VISIBLE);
            this.data.add(new set_file_data(filename, "."+extension, size,extension , getColor("."+extension), Destination.getAbsolutePath()));
            adapter.notifyDataSetChanged();

        }

    }
    private int getColor(String ext){
        String docs[]=getApplicationContext().getResources().getString(R.string.word).split(",");
        String ppt[]=getApplicationContext().getResources().getString(R.string.ppt).split(",");
        String excel[]=getApplicationContext().getResources().getString(R.string.excel).split(",");
        String audio[]=getApplicationContext().getResources().getString(R.string.audio).split(",");
        String video[]=getApplicationContext().getResources().getString(R.string.video).split(",");
        String images[]=getApplicationContext().getResources().getString(R.string.image).split(",");
        if(Arrays.asList(docs).contains(ext)){
            return getApplicationContext().getColor(R.color.docs);
        }
        if(Arrays.asList(ppt).contains(ext)){
            return getApplicationContext().getColor(R.color.ppts);
        }
        if(Arrays.asList(excel).contains(ext)){
            return getApplicationContext().getColor(R.color.excels);
        }
        if(Arrays.asList(audio).contains(ext)){
            return getApplicationContext().getColor(R.color.audio);
        }
        if(Arrays.asList(video).contains(ext)){
            return getApplicationContext().getColor(R.color.videos);
        }
        if(ext.equals(".pdf")){
            return getApplicationContext().getColor(R.color.pdf);
        }
        if(ext.equals(".txt")){
            return getApplicationContext().getColor(R.color.text);
        }
        if(Arrays.asList(images).contains(ext)){
            return getApplicationContext().getColor(R.color.images);
        }
        return getApplicationContext().getColor(R.color.others);
    }
    private void copy(ParcelFileDescriptor source, File destination) {

        FileChannel in = null;
        in = new FileInputStream(source.getFileDescriptor()).getChannel();
        FileChannel out = null;
        try {
            out = new FileOutputStream(destination).getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            in.transferTo(0, in.size(), out);
        } catch(Exception e){
            e.printStackTrace();
            // post to log
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode)
        {
            case 1:
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                }
                else
                {
                    closeNow();
                }
                break;
        }
    }
    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}