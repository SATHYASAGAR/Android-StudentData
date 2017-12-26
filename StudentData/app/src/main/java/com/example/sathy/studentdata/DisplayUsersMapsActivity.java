package com.example.sathy.studentdata;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class DisplayUsersMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    ArrayList<Address> addressList = new ArrayList<Address>();

    Double geoLatitudeDouble, serverLatitude;
    Double geoLongitudeDouble, serverLongitude;

    String url,country,state;
    String nicknameString;

    Integer countryZoomLevel=3;
    Integer stateZoomLevel=5;
    Integer worldZoomLevel=1;
    Integer mapZoomLevel=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_users_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle urlBundle = getIntent().getExtras();
        url=urlBundle.getString("url");
        country=urlBundle.getString("spinnerCountry");
        state=urlBundle.getString("spinnerState");
        geoLatitudeDouble=urlBundle.getDouble("latitudeDouble");
        geoLongitudeDouble=urlBundle.getDouble("longitudeDouble");

        if(country==null || country.length()==0){
            mapZoomLevel=worldZoomLevel;
        }
        else if(state==null || state.length()==0){
            mapZoomLevel=countryZoomLevel;
        }
        else{
            mapZoomLevel=stateZoomLevel;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                for(int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonobject= (JSONObject) response.get(i);
                        nicknameString=jsonobject.optString("nickname");
                        serverLatitude=jsonobject.optDouble("latitude");
                        serverLongitude=jsonobject.optDouble("longitude");
                        if(serverLatitude==0 && serverLongitude ==0){
                            new asyncTaskClass().execute(jsonobject);
                        }
                        else {
                            LatLng markerLocation = new LatLng(serverLatitude, serverLongitude);
                            mMap.addMarker(new MarkerOptions().position(markerLocation).title(nicknameString));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                LatLng movedMarkerLocation = new LatLng(geoLatitudeDouble,geoLongitudeDouble);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(movedMarkerLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(mapZoomLevel),2000,null);
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("rew", error.toString());
            }
        };

        JsonArrayRequest getRequest = new JsonArrayRequest( url, success, failure);
        VolleyQueue.instance(this).add(getRequest);
    }

    public class asyncTaskClass extends AsyncTask<JSONObject, Void ,LatLng>{
        String username;
        @Override
        protected LatLng doInBackground(JSONObject... params) {
            Double localServerLatitude=0.0, localServerLongitude=0.0;
            JSONObject locjsonobject = params[0];
            try {
                username = params[0].getString("nickname");
            }
            catch(Exception error){
                Log.i("rew","asyncTaskExceptionError: "+error);
            }
            LatLng returnLatng=new LatLng(0.0,0.0);
            try {
                Geocoder geocoder = new Geocoder(getBaseContext());
                addressList = (ArrayList<Address>) geocoder.getFromLocationName(locjsonobject.optString("state") + ", " + locjsonobject.optString("country"), 1);
                for (Address addressValue : addressList) {
                    localServerLatitude = addressValue.getLatitude();
                    localServerLongitude = addressValue.getLongitude();
                }
                returnLatng = new LatLng(localServerLatitude,localServerLongitude);
            } catch (IOException error) {
                Log.i("rew", "Geocoder IOException Error in Display Map" + error);
            }
            return returnLatng;
        }
        protected void onPostExecute(LatLng loc){
            mMap.addMarker(new MarkerOptions().position(loc).title(username));;
        }
    }
}
