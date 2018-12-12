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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewProductActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    JSONParser jsonParser = new JSONParser();
    EditText inputName;
    EditText inputPrice;
    EditText inputDesc;

    private static  final String url_create_product = "http://192.168.43.159/android/create_product.php";

    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        inputName = findViewById(R.id.inputName);
        inputPrice = findViewById(R.id.inputPrice);
        inputDesc = findViewById(R.id.inputDesc);

        Button btnCreate = findViewById(R.id.btnCreateProduct);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CreateNewProduct().execute();
            }
        });
    }

    class CreateNewProduct extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(NewProductActivity.this);
            progressDialog.setMessage("Creating product...");
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

            String name = inputName.getText().toString();
            String price = inputPrice.getText().toString();
            String desc = inputDesc.getText().toString();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("price", price));
            params.add(new BasicNameValuePair("description", desc));

            JSONObject json = null;
            try {
                json = jsonParser.makeHttpRequest(url_create_product, "POST", params);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try{
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1){
                    Intent i = new Intent(getApplicationContext(), AllProductActivity.class);
                    startActivity(i);

                    finish();
                }else {

                }
            }catch (JSONException e){

                e.printStackTrace();
            }
            return null;
        }
    }
}
