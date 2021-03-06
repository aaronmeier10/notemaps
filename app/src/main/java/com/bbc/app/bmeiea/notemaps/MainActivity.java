package com.bbc.app.bmeiea.notemaps;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        ArrayList<Note> notes = new ArrayList<Note>();

    @Override
    protected void onResume(){
        super.onResume();
        setAllNote();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent create = new Intent(getApplicationContext(), CreateActivity.class);
                startActivity(create);
            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.delete) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_note) {
            // Handle the camera action
        } else if (id == R.id.nav_map) {
            Intent maps = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(maps);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public String read(int file) {

        String ret = "";

        try {
            InputStream inputStream = openFileInput(file+".txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }

    public void setAllNote(){
        ArrayList<String> notearray = new ArrayList<String>();
        rename();
        int counter = 1;
        int countfiles = countFiles();
        notearray = new ArrayList<>();

        if(countfiles>0){
            while(countfiles>=counter){
                String[] text = read(counter).split(";");
                String[] ort = text[3].split(",");
                notes.add(counter - 1, new Note(text[0], text[1], Long.parseLong(text[2]), Float.parseFloat(ort[0]), Float.parseFloat(ort[1])));

                notearray.add(counter-1,text[0]);



                //Alarm deleten
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
                notificationIntent.addCategory("android.intent.category.DEFAULT");
                PendingIntent broadcast = PendingIntent.getBroadcast(this,counter, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                try {
                    alarmManager.cancel(broadcast);
                } catch (Exception e) {
                    Log.e("ALARM...", "AlarmManager update was not canceled. " + e.toString());
                }



                //Alarm neu setzen
                Long tsLong = System.currentTimeMillis();



                if(Long.parseLong(text[2]) != 0 && Long.parseLong(text[2])>tsLong) {
                    AlarmManager newalarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent newnotificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
                    newnotificationIntent.putExtra("id", counter);
                    newnotificationIntent.putExtra("titel", text[0]);
                    newnotificationIntent.addCategory("android.intent.category.DEFAULT");
                    PendingIntent newbroadcast = PendingIntent.getBroadcast(this, counter, newnotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    newalarmManager.setExact(AlarmManager.RTC_WAKEUP, Long.parseLong(text[2]), newbroadcast);
                }

                counter++;
            }
        }else{
            notearray.add(0, "Keine Notizen");
            return;
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, notearray);

        ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent show = new Intent(getApplicationContext(), ShowActivity.class);
                show.putExtra("id",String.valueOf(position+1));
                startActivity(show);
            }
        });


    }



    public int countFiles(){
        String path = getFilesDir().toString();
        File file=new File(path);
        File[] list = file.listFiles();
        int count = 0;
        for (File f: list){
            String name = f.getName();
            if (name.endsWith(".txt"))
                count++;
        }

        return count;
    }


    public void write(String inhalt, int file){

        String filename = String.valueOf(file+".txt");
        String writetext = inhalt;
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, MODE_PRIVATE);
            outputStream.write(writetext.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void rename(){

        int count = countFiles();
        if(count>0){

        }else{
            return;
        }
        int idcounter = 1;
        int filecounter = 1;

        while(count >= idcounter){
            File file = new File(getFilesDir()+"/"+filecounter+".txt");
            if(file.exists()){
                if(idcounter != filecounter){
                    //File file2 = new File(idcounter+".txt");
                    //boolean success = file.renameTo(file2);
                    String inhalt = read(filecounter);
                    deleteFile(filecounter);
                    write(inhalt, idcounter);
                }
                filecounter++;
                idcounter++;
            }else{
                filecounter++;
            }
        }
    }


    public void deleteFile(int filename){

        File dir = getFilesDir();
        File file = new File(dir, filename+".txt");
        boolean deleted = file.delete();
    }



    public void listFiles(){
        String path = String.valueOf(getFilesDir());
        Log.d("Files", "Path: " + path);
        File f = new File(path);
        File file[] = f.listFiles();
        Log.d("Files", "Size: "+ file.length);
        for (int i=0; i < file.length; i++)
        {
            Log.d("Files", "FileName:" + file[i].getName());
        }
    }




}

