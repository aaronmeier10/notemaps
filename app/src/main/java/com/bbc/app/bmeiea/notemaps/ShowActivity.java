package com.bbc.app.bmeiea.notemaps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ShowActivity extends AppCompatActivity {

    int fileid;

    @Override
    protected void onResume(){
        super.onResume();
        Intent intent = getIntent();
        fileid = Integer.parseInt(intent.getStringExtra("id"));
        String inhalt = read(fileid);
        String[] inhalte = inhalt.split(";");

        TextView titel = (TextView) findViewById(R.id.titel);
        TextView text = (TextView) findViewById(R.id.text);
        TextView zeit = (TextView) findViewById(R.id.zeittext);
        TextView ort = (TextView) findViewById(R.id.maptext);

        titel.setText(inhalte[0]);
        text.setText(inhalte[1]);
        if(inhalte[2] != "0"){
                String time = inhalte[2];
                long timestampLong = Long.parseLong(time);
                if(timestampLong != 0) {
                    Date d = new Date(timestampLong);
                    Calendar c = Calendar.getInstance();
                    c.setTime(d);
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH) + 1;
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
                    zeit.setText(getDate(timestampLong));
                }
        }
        if(inhalte[3].length() > 7){
            ort.setText(inhalte[3]);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show, menu);
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
            deleteFile(fileid);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private String getDate(long timeStamp){

        try{
            DateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }



    public void edit(View view){
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        Intent edit = new Intent(getApplicationContext(), EditActivity.class);
        edit.putExtra("id", id);
        startActivity(edit);
    }

    public void deleteFile(int filename){

        File dir = getFilesDir();
        File file = new File(dir, filename+".txt");
        boolean deleted = file.delete();
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



}
