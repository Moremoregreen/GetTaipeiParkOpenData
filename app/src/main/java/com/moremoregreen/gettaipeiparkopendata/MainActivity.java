package com.moremoregreen.gettaipeiparkopendata;

import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    //臺北市公園景點
    public static final String MY_URL = "http://data.taipei/opendata/datalist/apiAccess?scope=resourceAquire&rid=bf073841-c734-49bf-a97f-3757a6013812";
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);

    }

    private boolean isNetworkConnected(){
        ConnectivityManager cm =(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    public void onBtnClick(View view) {
        if(isNetworkConnected()){
            new GetDataTask().execute(MY_URL);
        }else {
            Toast.makeText(this, "No Internet!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private class GetDataTask extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("讀取中，稍等");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(10000);
                con.setUseCaches(false);
                br = new BufferedReader(
                        new InputStreamReader(
                                con.getInputStream()));
                String str;
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }

            } catch (Exception e) {
                Log.e("Main", "doInBackground: " + e.toString());
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        Log.e("Main", "doInBackground:br.close = " + e.toString());
                    }
                }
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.cancel();
            tvResult.setText(s);
        }
    }
}
