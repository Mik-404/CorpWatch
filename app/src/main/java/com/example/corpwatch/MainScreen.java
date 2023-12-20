package com.example.corpwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainScreen extends AppCompatActivity {
    int height_f = 3;
    View f = null;
    RelativeLayout layout;
    Button bt1, bt2, bt3;
    float ActiveTxt = 18f;
    float BaseTxt = 17;
    HashMap<String, byte[]> inputData;
    Files active;
    TextView ex1tsc;
    ImageButton ex1bsc;
    LinearLayout ex1lsc;
    Files [] resultObjects;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        layout = (RelativeLayout)findViewById(R.id.rel);
        bt1 = findViewById(R.id.button4);
        bt2 = findViewById(R.id.button5);
        bt3 = findViewById(R.id.button6);
        ex1tsc = findViewById(R.id.textView5);
        ex1bsc = findViewById(R.id.imageButton);
        ex1lsc = findViewById(R.id.example1_scroll);
        inputData=new HashMap<>();
        lineUlt(1);
        getHistory();
        System.out.println(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
    }
    public void getHistory () {
        final StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://213.226.126.69/hist.php",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        System.out.println(response);
                        LinearLayout scv = findViewById(R.id.scvcd1);
                        ArrayList<View> childs = new ArrayList<View>();
                        for (int i = 0; i < scv.getChildCount(); i++) {
                            if (scv.getChildAt(i).getId() != R.id.example1_scroll){
                                childs.add (scv.getChildAt(i));
                            }
                        }
                        for (View v:childs) {
                            scv.removeView(v);
                        }
                        if (response.equals("400")) {
                            TextView elem = new TextView(getApplicationContext());
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            elem.setLayoutParams(params);
                            elem.setTextColor(getResources().getColor(R.color.black));
                            elem.setTextAppearance(android.R.style.TextAppearance_Material_Body2);
                            elem.setTextSize(TypedValue.COMPLEX_UNIT_SP, ActiveTxt);
                            elem.setGravity(Gravity.CENTER);
                            elem.setText("Ничего не найдено");
                            scv.addView(elem);
                        } else {
                            resultObjects = new Gson().fromJson(response.toString(), Files[].class);
                            int i = 0;
                            for (Files f : resultObjects) {
                                System.out.println(f.name_file);
                                TextView elem = new TextView(getApplicationContext());
                                elem.setLayoutParams(ex1tsc.getLayoutParams());

                                ImageButton elem2 = new ImageButton((getApplicationContext()));
                                elem2.setLayoutParams(ex1bsc.getLayoutParams());

                                LinearLayout elem3 = new LinearLayout((getApplicationContext()));
                                elem3.setLayoutParams(ex1lsc.getLayoutParams());

                                elem.setTextColor(getResources().getColor(R.color.black));
                                elem.setTextAppearance(android.R.style.TextAppearance_Material_Body2);
                                elem.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                                elem.setGravity(Gravity.CENTER);
                                elem.setText(f.name_file);

                                elem2.setPadding(20,0,0,0);
                                elem2.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                                elem2.setScaleType(ImageView.ScaleType.FIT_START);
                                elem2.setImageResource (R.drawable.skrpk);
                                elem2.setTag(Integer.toString(i));
                                elem2.setOnClickListener(new View.OnClickListener(){
                                    @Override
                                    public void onClick(View view) {
                                        active = resultObjects[Integer.parseInt(view.getTag().toString())];
                                        showFileChooser(3);
                                    }
                                });
                                i++;


                                elem3.setVerticalGravity(Gravity.CENTER_VERTICAL);
                                elem3.setHorizontalGravity(Gravity.RIGHT);
                                elem3.setOrientation(LinearLayout.HORIZONTAL);

                                elem3.addView(elem);
                                elem3.addView(elem2);
                                scv.addView(elem3);
                            }
                        }
                        findViewById(R.id.scv1).setVisibility(View.VISIBLE);
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("id", getValue());
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

    private void showFileChooser(int requestCode) {
        for (int i = 0; i < 5; i++) {
            try {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, requestCode);
                break;
            } catch (Exception e) {

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            for (int i = 0; i < 5; i++) {
                try {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    String uriString = uri.toString();
                    File myFile = new File(uriString);
                    String path = myFile.getAbsolutePath();
                    String displayName = null;

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = this.getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                InputStream iStream = null;
                                try {
                                    iStream = getContentResolver().openInputStream(uri);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (requestCode == 1) {
                                    inputData.put("req",getBytes(iStream));
                                } else if (requestCode == 2) {
                                    inputData.put("test",getBytes(iStream));
                                } else if (requestCode == 3) {
                                    inputData.put("change",getBytes(iStream));
                                    AsyncUploader uploadFileToServer = new AsyncUploader();
                                    uploadFileToServer.execute("change");
                                    return;
                                }
                                Toast.makeText(this,"Успешно", Toast.LENGTH_LONG).show();
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        InputStream iStream = null;
                        try {
                            iStream = getContentResolver().openInputStream(uri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (requestCode == 1) {
                            inputData.put("req",getBytes(iStream));
                        } else if (requestCode == 2) {
                            inputData.put("test",getBytes(iStream));
                        } else if (requestCode == 3) {
                            inputData.put("change",getBytes(iStream));
                            AsyncUploader uploadFileToServer = new AsyncUploader();
                            uploadFileToServer.execute("change");
                            return;
                        }
                        Toast.makeText(this,"Успешно", Toast.LENGTH_LONG).show();
                    }
                    break;
                } catch (Exception e) {
                }
            }
        }
    }


    public void onClickSlider (View v) {
        int n = Integer.parseInt(v.getTag().toString());
        lineUlt (n);

        findViewById(R.id.scv1).setVisibility(View.GONE);
        findViewById(R.id.AddReq).setVisibility(View.GONE);
        findViewById(R.id.AddTest).setVisibility(View.GONE);


        bt1.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
        bt2.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
        bt3.setTextAppearance(android.R.style.TextAppearance_Material_Body1);


        bt1.setTextSize(TypedValue.COMPLEX_UNIT_SP,BaseTxt);
        bt2.setTextSize(TypedValue.COMPLEX_UNIT_SP,BaseTxt);
        bt3.setTextSize(TypedValue.COMPLEX_UNIT_SP,BaseTxt);
        if (n == 1) {
            bt1.setTextAppearance(android.R.style.TextAppearance_Material_Body2);
            bt1.setTextSize(TypedValue.COMPLEX_UNIT_SP,ActiveTxt);
            getHistory();

        } else if (n == 2) {
            bt2.setTextAppearance(android.R.style.TextAppearance_Material_Body2);
            bt2.setTextSize(TypedValue.COMPLEX_UNIT_SP,ActiveTxt);
            findViewById(R.id.AddReq).setVisibility(View.VISIBLE);

        } else if (n== 3) {
            bt3.setTextAppearance(android.R.style.TextAppearance_Material_Body2);
            bt3.setTextSize(TypedValue.COMPLEX_UNIT_SP,ActiveTxt);
            findViewById(R.id.AddTest).setVisibility(View.VISIBLE);

        }

    }

    public void OnAddButton (View v) {
        int n = Integer.parseInt(v.getTag().toString());
        showFileChooser(n);
    }

    public void OnClickButton1 (View v) {
        int n = Integer.parseInt(v.getTag().toString());
        AsyncUploader uploadFileToServer = new AsyncUploader();
        if (n == 1) {
            uploadFileToServer.execute("req");
        } else {
            uploadFileToServer.execute("test");
        }
    }

    public void lineUlt  (int n) {
        if (f != null) {
            ((ViewManager)f.getParent()).removeView(f);
        }
        View v = new View(this);
        RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height_f);
        v.setLayoutParams(params);
        v.setBackgroundResource(R.color.black);
        v.setId(R.id.reservedNamedId);
        if (n==1) {
            LinearLayout li = (LinearLayout) findViewById(R.id.layln1);
            li.addView(v);
        } else if (n==2) {
            LinearLayout li = (LinearLayout) findViewById(R.id.layln2);
            li.addView(v);
        } else {
            LinearLayout li = (LinearLayout) findViewById(R.id.layln3);
            li.addView(v);
        }
        f = v;
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public String getValue(){
        SharedPreferences preferences = this.getSharedPreferences("settings", this.MODE_PRIVATE);
        return preferences.getString("id", "");
    }

    private class AsyncUploader extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            return uploadFile(params[0]);
        }
        @Override
        protected void  onPostExecute(String result) {

            resultProcess (result);
        }

        private String uploadFile(String requestTo) {

            if (inputData.get(requestTo) == null) return "100";
            try {
                OkHttpClient client = new OkHttpClient();
                MultipartBody.Builder buildBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("id", getValue());
                if (requestTo == "change") {
                    buildBody.addFormDataPart("path", active.path);
                }
                RequestBody requestBody = buildBody.addFormDataPart("file", "file.pdf",
                                RequestBody.create(MediaType.parse("text/plain"), inputData.get(requestTo))).build();

                Request request = new Request.Builder()
                        .url("http://213.226.126.69:5000/" + requestTo)
                        .post(requestBody)
                        .build();

                Call call = client.newCall(request);
                Response response = call.execute();
                response.body().close();
                return "200";
            } catch (Exception e) {
                return "0";
            }
        }
    }

    public void resultProcess (String result) {
        if (result.equals("200")) {
            Toast.makeText(this,"Успешно", Toast.LENGTH_LONG).show();
        } else if (result.equals("100")) {
            Toast.makeText(this,"Необходимо прикрепить файл", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"No internet coonection", Toast.LENGTH_LONG).show();
        }
    }

}