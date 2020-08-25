package com.example.solenteatout;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.MenuInflater;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.location.LocationManager;
import android.location.LocationListener;
import android.location.Location;
import android.content.Context;
import android.widget.Toast;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import android.widget.CheckBox;
import android.os.Environment;
import android.app.AlertDialog;

public class MainActivity extends AppCompatActivity implements LocationListener {

    //global variables created to use through out; items: list of markers on the map
    MapView mv;
    Location loc;
    ItemizedIconOverlay<OverlayItem> items;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        setContentView(R.layout.activity_main);

        mv = findViewById(R.id.map1);

        mv.setMultiTouchControls(true);
        mv.getController().setZoom(16.0);
        mv.getController().setCenter(new GeoPoint(51.05, -0.72));
        LocationManager mgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        items = new ItemizedIconOverlay<OverlayItem>(this, new ArrayList<OverlayItem>(), null);
        mv.getOverlays().add(items);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //runs when an item is selected from the menu
        int id = item.getItemId();

        if (id == R.id.add_a_restaurant) {
            Intent intent = new Intent(this, com.example.solenteatout.AddARestaurantActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }
        if (id == R.id.preferences) {
            Intent intent = new Intent(this, PrefsActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }

//reading from items and saving each marker to csv file
        if (id == R.id.save_all_restaurants) {
            //
            for (OverlayItem marker : items.getDisplayedItems()) {
                //OverlayItem: DataType, items: list
                //items is the list looping through. each time i loop restaurant is the next marker in the list.
                try {
                    GeoPoint markerGP = (GeoPoint) marker.getPoint();
                    double lat = markerGP.getLatitude();
                    double lng = markerGP.getLongitude();

                    String strLat = String.valueOf(lat);
                    String strLng = String.valueOf(lng);
                    String restaurantDetails = marker.getTitle() + "," + marker.getSnippet() + "," + strLat + "," + strLng;
                    //So that in a csv file it'll be in one line separated by commas
                    PrintWriter pw = new PrintWriter(new FileWriter("data.csv"));
                    //creating a csv file to be written to
                    pw.println(restaurantDetails);
                    pw.close();
                } catch (IOException e) {
                    System.out.println("I/O Error: " + e);
                }
            }
            return true;
        }
        if (id == R.id.load_all_restaurants) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader("data.csv"));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] components = line.split(",");
                    if (components.length == 6) {
                        double lat = Double.parseDouble(components[4]);
                        double lon = Double.parseDouble(components[5]);
                        GeoPoint geoPoint = new GeoPoint(lat, lon);
                        String restaurantDescription = components[1] + "," + components[2] + "," + components[3];
                        OverlayItem restaurantMarker = new OverlayItem(components[0], restaurantDescription, geoPoint);
                        items.addItem(restaurantMarker);
                    }
                }
            } catch (IOException e) {
                System.out.println("ERROR: " + e);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean prefSaveAll = prefs.getBoolean("save_all", true);
//
        if (prefSaveAll == true) {
            //
            for (OverlayItem marker : items.getDisplayedItems()) {
                //OverlayItem: DataType, items: list
                //items is the list looping through. each time i loop restaurant is the next marker in the list.
                try {
                    GeoPoint markerGP = (GeoPoint) marker.getPoint();
                    double lat = markerGP.getLatitude();
                    double lng = markerGP.getLongitude();

                    String strLat = String.valueOf(lat);
                    String strLng = String.valueOf(lng);
                    String restaurantDetails = marker.getTitle() + "," + marker.getSnippet() + "," + strLat + "," + strLng;
                    //So that in a csv file it'll be in one line separated by commas
                    PrintWriter pw = new PrintWriter(new FileWriter("data.csv"));
                    //creating a csv file to be written to
                    pw.println(restaurantDetails);
                    pw.close();
                } catch (IOException e) {
                    System.out.println("I/O Error: " + e);
                }
            }
        }
    }

    public void onLocationChanged(Location newLoc) {
        //setting the center of the map to given lat and lon co-ordinates
        Toast.makeText
                (this, "Location=" +
                        newLoc.getLatitude() + "" +
                        newLoc.getLongitude(), Toast.LENGTH_LONG).show();
        mv.setMultiTouchControls(true);
        mv.getController().setCenter(new GeoPoint(newLoc.getLatitude(), newLoc.getLongitude()));
        loc = newLoc;
    }

    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Provider" + provider +
                "enabled", Toast.LENGTH_LONG).show();
    }

    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Provider" + provider +
                "enabled", Toast.LENGTH_LONG).show();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(this, "Status changed: " + status,
                Toast.LENGTH_LONG).show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        //menu inflater; menu to be displayed
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //Reading data from second  activity
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //converting and saving users location in a geoPoint using "loc"
                GeoPoint geoPoint = new GeoPoint(loc);
                Bundle extras = intent.getExtras();
                //
                String restaurantName = extras.getString("com.example.solenteatout.restaurantName");
                String restaurantAddress = extras.getString("com.example.solenteatout.restaurantAddress");
                String cuisine = extras.getString("com.example.solenteatout.cuisine");
                String rating = extras.getString("com.example.solenteatout.rating");
                String restaurantDescription = restaurantAddress + "," + cuisine + "," + rating;
                //combining 3 values into one string, to be used in "description" section of next line (title, description, location)
                OverlayItem restaurantMarker = new OverlayItem(restaurantName, restaurantDescription, geoPoint);
                items.addItem(restaurantMarker);
            }

        }
    }

}
