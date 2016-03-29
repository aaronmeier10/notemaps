package com.bbc.app.bmeiea.notemaps;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import junit.framework.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

public class CreateActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private long timeInMilis;
    private float x = 0;
    private float y = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create, menu);
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
            Toast.makeText(getApplicationContext(), "Notiz verworfen", Toast.LENGTH_SHORT).show();
            finish();
        } else if (id == R.id.ok) {
            String titeltext ="";
            String texttext="";
            String zeittext="";
            String maptext="";

            EditText titel = (EditText) findViewById(R.id.titel);
            titeltext = titel.getText().toString();

            EditText text = (EditText) findViewById(R.id.text);
            texttext = text.getText().toString();

            TextView zeit = (TextView) findViewById(R.id.zeittext);
            zeittext = zeit.getText().toString();

            TextView map = (TextView) findViewById(R.id.maptext);
            maptext = map.getText().toString();


            if(titeltext == "" || titeltext == "Titel..."){
                Toast.makeText(getApplicationContext(), "Kein Titel", Toast.LENGTH_SHORT).show();
                return false;
            }

            if(texttext == "" || texttext == "Notiz..."){
                Toast.makeText(getApplicationContext(), "Kein Text", Toast.LENGTH_SHORT).show();
                return false;
            }

            if(timeInMilis != 0) {



                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
                notificationIntent.putExtra("id",countFiles()+1);
                notificationIntent.putExtra("titel",titeltext);
                notificationIntent.addCategory("android.intent.category.DEFAULT");
                PendingIntent broadcast = PendingIntent.getBroadcast(this, countFiles()+1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMilis, broadcast);

                Toast.makeText(getApplicationContext(), "Notiz wurde erstellt", Toast.LENGTH_SHORT).show();
            }
            write(titeltext, texttext, String.valueOf(timeInMilis), String.valueOf(x+","+y));

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void showTimePickerDialog(View v) {

        DialogFragment TimeFragement = new TimePickerFragment();
        TimeFragement.show(getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(){
        DialogFragment DateFragement = new DatePickerFragment();
        DateFragement.show(getFragmentManager(), "datePicker");
    }


    public void showMap(View v){
        Intent map = new Intent(getApplicationContext(),MapsActivity.class);
        startActivity(map);
    }


    public long timeStamp(int year,int month,int day,int hour, int minute){
        Calendar calendar = new GregorianCalendar(year, month, day);
        calendar.add(Calendar.HOUR, hour);
        calendar.add(Calendar.MINUTE, minute);
        long millis = calendar.getTimeInMillis();

        return millis;

    }




    public void write(String titel, String text, String zeit, String ort){

        //rename();


        String filename = String.valueOf(countFiles()+1)+".txt";
        String writetext = titel+";"+text+";"+zeit+";"+ort;
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, MODE_PRIVATE);
            outputStream.write(writetext.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

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






    Calendar c;


    public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
            c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, 1);

            TextView t = (TextView) findViewById(R.id.zeittext);
            //t.setText(hourOfDay + ":" + minute);
            showDatePickerDialog();

        }
    }

    private String getDate(long timeStamp){

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }



    public  class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            TextView t = (TextView) findViewById(R.id.zeittext);


            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);


            timeInMilis = c.getTimeInMillis();

            month++;
            //t.setText(t.getText() + ", " + day + "." + month + "." + year);

            Calendar c2 = Calendar.getInstance();
            c2.setTimeInMillis(timeInMilis);

            t.setText(getDate(timeInMilis));
        }
    }




    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Create Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.bbc.app.bmeiea.notemaps/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Create Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.bbc.app.bmeiea.notemaps/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}

