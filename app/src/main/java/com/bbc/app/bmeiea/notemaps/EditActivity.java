package com.bbc.app.bmeiea.notemaps;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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

public class EditActivity extends AppCompatActivity {
    int fileid;
    long timeInMilis;
    float x = 0;
    float y = 0;


    int cyear;
    int cmonth;
    int cday;
    int chour;
    int cminute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
    }

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
            Date d = new Date(timestampLong);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cyear = cal.get(Calendar.YEAR);
            cmonth = cal.get(Calendar.MONTH)+1;
            cday = cal.get(Calendar.DAY_OF_MONTH);
            chour = cal.get(Calendar.HOUR_OF_DAY);
            cminute = cal.get(Calendar.MINUTE);
            c = cal;
            timeInMilis = c.getTimeInMillis();
            zeit.setText(getDate(timestampLong));
        }
        if(inhalte[3] != "0,0"){
            ort.setText(inhalte[3]);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
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
            EditText titel = (EditText) findViewById(R.id.titel);
            String titeltext = titel.getText().toString();

            EditText text = (EditText) findViewById(R.id.text);
            String texttext = text.getText().toString();

            TextView zeit = (TextView) findViewById(R.id.zeittext);
            String zeittext = zeit.getText().toString();

            TextView map = (TextView) findViewById(R.id.maptext);
            String maptext = map.getText().toString();

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
                notificationIntent.addCategory("android.intent.category.DEFAULT");
                PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMilis, broadcast);

                Toast.makeText(getApplicationContext(), "Notiz wurde gespeichert", Toast.LENGTH_SHORT).show();
            }

            deleteFile(fileid);
            write(fileid,titeltext, texttext, String.valueOf(timeInMilis), String.valueOf(x+","+y));

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

    public void deleteFile(int filename){

        File dir = getFilesDir();
        File file = new File(dir, filename+".txt");
        boolean deleted = file.delete();
    }





    public void write(int filenum, String titel, String text, String zeit, String ort){



        String filename = String.valueOf(filenum+".txt");
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
            int hour = chour;
            int minute = cminute;

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


    public  class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = cyear;
            int month = cmonth-1;
            int day = cday;

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
