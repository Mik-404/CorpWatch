package com.example.corpwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.Hashtable;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    public void OnClickUser (View v) {
        EditText login = (EditText) findViewById(R.id.editTextText);
        EditText password = (EditText) findViewById(R.id.editTextText2);
        final String usernameText = login.getText().toString().trim();
        final String passwordText = password.getText().toString().trim();
        Intent intent = new Intent(this, MainScreen.class);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://213.226.126.69/login_user.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        if (response.equals("200")) {
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Неправильный логин или пароль", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() == null) {
                    System.out.println(1212);
                } else {
                    System.out.println(error.getMessage());
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("login", usernameText);
                params.put("password", passwordText);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
        login.setText("");
        password.setText("");

    }

    public void OnClickAdmin (View v){
        EditText login = (EditText) findViewById(R.id.editTextText);
        EditText password = (EditText) findViewById(R.id.editTextText2);

    }
}