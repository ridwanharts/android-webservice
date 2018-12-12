package com.example.ridwa.androidconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditProductActivity extends AppCompatActivity {

    EditText tName;
    EditText tPrice;
    EditText tDesc;
    EditText tCreateAt;
    Button btnSave;
    Button btnDelete;
    String pid;
    private ProgressDialog progressDialog;
    JSONParser jsonParser = new JSONParser();

    private static final String url_product_details = "http://192.168.43.159/android/get_product_detail.php";
    private static final String url_update_product = "http://192.168.43.159/android/update_product.php";
    private static final String url_delete_product = "http://192.168.43.159/android/delete_product.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_DESCRIPTION = "description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        Intent i = getIntent();
        pid = i.getStringExtra(TAG_PID);
        new GetProduct().execute();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SaveProduct().execute();

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DeleteProduct().execute();

            }
        });
    }

    class GetProduct extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditProductActivity.this);
            progressDialog.setMessage("Loading product details. Please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int success;
                    try{
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("pid", pid));

                        JSONObject jsonObject = jsonParser.makeHttpRequest(url_product_details, "GET", params);
                        success = jsonObject.getInt(TAG_SUCCESS);

                        if (success == 1){
                            JSONArray productObj = jsonObject.getJSONArray(TAG_PRODUCT);
                            JSONObject product = productObj.getJSONObject(0);

                            tName = findViewById(R.id.inputName);
                            tPrice = findViewById(R.id.inputPrice);
                            tDesc = findViewById(R.id.inputDesc);

                            tName.setText(product.getString(TAG_NAME));
                            tPrice.setText(product.getString(TAG_PRICE));
                            tDesc.setText(product.getString(TAG_DESCRIPTION));
                        }else {

                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            return null;
        }
    }

    class SaveProduct extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditProductActivity.this);
            progressDialog.setMessage("Saving product details. Please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {

            String name = tName.getText().toString();
            String price = tPrice.getText().toString();
            String desc = tDesc.getText().toString();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_PID, pid));
            params.add(new BasicNameValuePair(TAG_NAME, name));
            params.add(new BasicNameValuePair(TAG_PRICE, price));
            params.add(new BasicNameValuePair(TAG_DESCRIPTION, desc));

            JSONObject jsonObject = null;
            try {
                jsonObject = jsonParser.makeHttpRequest(url_update_product, "POST", params);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try{
                int success = jsonObject.getInt(TAG_SUCCESS);
                if (success == 1){
                    Intent i = getIntent();
                    setResult(100, i);
                    finish();
                }else {

                }

            }catch (JSONException e){
                e.printStackTrace();
            }

            return null;
        }
    }

    class DeleteProduct extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {

            int success;
            try{
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("pid", pid));

                JSONObject jsonObject = null;
                try {
                    jsonObject = jsonParser.makeHttpRequest(url_delete_product,"POST", params);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                success = jsonObject.getInt(TAG_SUCCESS);

                if (success == 1){

                    Intent i = getIntent();
                    setResult(100, i);
                    finish();
                }

            }catch (JSONException e){

                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditProductActivity.this);
            progressDialog.setMessage("Deleting product details. Please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }
}
