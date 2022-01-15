package cl.app.montes.view.drawer.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cl.app.montes.R;
import cl.app.montes.clases.Usuarios;
import cl.app.montes.db.DatabaseHelper;
import cl.app.montes.view.detalle.DetalleActivity;
import cl.app.montes.view.editar.EditarActivity;
import cl.app.montes.view.main.MainActivity;
import cz.msebera.android.httpclient.Header;

public class Listado_fragment extends Fragment {


    Button nuevo, subir, vaciar;
    DatabaseHelper myDB;
    ArrayList<String> listaregistros = new ArrayList<>();
    ArrayList<String> listaidregistros = new ArrayList<>();
    RequestQueue mRequestQueue;
    StringRequest mStringRequest;
    String PROCESAR_URL = "https://agricolalosdelmonte.com/sistema/backend/public/api/v1/api/procesar";
    ProgressDialog progressDialog;
    SwipeMenuListView listado;
    TableLayout tabla_registros;
    TableRow row;
    ArrayList<Integer> array_id = new ArrayList<Integer>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_listado_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Listado de recorridos");
        nuevo = (Button) getView().findViewById(R.id.btnnuevo);
        subir = (Button) getView().findViewById(R.id.btnsubir);
        vaciar = (Button) getView().findViewById(R.id.btnvaciar);
        myDB = new DatabaseHelper(getActivity().getApplicationContext());
        listado = (SwipeMenuListView) getView().findViewById(R.id.listado_registros);

