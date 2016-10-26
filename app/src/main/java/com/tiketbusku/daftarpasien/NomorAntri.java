package com.tiketbusku.daftarpasien;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class NomorAntri extends AppCompatActivity {

    JSONParser jParser = new JSONParser();
    ProgressDialog pDialog;

    // User Session Manager Class
    UserSessionManager session;
    TextView text, text1, text2, text3, text4, text5, text6;
    EditText e;
    String ktp;
    private ProgressDialog loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nomor_antri);

        e = (EditText) findViewById(R.id.editText);
        text = (TextView) findViewById(R.id.textView2);
        text1 = (TextView) findViewById(R.id.textView4);
        text2 = (TextView) findViewById(R.id.textView5);
        text3 = (TextView) findViewById(R.id.textView6);
        text4 = (TextView) findViewById(R.id.textView7);
        text5 = (TextView) findViewById(R.id.textView8);
        text6 = (TextView) findViewById(R.id.textView9);
        // Session class instance
        session = new UserSessionManager(getApplicationContext());
        if (session.checkLogin()) {
            Toast.makeText(NomorAntri.this, "Anda Belum Melakukan Pendaftaran Untuk Pemeriksaan Hari Ini", Toast.LENGTH_LONG).show();
            finish();
        }

        // Check user login (this is the important point)
        // If User is not logged in , This will redirect user to LoginActivity
        // and finish current activity from activity stack.

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // get ktp
        ktp = user.get(UserSessionManager.KEY_KTP);

        // get ktp
        String tgl = user.get(UserSessionManager.KEY_TGL);
//        text.setText(tgl);
     /*   try {
            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = df.format(c.getTime());

            Date date1 = df.parse(tgl);
            Date date2 = df.parse(formattedDate);
            if (date1.compareTo(date2)<0) {
                session.logoutUser();
            }
        }

        catch (ParseException e1) {
            e1.printStackTrace();
        }*/

        getData();
//        text.setText(ktp);
    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }

    private void getData() {
        if (ktp.equals("")) {
            Toast.makeText(NomorAntri.this, "Anda Belum Melakukan Pendaftaran Untuk Pemeriksaan Hari Ini", Toast.LENGTH_LONG).show();
            return;
        }
        loading=ProgressDialog.show(this,"Please wait...","Fetching...",false,false);
        String url=Config.DATA_URL+ktp.trim();
        StringRequest stringRequest=new StringRequest(url,new Response.Listener<String>(){
            @Override
            public void onResponse(String response){
                loading.dismiss();
                showJSON(response);
            }
        },
            new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(NomorAntri.this,error.getMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response){
        String nama = "";
        String nomor_antrian = "";
        String poli = "";
        String klinik = "";
        String dokter = "";
        String keluhan = "";
        try{
            JSONObject jsonObject=new JSONObject(response);
            JSONArray result=jsonObject.getJSONArray(Config.JSON_ARRAY);
            JSONObject collegeData=result.getJSONObject(0);
            nama=collegeData.getString(Config.KEY_NAMA);
            nomor_antrian=collegeData.getString(Config.KEY_NOMOR_ANTRIAN);
            poli=collegeData.getString(Config.KEY_POLI);
            klinik=collegeData.getString(Config.KEY_KLINIK);
            dokter=collegeData.getString(Config.KEY_DOKTER);
            keluhan=collegeData.getString(Config.KEY_KELUHAN);
            if ((nama.equals("null"))&&(nomor_antrian.equals("null"))&&(poli.equals("null"))) {
//                text.setText("Tidak ada apapa");
                Toast.makeText(NomorAntri.this, "Maaf Anda Belum Mendaftar Untuk Pemeriksaan Hari ini", Toast.LENGTH_LONG).show();
                session.logoutUser();
            }
            else {
                text.setText("Nama = \t"+nama);
                text1.setText("Nomor Antrian anda adalah = \t"+nomor_antrian);
                text2.setText("Klinik = \t"+klinik);
                text3.setText("Poli = \t"+poli);
                text4.setText("Diperiksa Oleh = \t"+dokter);
                text5.setText("Dengan Keluhan = \t"+keluhan);
                text6.setText("Silahkan Menunggu Pemberitahuan Jam Kedatangan Dokter \t"+dokter+" Untuk Mengefisienkan Waktu Tunggu Anda");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
