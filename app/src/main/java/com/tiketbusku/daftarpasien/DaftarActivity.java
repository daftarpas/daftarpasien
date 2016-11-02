package com.tiketbusku.daftarpasien;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;

public class DaftarActivity extends AppCompatActivity {

    ArrayList<String> listItems=new ArrayList<>();
    ArrayList<String> listItems1=new ArrayList<>();
    ArrayAdapter<String> adapter, adapter1;
    private String idPoli;

    // API urls
    // Url to create new categor
    // Url to get all categories
//    private String URL_CATEGORIES = "http://192.168.28.176:88/klinik/Service/getPoli";
    // API urls
    // Url to create new category
    // Url to get all categories
    private Spinner nPoli, nId;
    private Button nBtnDaftar;
    private EditText nNoKTP, nNama, nUsia, nAlamat, nKeluhan, nNoTelp;
    String[] id, nama;
    String item;

    JSONParser jParser = new JSONParser();
    ProgressDialog pDialog;
    private static String url = "https://daftarklinikid.000webhostapp.com/admin/Service/daftar";

    private ProgressDialog waiDialog;

    public static final String referensi = "MyPrefs";
    SharedPreferences data;

    UserSessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);

        session = new UserSessionManager(getApplicationContext());
        if (session.checkLogout()) {
            finish();
        }

        HashMap<String, String> user = session.getUserDetails();

        // get name
        String name = user.get(UserSessionManager.KEY_KTP);

        // get email
        String tgl = user.get(UserSessionManager.KEY_TGL);


        nPoli = (Spinner) findViewById(R.id.poli);
        nId = (Spinner) findViewById(R.id.id_poli);
        nBtnDaftar = (Button) findViewById(R.id.btn_daftar);
        nNoKTP = (EditText) findViewById(R.id.input_ktp);
        nNama = (EditText) findViewById(R.id.input_nama);
        nUsia = (EditText) findViewById(R.id.input_usia);
        nNoTelp = (EditText) findViewById(R.id.input_telepon);
        nAlamat = (EditText) findViewById(R.id.input_alamat);
        nKeluhan = (EditText) findViewById(R.id.input_keluhan);


        nId.setVisibility(View.GONE);
        adapter=new ArrayAdapter<String>(this,R.layout.spinner_layout,R.id.txt,listItems);
        nPoli.setAdapter(adapter);

        adapter1=new ArrayAdapter<String>(this,R.layout.spinner_layout,R.id.txt,listItems1);
        nId.setAdapter(adapter1);

        nPoli.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nId.setSelection(position);
                item = nId.getSelectedItem().toString();
                //Toast.makeText(DaftarActivity.this, item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        nId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nPoli.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        nBtnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((nNoKTP.equals(""))||(nNama.equals(""))||(nUsia.equals(""))||(nNoTelp.equals(""))||(nAlamat.equals(""))||(nKeluhan.equals(""))) {
                    Toast.makeText(DaftarActivity.this, "Silahkan melengkapi data anda", Toast.LENGTH_LONG).show();
                }
                else {
                    new input().execute();
                }
            }
        });
    }

    public class input extends AsyncTask<String, String, String>
    {

        String success, message;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DaftarActivity.this);
            pDialog.setMessage("Lagi Proses bro...");
            pDialog.setIndeterminate(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            String ktp = nNoKTP.getText().toString();
            String nama = nNama.getText().toString();
            String usia = nUsia.getText().toString();
            String alamat = nAlamat.getText().toString();
            String keluhan = nKeluhan.getText().toString();
            String telp = nNoTelp.getText().toString();
            String token = FirebaseInstanceId.getInstance().getToken();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_poli", item));
            params.add(new BasicNameValuePair("no_ktp", ktp));
            params.add(new BasicNameValuePair("nama", nama));
            params.add(new BasicNameValuePair("usia", usia));
            params.add(new BasicNameValuePair("alamat", alamat));
            params.add(new BasicNameValuePair("keluhan", keluhan));
            params.add(new BasicNameValuePair("telp", telp));
            params.add(new BasicNameValuePair("token", token));

            JSONObject json = jParser.makeHttpRequest(url, "POST", params);

            try {
                success = json.getString("success");
                message = json.getString("message");

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error",
                        Toast.LENGTH_LONG).show();
            }
            return null;
        }
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();

            if (success.equals("1"))
            {
                Calendar c = Calendar.getInstance();
                System.out.println("Current time => " + c.getTime());

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = df.format(c.getTime());

                Toast.makeText(getApplicationContext(), "Sukses broo!!!", Toast.LENGTH_LONG).show();
                session.createUserLoginSession( nNoKTP.getText().toString(), formattedDate);

                // Starting MainActivity
                Intent i = new Intent(getApplicationContext(), NomorAntri.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Add new Flag to start new Activity
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

                finish();
                /*Intent i = new Intent(DaftarActivity.this, NomorAntri.class);
                startActivity(i);*/
            }
            else if (success.equals("2"))
            {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
            else if (success.equals("3"))
            {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onStart(){
        super.onStart();
        BackTask bt=new BackTask();
        bt.execute();
    }

    private class BackTask extends AsyncTask<Void,Void,Void> {
        ArrayList<String> list, list1;
        protected void onPreExecute(){
            super.onPreExecute();
            list=new ArrayList<>();
            list1=new ArrayList<>();
        }
        protected Void doInBackground(Void...params){
            InputStream is=null;
            String result="";
            try{
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost= new HttpPost("https://daftarklinikid.000webhostapp.com/admin/Service/getPoli");
                HttpResponse response=httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                // Get our response as a String.
                is = entity.getContent();
            }catch(IOException e){
                e.printStackTrace();
            }

            //convert response to string
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    result+=line;
                }
                is.close();
                //result=sb.toString();
            }catch(Exception e){
                e.printStackTrace();
            }
            // parse json data
            try{
                JSONArray jArray =new JSONArray(result);
                JSONObject jsonObject = null;
                id = new String[jArray.length()];
                nama = new String[jArray.length()];
                for(int i=0;i<jArray.length();i++){
                    jsonObject=jArray.getJSONObject(i);
                    id[i] = jsonObject.getString("id_poli");
                    nama[i] = jsonObject.getString("nama");
                    // add interviewee name to arraylist
                    //idPoli = jsonObject.getString("id_poli");
                    //list.add(jsonObject.getString("nama"));
                }
                for(int i=0;i<nama.length;i++)
                {
                    list1.add(id[i]);
                    list.add(nama[i]);
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Void result){
            listItems.addAll(list);
            listItems1.addAll(list1);
            adapter.notifyDataSetChanged();
            adapter1.notifyDataSetChanged();
        }
    }
}
