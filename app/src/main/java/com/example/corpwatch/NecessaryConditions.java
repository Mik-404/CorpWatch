package com.example.corpwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class NecessaryConditions extends AppCompatActivity {
    public boolean FirstCondition = false, SecondCondition = false;
    byte[] inputData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_necessary_conditions);
        SpannableString ss = new SpannableString("Вам необходимо активировать Telegram бота. Для этого необходимо отправить ему любой текст. Затем прикрепите фото вашей подписи.");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/andry_admin_bot"));
                startActivity(browserIntent);
                SendStat();
            }
        };
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                DarkMagic();
            }
        };
        ss.setSpan(clickableSpan, 28, 41, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan2, 97, 112, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView textView = (TextView) findViewById(R.id.infotxt);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(getResources().getColor(R.color.bluedark));
    }

    public void  DarkMagic () {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageIntent.setType("image/*");
        pickImageIntent.putExtra("aspectX", 1);
        pickImageIntent.putExtra("aspectY", 1);
        pickImageIntent.putExtra("scale", true);
        pickImageIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(pickImageIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                inputData = baos.toByteArray();

            } catch (IOException e) {
                e.printStackTrace();
            }
            AsyncUploader uploadFileToServer = new AsyncUploader();
            uploadFileToServer.execute();
        }
    }

    private void SendStat() {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://213.226.126.69/info.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        FirstCondition = true;
                        Toast.makeText(getApplicationContext(), "Успешно", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("id", getValue());
                params.put("type", "bot");
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public String getValue(){
        SharedPreferences preferences = this.getSharedPreferences("settings", this.MODE_PRIVATE);
        return preferences.getString("id", "");
    }

    public void OnClick (View v) {
        if (FirstCondition && SecondCondition) {
            Intent intent = new Intent(this, MainScreen.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Вам необходимо написать Telegram боту и прикрепить фото подписи", Toast.LENGTH_LONG).show();
        }
    }

    public void resultImageUploading(String resp) {
        System.out.println(resp);
        if (resp.equals("OK")) {
            SecondCondition = true;
            Toast.makeText(this, "Фото успешно добавлено", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Произошла ошибка при добавлении, попробуйте ещё раз", Toast.LENGTH_LONG).show();
        }
    }

    private class AsyncUploader extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return uploadFile();
        }
        @Override
        protected void  onPostExecute(String result) {
            resultImageUploading (result);
        }


        private String uploadFile() {
            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("id", getValue())
                        .addFormDataPart("file", "file.png",
                                RequestBody.create( MediaType.parse("image/*"),inputData))
                        .build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("http://213.226.126.69:5000/image-load")
                        .post(requestBody)
                        .build();

                Call call = client.newCall(request);
                okhttp3.Response response = call.execute();
                String response_text = response.body().string();
                System.out.println(response_text);
                response.body().close();
                return response_text;
            } catch (Exception e) {
                System.out.println(e + " ! " + e.getMessage());
                return "ERROR";
            }
        }
    }
}