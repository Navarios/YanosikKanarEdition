package com.example.kamilandrusiewicz.yanosikkanaredition;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.kamilandrusiewicz.yanosikkanaredition.Models.PlanModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private GoogleMap mMap;
    MarkerOptions mo;
    Marker marker;
    LocationManager locationManager;
    private List<PlanModel> planModelList;
    private MarkerOptions options = new MarkerOptions();
    private EditText searchLine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        searchLine=(EditText)findViewById(R.id.searchLine2);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mo = new MarkerOptions().position(new LatLng(53.438056, 14.542222)).title("Twoja lokalizacja");
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        } else requestLocation();
        if (!isLocationEnabled())
            showAlert(1);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


    }


    @Override
    public void onLocationChanged(Location location) {
        new JSONTask().execute("https://www.zditm.szczecin.pl/json/pojazdy.inc.php");
        mMap.clear();
        LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
        marker = mMap.addMarker(mo);
        marker.setPosition(myCoordinates);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myCoordinates, 15));




//        PlanModel planModel = new PlanModel();
//        planModelList = new ArrayList<>();
//        planModel.setLat("53");
//        planModel.setLon("14");
//        planModel.setLinia("12");
//        planModelList.add(planModel);
//        PlanModel planMode2 = new PlanModel();
//        planMode2.setLat("53.1");
//        planMode2.setLon("14.1");
//        planMode2.setLinia("13");
//        planModelList.add(planMode2);

        if(planModelList!=null) {

            for (int i = 0; i < planModelList.size(); i++) {
                options.position(new LatLng(Double.parseDouble(planModelList.get(i).getLat()), Double.parseDouble(planModelList.get(i).getLon())));
                options.title("Trasa: "+planModelList.get(i).getLinia());
                options.snippet("Z: "+ planModelList.get(i).getZ()+ " do: "+ planModelList.get(i).getD()+" punktualnie: " +planModelList.get(i).getPunktualnosc1());
                mMap.addMarker(options);
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 5000, 10, this);
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isPermissionGranted() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED || checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("mylog", "Permission is granted");
            return true;
        } else {
            Log.v("mylog", "Permission not granted");
            return false;
        }
    }

    private void showAlert(final int status) {
        String message, title, btnText;
        if (status == 1) {
            message = "Twoja lokzalicja jest wyłączona.\nProszę ją uruchomić, " +
                    "aby używać tej aplikacji";
            title = "Uruchom lokalizację";
            btnText = "Ustawienia lokzalicacji";
        } else {
            message = "Prosze o pozwolenie dostępu do lokalizacji!";
            title = "Pozwolenie przyznane";
            btnText = "Pozwól";
        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        if (status == 1) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                        } else
                            requestPermissions(PERMISSIONS, PERMISSION_ALL);
                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }

    public void relocateGPS(View view) {
        mMap.clear();
        requestLocation();
    }

    public class JSONTask extends AsyncTask<String, String, List<PlanModel>> {

        @Override
        protected List<PlanModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONArray parentArray = new JSONArray(finalJson);

                planModelList = new ArrayList<>();

                StringBuffer finalBufferedData = new StringBuffer();


                    for (int i = 0; i < parentArray.length(); i++) {
                        if(searchLine.getText().toString().length()==0) {
                            JSONObject finalObject = parentArray.getJSONObject(i);
                            PlanModel planModel = new PlanModel();
                            planModel.setLinia(finalObject.getString("linia"));
                            planModel.setLat(finalObject.getString("lat"));
                            planModel.setLon(finalObject.getString("lon"));
                            planModel.setZ(finalObject.getString("z"));
                            planModel.setD(finalObject.getString("do"));
                            planModel.setPunktualnosc1(finalObject.getString("punktualnosc1"));

                            planModelList.add(planModel);
                        }
                        else {
                            JSONObject finalObject = parentArray.getJSONObject(i);
                            PlanModel planModel = new PlanModel();
                            if(searchLine.getText().toString().equals(finalObject.getString("linia")))
                            {
                                planModel.setLinia(finalObject.getString("linia"));
                                planModel.setLat(finalObject.getString("lat"));
                                planModel.setLon(finalObject.getString("lon"));
                                planModel.setZ(finalObject.getString("z"));
                                planModel.setD(finalObject.getString("do"));
                                planModel.setPunktualnosc1(finalObject.getString("punktualnosc1"));
                            }
                        }

                    }


                return planModelList;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<PlanModel> result) {
            super.onPostExecute(result);
        }
    }


}
