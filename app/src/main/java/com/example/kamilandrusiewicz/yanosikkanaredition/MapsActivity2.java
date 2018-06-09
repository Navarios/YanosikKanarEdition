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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kamilandrusiewicz.yanosikkanaredition.Models.PlanModel;
import com.example.kamilandrusiewicz.yanosikkanaredition.Models.StopModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private GoogleMap mMap;
    MarkerOptions mo;
    Marker marker;
    LocationManager locationManager;
    private List<StopModel> stopModelList;
    private MarkerOptions options = new MarkerOptions();
    boolean isLoaded=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        WebView myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl("https://www.zditm.szczecin.pl/img/zditmlogo.svg");
        new JSONTask().execute("https://www.zditm.szczecin.pl/json/slupki.inc.php");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mo = new MarkerOptions().position(new LatLng(53.438056, 14.542222)).title("Twoja lokalizacja").icon(BitmapDescriptorFactory.fromResource(R.drawable.location_person_black_3_48dp)).zIndex(-1);
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
        if(isLoaded==false) {
            mMap.clear();
            marker = mMap.addMarker(mo);
        }
        LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
        marker.setPosition(myCoordinates);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myCoordinates, 17));

        if (stopModelList != null && isLoaded==false) {

            for (int i = 0; i < stopModelList.size(); i++) {
                options.position(new LatLng(Double.parseDouble(stopModelList.get(i).getLat()), Double.parseDouble(stopModelList.get(i).getLon())));
                options.title("Przystanek: " + stopModelList.get(i).getNazwa());
                options.snippet("Nr słupka: " + stopModelList.get(i).getNrslupka() + " | Nr zespolu: " + stopModelList.get(i).getNrzespolu() + " | ID: " + stopModelList.get(i).getId());
                //options.snippet("https://www.zditm.szczecin.pl/pasazer/rozklady-jazdy,tablica,"+stopModelList.get(i).getId());
                options.zIndex(Float.parseFloat(stopModelList.get(i).getId()));
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop_black_29dp));

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        WebView myWebView = (WebView) findViewById(R.id.webview);
                        WebSettings webSettings = myWebView.getSettings();
                        webSettings.setJavaScriptEnabled(true);
                        if(marker.getZIndex()>0)
                            myWebView.loadUrl("https://www.zditm.szczecin.pl/pasazer/rozklady-jazdy,tablica,"+Math.round(marker.getZIndex()));
                        else
                            myWebView.loadUrl("https://www.zditm.szczecin.pl/img/zditmlogo.svg");
                        return false;
                    }
                });
                mMap.addMarker(options);
            }
            isLoaded=true;
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

    public void relocateGPS(View view)  {
        requestLocation();
    }

    public class JSONTask extends AsyncTask<String, String, List<StopModel>> {

        @Override
        protected List<StopModel> doInBackground(String... params) {
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

                stopModelList = new ArrayList<>();

                StringBuffer finalBufferedData = new StringBuffer();


                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    StopModel stopModel = new StopModel();
                    stopModel.setId(finalObject.getString("id"));
                    stopModel.setLat(finalObject.getString("szerokoscgeo"));
                    stopModel.setLon(finalObject.getString("dlugoscgeo"));
                    stopModel.setNazwa(finalObject.getString("nazwa"));
                    stopModel.setNrzespolu(finalObject.getString("nrzespolu"));
                    stopModel.setNrslupka(finalObject.getString("nrslupka"));

                    stopModelList.add(stopModel);

                }

                return stopModelList;


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
        protected void onPostExecute(List<StopModel> result) {
            super.onPostExecute(result);
        }
    }




}
