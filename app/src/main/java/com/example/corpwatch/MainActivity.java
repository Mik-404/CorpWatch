package com.example.corpwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.spec.ECField;
import java.util.Hashtable;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

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
        AsyncUploader uploadFileToServer = new AsyncUploader();
        uploadFileToServer.execute(usernameText, passwordText);
        login.setText("");
        password.setText("");
    }

    public void resultAuthentification (String response){
        Intent intent1 = new Intent(this, NecessaryConditions.class);
        Intent intent2 = new Intent(this, MainScreen.class);
        if (response.equals("400")) {
            Toast.makeText(getApplicationContext(), "Неправильный логин или пароль", Toast.LENGTH_LONG).show();
        } else {
            try {
                JSONArray resultObjects = new JSONArray(response.toString());
                setValue(String.valueOf(resultObjects.getInt(2)));
                if (resultObjects.getInt(4) == 1 && resultObjects.getInt(3) == 1) {
                    startActivity(intent2);
                } else {
                    startActivity(intent1);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void setValue(String value){
        SharedPreferences preferences = this.getSharedPreferences("settings", this.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id", value);
        editor.apply();
    }

    public void OnClickAdmin (View v){
        EditText login = (EditText) findViewById(R.id.editTextText);
        EditText password = (EditText) findViewById(R.id.editTextText2);
        final String usernameText = login.getText().toString().trim();
        final String passwordText = password.getText().toString().trim();
        Intent intent = new Intent(this, MainAdminPanel.class);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://213.226.126.69/login_admin.php",
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
                System.out.println(usernameText + " " + passwordText);
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

    private class AsyncUploader extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return uploadFile(params[0], params[1]);
        }
        @Override
        protected void  onPostExecute(String result) {
            resultAuthentification (result);
        }
        private String uploadFile(String login, String password) {
            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("login", login)
                        .addFormDataPart("password", password)
                        .build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("http://213.226.126.69:5000/login")
                        .post(requestBody)
                        .build();

                Call call = client.newCall(request);
                okhttp3.Response response = call.execute();
                int status_code = response.code();
                String response_text = response.body().string();
                response.body().close();
                System.out.println(status_code);
                return response_text;
            } catch (Exception e) {
                System.out.println(e + " ! " + e.getMessage());
                return "0";
            }
        }
    }
}