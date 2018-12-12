package com.example.ridwa.androidconnect;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllProductActivity extends ListActivity {

    private ProgressDialog progressDialog;

    JSONParser jsonParser = new JSONParser();

    ArrayList<HashMap<String, String>> productList;

    private static String url_all_product = "http://192.168.43.159/android/get_all_product.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";

    JSONArray product = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_product);

        productList = new ArrayList<HashMap<String, String>>();

        new LoadAllProduct().execute();

        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String pid = ((TextView)view.findViewById(R.id.pid)).getText().toString();

                Intent in = new Intent(getApplicationContext(), EditProductActivity.class);
                in.putExtra(TAG_PID, pid);
                startActivityForResult(in, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 100){
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    class LoadAllProduct extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(AllProductActivity.this);
            progressDialog.setMessage("Loading product. please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            JSONObject json = null;
            try {
                json = jsonParser.makeHttpRequest(url_all_product, "GET", params);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("All Products: ", json.toString());

            try{
                int success = json.getInt(TAG_SUCCESS);

                if(success == 1){
                    product = json.getJSONArray(TAG_PRODUCT);

                    for (int i=0; i<product.length(); i++){
                        JSONObject c = product.getJSONObject(i);
                        String id = c.getString(TAG_PID);
                        String name = c.getString(TAG_NAME);

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(TAG_PID, id);
                        map.put(TAG_NAME, name);
                        productList.add(map);
                    }
                }else{
                    Intent i = new Intent(getApplicationContext(), NewProductActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            return null;
        }

        protected  void onPostExecute(String file_url){
            progressDialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ListAdapter adapter = new SimpleAdapter(AllProductActivity.this, productList, R.layout.item_product,
                            new String[]{
                                    TAG_PID,
                                    TAG_NAME},
                            new int[]{
                                    R.id.pid,
                                    R.id.name
                            });
                    setListAdapter(adapter);
                }
            });
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        if(progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
