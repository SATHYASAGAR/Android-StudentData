package com.example.sathy.studentdata;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity {

    ArrayList<Address> addressList = new ArrayList<Address>();
    ArrayList<String> spinnerYears = new ArrayList<String>();
    String spinnerYear;
    ArrayList<String> countrySpinnerList;
    String spinnerCountry;
    ArrayList<String> stateSpinnerList;
    String spinnerState;

    Spinner stateSpinner;

    Button userListButton;
    Button userMapButton;
    Button filterBackButton;

    String baseUrl="http://bismarck.sdsu.edu/hometown/users?reverse=true&";
    String url=baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        countrySpinnerList=new ArrayList<String>();
        countrySpinnerList.add("");
        stateSpinnerList=new ArrayList<String>();

        stateSpinner = (Spinner) findViewById(R.id.filterSpinnerState);

        filterBackButton = (Button) this.findViewById(R.id.filterBackButton);
        userListButton = (Button)this.findViewById(R.id.userListViewButton);
        userMapButton = (Button) this.findViewById(R.id.userMapViewButton);

        StringRequest request = new StringRequest(Request.Method.GET, "http://bismarck.sdsu.edu/hometown/countries",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        response=response.replace("[","").replace("]","").replace("\"","");
                        String[] arrTmp = response.split(",");
                        for(int i=0;i<arrTmp.length;i++){
                            countrySpinnerList.add(arrTmp[i]);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, countrySpinnerList);
                        Spinner countrySpinner = (Spinner) findViewById(R.id.filterSpinnerCountry);
                        countrySpinner.setAdapter(adapter);
                        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapter, View v,int position, long id) {
                                spinnerCountry = adapter.getItemAtPosition(position).toString();
                                generateUrl();
                                if(spinnerCountry.length()==0 || spinnerCountry==null){
                                    stateSpinnerList.clear();
                                    stateSpinnerList.add("");
                                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, stateSpinnerList);
                                    stateSpinner.setAdapter(adapter1);
                                    spinnerState="";
                                    generateUrl();
                                }
                                else {

                                    StringRequest request = new StringRequest(Request.Method.GET, "http://bismarck.sdsu.edu/hometown/states?country=" + spinnerCountry,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    response = response.replace("[", "").replace("]", "").replace("\"", "");
                                                    String[] arrTmp = response.split(",");
                                                    stateSpinnerList.clear();
                                                    stateSpinnerList.add("");
                                                    for (int i = 0; i < arrTmp.length; i++) {
                                                        stateSpinnerList.add(arrTmp[i]);
                                                    }
                                                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, stateSpinnerList);
                                                    stateSpinner.setAdapter(adapter1);
                                                    stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                                                            spinnerState = adapter.getItemAtPosition(position).toString().replace(" ", "%20");
                                                            generateUrl();
                                                        }
                                                        @Override
                                                        public void onNothingSelected(AdapterView<?> arg0) {

                                                        }
                                                    });
                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    });

                                    VolleyQueue.instance(getBaseContext()).add(request);
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {

                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        VolleyQueue.instance(this).add(request);

        spinnerYears.add("");
        for (int i = 1970; i <= 2017; i++) {
            spinnerYears.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerYears);
        Spinner spinYear = (Spinner) findViewById(R.id.filterSpinnerYear);
        spinYear.setAdapter(adapter);
        spinYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,int position, long id) {
                spinnerYear = adapter.getItemAtPosition(position).toString();
                generateUrl();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        filterBackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });

        userListButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                displayUserListView();
            }
        });

        userMapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                displayUserMapView();
            }
        });

    }

    public void generateUrl(){
        Integer isCountrySelected;
        Integer isStateSelected;
        Integer isYearSelected;

        if(spinnerCountry==null){
            isCountrySelected=0;
        }
        else if(spinnerCountry.length()==0){
            isCountrySelected=0;
        }
        else{
            isCountrySelected=1;
        }

        if(spinnerState==null){
            isStateSelected=0;
        }
        else if(spinnerState.length()==0){
            isStateSelected=0;
        }
        else{
            isStateSelected=1;
        }

        if(spinnerYear==null){
            isYearSelected=0;
        }
        else if(spinnerYear.length()==0){
            isYearSelected=0;
        }
        else{
            isYearSelected=1;
        }

        if(isCountrySelected==0 && isStateSelected ==0 && isYearSelected ==0){
            url=baseUrl;
        }
        if(isCountrySelected==0 && isStateSelected ==0 && isYearSelected ==1){
            url=baseUrl+"year="+spinnerYear;
        }
        if(isCountrySelected==0 && isStateSelected ==1 && isYearSelected ==0){
            url=baseUrl+"state="+spinnerState;
        }
        if(isCountrySelected==0 && isStateSelected ==1 && isYearSelected ==1){
            url=baseUrl+"year="+spinnerYear+"&state="+spinnerState;
        }
        if(isCountrySelected==1 && isStateSelected ==0 && isYearSelected ==0){
            url=baseUrl+"country="+spinnerCountry;
        }
        if(isCountrySelected==1 && isStateSelected ==0 && isYearSelected ==1){
            url=baseUrl+"year="+spinnerYear+"&country="+spinnerCountry;
        }
        if(isCountrySelected==1 && isStateSelected ==1 && isYearSelected ==0){
            url=baseUrl+"state="+spinnerState+"&country="+spinnerCountry;
        }
        if(isCountrySelected==1 && isStateSelected ==1 && isYearSelected ==1){
            url=baseUrl+"year="+spinnerYear+"&state="+spinnerState+"&country="+spinnerCountry;
        }
    }

    public void displayUserListView(){
        Intent go = new Intent(this,UserListActivity.class);
        go.putExtra("url",url);
        startActivity(go);
    }

    public void displayUserMapView(){
        Double latitudeDouble=0.0,longitudeDouble=0.0;
        if((spinnerState==null || spinnerState.length()==0)&&(spinnerCountry==null || spinnerCountry.length()==0)){
            latitudeDouble=0.0; longitudeDouble=0.0;
        }
        else{
            try {
                Geocoder geocoder = new Geocoder(this);
                addressList = (ArrayList<Address>) geocoder.getFromLocationName(spinnerState + ", " + spinnerCountry, 1);
                for (Address addressValue : addressList) {
                    latitudeDouble = addressValue.getLatitude();
                    longitudeDouble = addressValue.getLongitude();
                }
            } catch (IOException error) {
                Log.i("rew", "IOException Error" + error);
            }
        }

        Intent go = new Intent(this,DisplayUsersMapsActivity.class);
        go.putExtra("url",url);
        go.putExtra("spinnerCountry",spinnerCountry);
        go.putExtra("spinnerState",spinnerState);
        go.putExtra("latitudeDouble",latitudeDouble);
        go.putExtra("longitudeDouble",longitudeDouble);
        startActivity(go);
    }
}
