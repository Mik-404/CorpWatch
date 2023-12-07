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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

public class MainScreen extends AppCompatActivity {
    int height_f = 3;
    View f = null;
    RelativeLayout layout;
    Button bt1, bt2, bt3;
    float ActiveTxt = 18f;
    float BaseTxt = 17;
    byte[] inputData;
    TextView ex1tsc;
    ImageButton ex1bsc;
    LinearLayout ex1lsc;
    String CurrentReq;
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
        lineUlt(1);
        getHistory();
    }
    public void getHistory () {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://213.226.126.69/hist.php",
                new Response.Listener<String>() {
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
                            LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                            //params.setMargins(1, 5, 1, 0);
                            elem.setLayoutParams(params);
                            elem.setTextColor(getResources().getColor(R.color.black));
                            elem.setTextAppearance(android.R.style.TextAppearance_Material_Body2);
                            elem.setTextSize(TypedValue.COMPLEX_UNIT_SP,ActiveTxt);
                            elem.setGravity(Gravity.CENTER);
                            elem.setText("Ничего не найдено");
                            scv.addView(elem);
                            return;
                        }
                        Files [] resultObjects = new Gson().fromJson(response.toString(), Files[].class);
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



                            elem3.setVerticalGravity(Gravity.CENTER_VERTICAL);
                            elem3.setHorizontalGravity(Gravity.RIGHT);
                            elem3.setOrientation(LinearLayout.HORIZONTAL);

                            elem3.addView(elem);
                            elem3.addView(elem2);
                            scv.addView(elem3);
                        }
                    }
                }, new Response.ErrorListener() {
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
    //method to show file chooser
    private void showFileChooser() {
        System.out.println(12);
        for (int i = 0; i < 5; i++) {
            try {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, 1);
                break;
            } catch (Exception e) {

            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (int i = 0; i < 5; i++) {
            try {
                if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    if (resultCode == RESULT_OK) {
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
                                    displayName = cursor.getString(Math.max(0,cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));

                                    uploadPDF(displayName,uri);
                                }
                            } finally {
                                cursor.close();
                            }
                        } else if (uriString.startsWith("file://")) {
                            displayName = myFile.getName();
                            uploadPDF(displayName,uri);
                        }
                    }
                }
                break;
            } catch (Exception e) {

            }
        }
    }

    private void uploadPDF(final String pdfname, Uri pdffile){
        InputStream iStream = null;
        try {
            iStream = getContentResolver().openInputStream(pdffile);
            System.out.println(pdffile.toString());
            inputData = getBytes(iStream);
            AsyncUploader uploadFileToServer = new AsyncUploader();
            uploadFileToServer.execute();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this,"Успешно", Toast.LENGTH_LONG).show();
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
    public void onClickHistory (View v) {
        lineUlt (1);
        findViewById(R.id.button10).setVisibility(View.GONE);
        findViewById(R.id.button11).setVisibility(View.GONE);
        findViewById(R.id.scv1).setVisibility(View.VISIBLE);
        bt1.setTextAppearance(android.R.style.TextAppearance_Material_Body2);
        bt2.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
        bt3.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
        bt1.setTextSize(TypedValue.COMPLEX_UNIT_SP,ActiveTxt);
        bt2.setTextSize(TypedValue.COMPLEX_UNIT_SP,BaseTxt);
        bt3.setTextSize(TypedValue.COMPLEX_UNIT_SP,BaseTxt);
        getHistory();
    }
    public void onClickReq (View v) {
        lineUlt (2);
        findViewById(R.id.button11).setVisibility(View.VISIBLE);
        findViewById(R.id.scv1).setVisibility(View.GONE);
        findViewById(R.id.button10).setVisibility(View.GONE);
        bt1.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
        bt2.setTextAppearance(android.R.style.TextAppearance_Material_Body2);
        bt3.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
        bt2.setTextSize(TypedValue.COMPLEX_UNIT_SP,ActiveTxt);
        bt1.setTextSize(TypedValue.COMPLEX_UNIT_SP,ActiveTxt);
        bt3.setTextSize(TypedValue.COMPLEX_UNIT_SP,BaseTxt);
    }
    public void OnClickReqButton (View v) {
        CurrentReq = "req";
        showFileChooser();
    }
    public void onClickTest (View v) {
        lineUlt (3);
        findViewById(R.id.scv1).setVisibility(View.GONE);
        findViewById(R.id.button11).setVisibility(View.GONE);
        findViewById(R.id.button10).setVisibility(View.VISIBLE);
        bt1.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
        bt2.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
        bt3.setTextAppearance(android.R.style.TextAppearance_Material_Body2);
        bt3.setTextSize(TypedValue.COMPLEX_UNIT_SP,ActiveTxt);
        bt2.setTextSize(TypedValue.COMPLEX_UNIT_SP,BaseTxt);
        bt1.setTextSize(TypedValue.COMPLEX_UNIT_SP,BaseTxt);
    }
    public void onClickTestButton (View v) {
        CurrentReq = "test";
        showFileChooser();
    }
    public void lineUlt  (int n) {
        if (f != null) {
            ((ViewManager)f.getParent()).removeView(f);
        }
        View v = new View(this);
        RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height_f);
        //params.setMargins(1, 5, 1, 0);
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

    public String getValue(){
        SharedPreferences preferences = this.getSharedPreferences("settings", this.MODE_PRIVATE);
        return preferences.getString("id", "");
    }

    private class AsyncUploader extends AsyncTask<Void, Integer, String> {
        String attachmentName = "file";
        String attachmentFileName = "file.pdf";
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        private String uploadFile() {
            try {
                JSONObject params = new JSONObject();
                params.put("id", getValue());
                HttpURLConnection httpUrlConnection = null;
                URL url = new URL("http://213.226.126.69:5000/" + CurrentReq);
                httpUrlConnection = (HttpURLConnection) url.openConnection();
                httpUrlConnection.setUseCaches(false);
                httpUrlConnection.setDoOutput(true);

                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
                httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
                httpUrlConnection.setRequestProperty(
                        "Content-Type", "multipart/form-data;boundary=" + this.boundary);
                DataOutputStream request = new DataOutputStream(
                        httpUrlConnection.getOutputStream());

                request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
                request.writeBytes("Content-Disposition: form-data; name=\"" +
                        this.attachmentName + "\";filename=\"" +
                        this.attachmentFileName + "\"" + this.crlf);
                request.writeBytes(this.crlf);
                request.write(inputData);
                request.writeBytes(this.crlf);
                request.writeBytes(this.twoHyphens + this.boundary +
                        this.twoHyphens + this.crlf);
                request.flush();
                request.close();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(httpUrlConnection.getOutputStream()));
                writer.write(params.toString());
                writer.flush();
                writer.close();

                InputStream responseStream = new
                        BufferedInputStream(httpUrlConnection.getInputStream());

                BufferedReader responseStreamReader =
                        new BufferedReader(new InputStreamReader(responseStream));

                String line = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((line = responseStreamReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                responseStreamReader.close();

                String response = stringBuilder.toString();
                responseStream.close();
                httpUrlConnection.disconnect();
                return response;
            } catch (Exception e) {
                return "0";
            }
        }
    }
}