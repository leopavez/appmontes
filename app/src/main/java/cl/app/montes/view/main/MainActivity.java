package cl.app.montes.view.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cl.app.montes.R;
import cl.app.montes.clases.Usuarios;
import cl.app.montes.db.DatabaseHelper;
import cl.app.montes.view.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    Button ingresar;
    Button sincronizar;
    TextView ultsincronizacion;
    String API_SINCROINZAR = "https://agricolalosdelmonte.com/sistema/backend/public/api/v1/api/sincronizar";
    ProgressDialog progressDialog;
    RequestQueue mRequestQueue;
    StringRequest mStringRequest;
    DatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        ingresar = (Button)findViewById(R.id.btningresar);
        sincronizar = (Button)findViewById(R.id.btnsincronizar);
        ultsincronizacion = (TextView)findViewById(R.id.tvultimasincronizacion);
        myDB = new DatabaseHelper(MainActivity.this);

        //SETEAMOS LA ULTIMA SINCRONIZACION MEDIANTE SHARED PREFERENCES
        SharedPreferences sharedPreferences = getSharedPreferences("ultima_sincronizacion", Context.MODE_PRIVATE);
        String ultima_sincronizacion =  sharedPreferences.getString("fecha", "No disponible");
        ultsincronizacion.setText("Ultima sincronización: "+ ultima_sincronizacion);

        sincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()){
                    sincronizarapp tareaAsincrona = new sincronizarapp();
                    tareaAsincrona.execute();
                }else{
                    Toast.makeText(MainActivity.this, "Error, sin acceso a Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });
    }


    //TAREA EN SEGUNDO PLANO PARA SINCRONIZAR LA APP
    private class sincronizarapp extends AsyncTask<Void, Integer, Boolean> {
        protected Boolean doInBackground(Void... voids) {
            mRequestQueue = Volley.newRequestQueue(MainActivity.this);
            mStringRequest = new StringRequest(Request.Method.POST, API_SINCROINZAR, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        String json = response.toString();
                        Usuarios usuarios = new Usuarios();
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray jsonUsuarios = jsonObject.getJSONArray("usuarios");
                        JSONArray jsonProductos = jsonObject.getJSONArray("productos");
                        JSONArray jsonProductores = jsonObject.getJSONArray("productores");
                        JSONArray jsonRecorridos = jsonObject.getJSONArray("recorridos");
                        JSONArray jsonVariedad = jsonObject.getJSONArray("variedad");
                        JSONArray jsonTara = jsonObject.getJSONArray("taras");

                        SQLiteDatabase db = myDB.getWritableDatabase();

                        db.execSQL("DELETE FROM usuarios");
                        db.execSQL("DELETE FROM productos");
                        db.execSQL("DELETE FROM productores");
                        db.execSQL("DELETE FROM recorrido");
                        db.execSQL("DELETE FROM variedad");
                        db.execSQL("DELETE FROM tara");


                        for (int i = 0;  i<jsonUsuarios.length(); i++){
                            JSONObject data = jsonUsuarios.getJSONObject(i);
                            int id = data.getInt("id");
                            String name = data.getString("name");
                            String email = data.getString("email");
                            String password = data.getString("password");

                            Cursor getUsuarios = db.rawQuery("SELECT id, name , email, password FROM usuarios WHERE id ='"+id+"'",null);

                            if (getUsuarios.getCount() <= 0){
                                //NO EXISTE USUARIO
                                myDB.guardarUsuarios(id,name,email,password);
                            }
                        }

                        for (int i = 0;  i<jsonProductos.length(); i++){
                            JSONObject data = jsonProductos.getJSONObject(i);
                            int id = data.getInt("id");
                            String name = data.getString("name");
                            int precio = data.getInt("precio");

                            Cursor getProductos = db.rawQuery("SELECT id, name , precio FROM productos WHERE id ='"+id+"'",null);

                            if (getProductos.getCount() <= 0){
                                //NO EXISTE PRODUCTO
                                myDB.guardarProductos(id,name,precio);
                            }
                        }

                        for (int i = 0;  i<jsonProductores.length(); i++){
                            JSONObject data = jsonProductores.getJSONObject(i);
                            int id = data.getInt("id");
                            String razon_social = data.getString("razon_social");
                            String rut = data.getString("rut");

                            Cursor getProductores = db.rawQuery("SELECT id, razon_social , rut FROM productores WHERE id ='"+id+"'",null);

                            if (getProductores.getCount() <= 0){
                                //NO EXISTE PRODUCTOR
                                myDB.guardarProductores(id,razon_social,rut);
                            }
                        }

                        for (int i = 0;  i<jsonRecorridos.length(); i++){
                            JSONObject data = jsonRecorridos.getJSONObject(i);
                            int id = data.getInt("id");
                            String name = data.getString("name");

                            Cursor getRecorridos = db.rawQuery("SELECT id, name FROM recorrido WHERE id ='"+id+"'",null);

                            if (getRecorridos.getCount() <= 0){
                                //NO EXISTE RECORRIDO
                                myDB.guardarRecorrido(id,name);
                            }
                        }

                        for (int i = 0;  i<jsonVariedad.length(); i++){
                            JSONObject data = jsonVariedad.getJSONObject(i);
                            int id = data.getInt("id");
                            String name = data.getString("name");

                            Cursor getVariedad = db.rawQuery("SELECT id, name FROM variedad WHERE id ='"+id+"'",null);

                            if (getVariedad.getCount() <= 0){
                                //NO EXISTE VARIEDAD
                                myDB.guardarVariedad(id,name);
                            }
                        }

                        for (int i = 0;  i<jsonTara.length(); i++){
                            JSONObject data = jsonTara.getJSONObject(i);
                            int id = data.getInt("id");
                            String peso = data.getString("peso");
                            String name_envase = data.getString("name_envase");

                            Cursor getVariedad = db.rawQuery("SELECT id, peso, name_envase FROM tara WHERE id ='"+id+"'",null);

                            if (getVariedad.getCount() <= 0){
                                //NO EXISTE TARA
                                myDB.guardarTara(id,peso,name_envase);
                            }
                        }

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String fecha_actual = sdf.format(c.getTime());
                        SharedPreferences sharedPreferences = getSharedPreferences("ultima_sincronizacion", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("fecha", fecha_actual);
                        editor.apply();

                        ultsincronizacion.setText("Ultima sincronización: "+ fecha_actual);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Fallo la sincronización", Toast.LENGTH_SHORT).show();
                    Log.i("Error response:",error.toString());
                }
            });
            mStringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            mRequestQueue.add(mStringRequest);

            return true;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Sincronizando aplicación");
            progressDialog.setMessage("Espere un momento...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected void onPostExecute(Boolean result) {
            try {
                Thread.sleep(2000);
            }catch (Exception e){
                e.printStackTrace();
            }
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this,"Sincronización completada", Toast.LENGTH_SHORT).show();
        }
    }
}