        obtenerlistado();
        ArrayAdapter adaptador= new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1,listaregistros);
        listado.setAdapter(adaptador);

        nuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment_actual = new Listado_fragment();
                Fragment fragment = new Formulario_fragment();
                getFragmentManager().beginTransaction().remove(fragment_actual).commit();
                getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

            }
        });

        vaciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SQLiteDatabase db = myDB.getWritableDatabase();

                    db.execSQL("DELETE FROM registros");

                    Toast.makeText(getActivity().getApplicationContext(), "Los datos fueron eliminados correctamente", Toast.LENGTH_SHORT).show();

                    Fragment fragment_actual = new Listado_fragment();
                    Fragment fragment = new Listado_fragment();
                    getFragmentManager().beginTransaction().remove(fragment_actual).commit();
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        subir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()){
                    EnviodeDatos enviodeDatos = new EnviodeDatos();
                    enviodeDatos.execute();

                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Error, sin acceso a Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "edit" item
                SwipeMenuItem editItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // set item background
                editItem.setBackground(new ColorDrawable(Color.rgb(0,150,136)));
                // set item width
                editItem.setWidth(170);
                // set a icon
                editItem.setIcon(R.drawable.ic_baseline_edit_24);
                // add to menu
                menu.addMenuItem(editItem);

                //create "details" item
                SwipeMenuItem detailsItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // set item background
                detailsItem.setBackground(new ColorDrawable(Color.rgb(0,150,136)));
                // set item width
                detailsItem.setWidth(170);
                // set a icon
                detailsItem.setIcon(R.drawable.ic_baseline_format_list_bulleted_24);
                // add to menu
                menu.addMenuItem(detailsItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0,150,136)));
                // set item width
                deleteItem.setWidth(170);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_baseline_delete_forever_24);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        listado.setMenuCreator(creator);


        listado.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        String idregistroeditar = listaidregistros.get(position);
                        Intent intenteditar = new Intent(getActivity().getApplicationContext(), EditarActivity.class);
                        intenteditar.putExtra("id_registro", idregistroeditar);
                        startActivity(intenteditar);

                        break;
                    case 1:
                        String idregistrodetalle = listaidregistros.get(position);
                        Intent intent = new Intent(getActivity().getApplicationContext(), DetalleActivity.class);
                        intent.putExtra("id_registro", idregistrodetalle);
                        startActivity(intent);
                        break;

                    case 2:

                        AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
                        alerta.setTitle("Eliminar recorrido");
                        alerta.setMessage("¿Estas seguro de eliminar?");
                        alerta.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    String idregistroeliminar = listaidregistros.get(position);
                                    eliminarRegistro(idregistroeliminar);
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        alerta.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alerta.show();
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

    private void eliminarRegistro(String id){
        SQLiteDatabase db = myDB.getWritableDatabase();
        db.execSQL("DELETE FROM registros WHERE id ='"+id+"'");
        Toast.makeText(getActivity().getApplicationContext(), "Registro eliminado correctamente.", Toast.LENGTH_SHORT).show();
        Fragment fragment = new Listado_fragment();
        getFragmentManager().beginTransaction().add(R.id.content_frame, fragment).commit();
    }

    private void obtenerlistado(){
        SQLiteDatabase db = myDB.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT reg.id as id_registro, " +
                "                               reg.productor as id_productor, " +
                "                               reg.fecha, " +
                "                               prod.razon_social  FROM registros reg " +
                "INNER JOIN productores prod ON reg.productor = prod.id",null);


        while (cursor.moveToNext()){
            listaregistros.add(""+cursor.getString(3)+" | "+ ""+cursor.getString(2));
            listaidregistros.add(cursor.getString(0));
        }
    }


    private class EnviodeDatos extends AsyncTask<Void, Integer, Boolean> {
        protected Boolean doInBackground(Void... voids) {
            try {
                SQLiteDatabase db = myDB.getWritableDatabase();
                Cursor cursor = db.rawQuery("SELECT id, id_usuario, fecha, hora, recorrido, productor, producto, " +
                        "variedad, tara, bandeja, cantidad_envase, kilos_brutos, kilos_netos, total, bandejas_pendientes," +
                        "bandejas_entregadas, precio_usuario, kilos_netos FROM registros", null);

                while (cursor.moveToNext()) {

                    int id = cursor.getInt(0);
                    String recorridos_id = cursor.getString(4);
                    String productor_id = cursor.getString(5);
                    String productos_id = cursor.getString(6);
                    String fecha = cursor.getString(2);
                    String taras_id = cursor.getString(8);
                    String bandejas_cant = cursor.getString(10);
                    String kilos_b = cursor.getString(11);
                    String bandejas_pend = cursor.getString(14);
                    String bandejas_entre = cursor.getString(15);
                    String precio_usuario = cursor.getString(16);
                    String hora = cursor.getString(3);
                    String kilos_netos = cursor.getString(17);


                    final RequestParams params = new RequestParams();

                    params.put("recorridos_id", recorridos_id);
                    params.put("productor_id", productor_id);
                    params.put("productos_id", productos_id);
                    params.put("fecha", fecha);
                    params.put("taras_id", taras_id);
                    params.put("bandejas_pendientes", bandejas_pend);
                    params.put("bandejas_entregadas", bandejas_entre);
                    params.put("bandejas", bandejas_cant);
                    params.put("kilos_brutos", kilos_b);
                    params.put("precio", precio_usuario);
                    params.put("hora", hora);
                    params.put("kilos_netos", kilos_netos);

                    Handler handler = new Handler(Looper.getMainLooper());
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            AsyncHttpClient client = new AsyncHttpClient();
                            client.post(PROCESAR_URL, params, new AsyncHttpResponseHandler() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    if (statusCode == 200) {
                                        Log.i("200", new String(responseBody,StandardCharsets.UTF_8));
                                        db.execSQL("DELETE FROM registros WHERE id ='" + id + "'");
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    try {
                                        String response = new String(responseBody).toUpperCase();
                                        Log.i("REQUEST FAIL", "" + error.toString() + " AA " + response);
                                        Toast.makeText(getActivity().getApplicationContext(), "Fallo el envío de datos.", Toast.LENGTH_SHORT).show();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    };
                    handler.post(runnable);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getView().getContext());
            progressDialog.setTitle("Enviando datos");
            progressDialog.setMessage("Espere un momento...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected void onPostExecute(Boolean result) {
            try {
                Thread.sleep(3000);
            }catch (Exception e){
                e.printStackTrace();
            }
            progressDialog.dismiss();
            Toast.makeText(getActivity().getApplicationContext(),"Envio de datos completado.", Toast.LENGTH_SHORT).show();

            Fragment fragment_actual = new Listado_fragment();
            Fragment fragment = new Listado_fragment();
            getFragmentManager().beginTransaction().remove(fragment_actual).commit();
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        }
    }
}