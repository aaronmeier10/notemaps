package com.bbc.app.bmeiea.notemaps;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    ArrayList<Note> notes = new ArrayList<Note>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        setAllNote();;
        for (int i = 0; notes.size()< i; i++) {
            mMap = googleMap;

            LatLng marker = new LatLng(notes.get(i).x, notes.get(i).y);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 13));
            mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                    //.anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                            .position(marker)
                            .title(notes.get(i).titel)
                    //.snippet("Thuner")
                    //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            );
        }
    }

    public void setAllNote(){
        ArrayList<String> notearray = new ArrayList<String>();
        int counter = 1;
        int countfiles = countFiles();
        notearray = new ArrayList<>();
        if(countfiles>0){
            while(countfiles>=counter){
                String[] text = read(counter).split(";");
                String[] ort = text[3].split(",");
                notes.add(counter - 1, new Note(text[0], text[1], Long.parseLong(text[2]), Float.parseFloat(ort[0]), Float.parseFloat(ort[1])));

                notearray.add(counter-1,text[0]);
                counter++;
            }
        }else{
            notearray.add(0, "Keine Notizen");
            return;
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

}
