package com.example.sathy.studentdata;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    ArrayAdapter<String> itemsAdapter;
    ArrayList<String>studentDetails=new ArrayList<String>();
    ListView userListView;
    Button userListBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        userListView=(ListView)this.findViewById(R.id.userList);
        userListBack=(Button)this.findViewById(R.id.userListBackButton);

        Bundle urlBundle = getIntent().getExtras();
        String url = urlBundle.getString("url");

        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                for(int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonobject= (JSONObject) response.get(i);
                        String nickname=jsonobject.optString("nickname");
                        studentDetails.add("NickName: "+" "+nickname+"\nCountry: "+jsonobject.optString("country")+"\nState: "+jsonobject.optString("state")+"\nCity: "+jsonobject.optString("city"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                itemsAdapter =new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1,studentDetails);
                userListView.setAdapter(itemsAdapter);
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("rew", error.toString());
            }
        };

        JsonArrayRequest getRequest = new JsonArrayRequest( url, success, failure);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(getRequest);

        userListBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }
}

