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

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cl.app.montes.R;
import cl.app.montes.db.DatabaseHelper;
import cl.app.montes.view.detalle.DetalleActivity;
import cl.app.montes.view.detalle.DetalleDevolverActivity;
import cl.app.montes.view.detalle.DetalleRecuperarActivity;
import cl.app.montes.view.editar.EditarActivity;
import cz.msebera.android.httpclient.Header;

public class Listado_cc_fragment extends Fragment {


    Button nuevo, subir, vaciar;
    DatabaseHelper myDB;
    ArrayList<String> listaregistros = new ArrayList<>();
    ArrayList<String> listaidregistros = new ArrayList<>();
    ArrayList<String> listaregistrosrecuperados = new ArrayList<>();
    ArrayList<String> listaidregistrosrecuperados = new ArrayList<>();
    RequestQueue mRequestQueue;
    StringRequest mStringRequest;
    String PROCESAR_URL_DEVOLUCION = "https://agricolalosdelmonte.com/sistema/backend/public/api/v1/api/procesardevolucion";
    String PROCESAR_URL_RECUPERAR = "https://agricolalosdelmonte.com/sistema/backend/public/api/v1/api/procesarrecuperacion";
    //String PROCESAR_URL_DEVOLUCION = "https://ac-turing.cl/sistema/backend/public/api/v1/api/procesardevolucion"; //TEST
    //String PROCESAR_URL_RECUPERAR = "https://ac-turing.cl/sistema/backend/public/api/v1/api/procesarrecuperacion"; //TEST
    //String PROCESAR_URL_DEVOLUCION = "http://192.168.1.160:8000/api/v1/api/procesardevolucion";
    //String PROCESAR_URL_RECUPERAR = "http://192.168.1.160:8000/api/v1/api/procesarrecuperacion";
    ProgressDialog progressDialog;
    SwipeMenuListView listado, listadorecuperados;
    TableLayout tabla_registros;
    TableRow row;
    ArrayList<Integer> array_id = new ArrayList<Integer>();
    SharedPreferences preferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_listado_cc_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Listado de C. Corriente Envases");
        nuevo = (Button) getView().findViewById(R.id.btnnuevo);
        subir = (Button) getView().findViewById(R.id.btnsubir);
        vaciar = (Button) getView().findViewById(R.id.btnvaciar);
        myDB = new DatabaseHelper(getActivity().getApplicationContext());
        listado = (SwipeMenuListView) getView().findViewById(R.id.listado_registros);
        listadorecuperados = (SwipeMenuListView) getView().findViewById(R.id.listado_registros_recuperados);

        obtenerlistado();
        ArrayAdapter adaptador= new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1,listaregistros);
        listado.setAdapter(adaptador);

        ArrayAdapter adaptadorrecuperados= new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1,listaregistrosrecuperados);
        listadorecuperados.setAdapter(adaptadorrecuperados);

        nuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment_actual = new Listado_cc_fragment();
                Fragment fragment = new Cc_fragment();
                getFragmentManager().beginTransaction().remove(fragment_actual).commit();
                getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

            }
        });

        vaciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    progressDialog = new ProgressDialog(getView().getContext());
                    progressDialog.setTitle("Eliminando datos");
                    progressDialog.setMessage("Espere un momento...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    for (String idregistro : listaidregistros) {
                        vaciarRegistro(idregistro);
                    }

                    for (String idregistro: listaidregistrosrecuperados) {
                        vaciarRegistroRecuperar(idregistro);
                    }

                    progressDialog.dismiss();
                    Toast.makeText(getActivity().getApplicationContext(), "Los datos fueron eliminados correctamente", Toast.LENGTH_SHORT).show();

                    Fragment fragment_actual = new Listado_cc_fragment();
                    Fragment fragment = new Listado_cc_fragment();
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
        listadorecuperados.setMenuCreator(creator);


        listado.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        String idregistrodetalle = listaidregistros.get(position);
                        Intent intent = new Intent(getActivity().getApplicationContext(), DetalleDevolverActivity.class);
                        intent.putExtra("id_registro", idregistrodetalle);
                        startActivity(intent);
                        break;

                    case 1:

                        AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
                        alerta.setTitle("Eliminar Devolución");
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

        listadorecuperados.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        String idregistrodetalle = listaidregistrosrecuperados.get(position);
                        Intent intent = new Intent(getActivity().getApplicationContext(), DetalleRecuperarActivity.class);
                        intent.putExtra("id_registro", idregistrodetalle);
                        startActivity(intent);
                        break;

                    case 1:
                        AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
                        alerta.setTitle("Eliminar recuperación");
                        alerta.setMessage("¿Estas seguro de eliminar?");
                        alerta.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    String idregistroeliminar = listaidregistrosrecuperados.get(position);
                                    eliminarRegistroRecuperar(idregistroeliminar);
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
        Cursor cursorDevoluciones = db.rawQuery("SELECT ep.id, ep.productor_id, ep.envases_id, ep.cantidad, ep.desde, ep.hasta  FROM registros_devolucion ep WHERE ep.id = '" + id + "' ",null);

        int productorid = 0;
        int cantidad = 0;
        int envasedid = 0;
        String desde = "";
        String hasta = "";

        while(cursorDevoluciones.moveToNext()) {
            productorid = cursorDevoluciones.getInt(1);
            cantidad = cursorDevoluciones.getInt(3);
            envasedid = cursorDevoluciones.getInt(2);
            desde = cursorDevoluciones.getString(4);
            hasta = cursorDevoluciones.getString(5);
        }


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-dd-MM");
        Log.i("DESDE", simpleDateFormat.format(new Date(desde)));
        Log.i("HASTA", simpleDateFormat.format(new Date(hasta)));

        Cursor cursorPendientes = db.rawQuery("SELECT id , ep.cantidad FROM envasesPendientes ep WHERE productor_id ='" + productorid + "' AND fecha >= date('" + simpleDateFormat.format(new Date(desde.toString())) + "') AND fecha <= date('" + simpleDateFormat.format(new Date(hasta.toString())) + "') AND envase_id = '" + envasedid + "' ",null);


        int idregistroactualizar = 0;
        int nuevacantidad = 0;
        while (cursorPendientes.moveToNext()) {
            idregistroactualizar = cursorPendientes.getInt(0);
            nuevacantidad = cantidad + cursorPendientes.getInt(1);
        }

        Log.i("NUEVA CANTIDAD", String.valueOf(nuevacantidad));

        db.execSQL("UPDATE envasesPendientes SET cantidad = '" + nuevacantidad + "' WHERE id ='" + idregistroactualizar + "' ");
        db.execSQL("DELETE FROM registros_devolucion WHERE id ='"+id+"'");
        Toast.makeText(getActivity().getApplicationContext(), "Registro eliminado correctamente.", Toast.LENGTH_SHORT).show();
        Fragment fragment = new Listado_cc_fragment();
        getFragmentManager().beginTransaction().add(R.id.content_frame, fragment).commit();
    }

    private void eliminarRegistroRecuperar(String id){
        SQLiteDatabase db = myDB.getWritableDatabase();
        Cursor cursorRecuperacion = db.rawQuery("SELECT ep.id, ep.productor_id, ep.envases_id, ep.cantidad, ep.desde, ep.hasta  FROM registros_recuperar ep WHERE ep.id = '" + id + "' ",null);

        int productorid = 0;
        int cantidad = 0;
        int envasedid = 0;
        String desde = "";
        String hasta = "";

        while(cursorRecuperacion.moveToNext()) {
            productorid = cursorRecuperacion.getInt(1);
            cantidad = cursorRecuperacion.getInt(3);
            envasedid = cursorRecuperacion.getInt(2);
            desde = cursorRecuperacion.getString(4);
            hasta = cursorRecuperacion.getString(5);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-dd-MM");
        Log.i("DESDE", simpleDateFormat.format(new Date(desde)));
        Log.i("HASTA", simpleDateFormat.format(new Date(hasta)));

        Cursor cursorRecuperar = db.rawQuery("SELECT id , ep.cantidad FROM envasesRecuperar ep WHERE productor_id ='" + productorid + "' AND fecha >= date('" + simpleDateFormat.format(new Date(desde.toString())) + "') AND fecha <= date('" + simpleDateFormat.format(new Date(hasta.toString())) + "') AND envase_id = '" + envasedid + "' ",null);


        int idregistroactualizar = 0;
        int nuevacantidad = 0;
        while (cursorRecuperar.moveToNext()) {
            idregistroactualizar = cursorRecuperar.getInt(0);
            nuevacantidad = cantidad + cursorRecuperar.getInt(1);
        }


        db.execSQL("UPDATE envasesRecuperar SET cantidad = '" + nuevacantidad + "' WHERE id ='" + idregistroactualizar + "' ");
        db.execSQL("DELETE FROM registros_recuperar WHERE id ='"+id+"'");
        Toast.makeText(getActivity().getApplicationContext(), "Registro eliminado correctamente.", Toast.LENGTH_SHORT).show();
        Fragment fragment = new Listado_cc_fragment();
        getFragmentManager().beginTransaction().add(R.id.content_frame, fragment).commit();
    }

    private void obtenerlistado(){
        SQLiteDatabase db = myDB.getReadableDatabase();

        Cursor cursorDevoluciones = db.rawQuery("SELECT ep.id, ep.productor_id, ep.envases_id, ep.cantidad,prod.razon_social, ta.name_envase, ta.envase_id FROM registros_devolucion ep INNER JOIN productores prod ON ep.productor_id = prod.id INNER JOIN tara ta ON ep.envases_id = ta.envase_id ",null);
        Cursor cursorRecuperados = db.rawQuery("SELECT ep.id, ep.productor_id, ep.envases_id, ep.cantidad,prod.razon_social, ta.name_envase, ta.envase_id FROM registros_recuperar ep INNER JOIN productores prod ON ep.productor_id = prod.id INNER JOIN tara ta ON ep.envases_id = ta.envase_id ",null);


        while (cursorDevoluciones.moveToNext()){
            listaregistros.add(""+cursorDevoluciones.getString(4)+" | "+ ""+cursorDevoluciones.getString(3) + " | " + ""+cursorDevoluciones.getString(5));
            listaidregistros.add(cursorDevoluciones.getString(0));
        }

        while (cursorRecuperados.moveToNext()){
            listaregistrosrecuperados.add(""+cursorRecuperados.getString(4)+" | "+ ""+cursorRecuperados.getString(3) + " | " + ""+cursorRecuperados.getString(5));
            listaidregistrosrecuperados.add(cursorRecuperados.getString(0));
        }
    }


    private class EnviodeDatos extends AsyncTask<Void, Integer, Boolean> {
        protected Boolean doInBackground(Void... voids) {
            try {
                SQLiteDatabase db = myDB.getWritableDatabase();
                Cursor cursor = db.rawQuery("SELECT id, productor_id, desde, hasta, cantidad, envases_id FROM registros_devolucion", null);

                SharedPreferences preferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
                int user_id = preferences.getInt("id", 0);


                while (cursor.moveToNext()) {

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-dd-MM");

                    int id = cursor.getInt(0);
                    String productor_id = cursor.getString(1);
                    String desde = simpleDateFormat.format(new Date(cursor.getString(2)));
                    String hasta = simpleDateFormat.format(new Date(cursor.getString(3)));
                    String cantidad = cursor.getString(4);
                    String envases_id = cursor.getString(5);

                    final RequestParams params = new RequestParams();

                    params.put("id", id);
                    params.put("productor_id", productor_id);
                    params.put("desde", desde);
                    params.put("hasta", hasta);
                    params.put("cantidad", cantidad);
                    params.put("envases_id", envases_id);
                    params.put("user_id", user_id);

                    Handler handler = new Handler(Looper.getMainLooper());
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            AsyncHttpClient client = new AsyncHttpClient();
                            client.post(PROCESAR_URL_DEVOLUCION, params, new AsyncHttpResponseHandler() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    if (statusCode == 200) {
                                        Log.i("200", new String(responseBody,StandardCharsets.UTF_8));
                                        db.execSQL("DELETE FROM registros_devolucion WHERE id ='" + id + "'");
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


                Cursor cursorRecuperar = db.rawQuery("SELECT id, productor_id, desde, hasta, cantidad, envases_id FROM registros_recuperar", null);

                while (cursorRecuperar.moveToNext()) {


                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-dd-MM");


                    int id = cursorRecuperar.getInt(0);
                    String productor_id = cursorRecuperar.getString(1);
                    String desde = simpleDateFormat.format(new Date(cursorRecuperar.getString(2)));
                    String hasta = simpleDateFormat.format(new Date(cursorRecuperar.getString(3)));
                    String cantidad = cursorRecuperar.getString(4);
                    String envases_id = cursorRecuperar.getString(5);

                    final RequestParams params = new RequestParams();

                    params.put("id", id);
                    params.put("productor_id", productor_id);
                    params.put("desde", desde);
                    params.put("hasta", hasta);
                    params.put("cantidad", cantidad);
                    params.put("envases_id", envases_id);
                    params.put("user_id", user_id);

                    Handler handler = new Handler(Looper.getMainLooper());
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            AsyncHttpClient client = new AsyncHttpClient();
                            client.post(PROCESAR_URL_RECUPERAR, params, new AsyncHttpResponseHandler() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    if (statusCode == 200) {
                                        Log.i("200", new String(responseBody,StandardCharsets.UTF_8));
                                        db.execSQL("DELETE FROM registros_recuperar WHERE id ='" + id + "'");
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

            Fragment fragment_actual = new Listado_cc_fragment();
            Fragment fragment = new Listado_cc_fragment();
            getFragmentManager().beginTransaction().remove(fragment_actual).commit();
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        }
    }


    private void vaciarRegistro(String id){
        SQLiteDatabase db = myDB.getWritableDatabase();
        Cursor cursorDevoluciones = db.rawQuery("SELECT ep.id, ep.productor_id, ep.envases_id, ep.cantidad, ep.desde, ep.hasta  FROM registros_devolucion ep WHERE ep.id = '" + id + "' ",null);

        int productorid = 0;
        int cantidad = 0;
        int envasedid = 0;
        String desde = "";
        String hasta = "";

        while(cursorDevoluciones.moveToNext()) {
            productorid = cursorDevoluciones.getInt(1);
            cantidad = cursorDevoluciones.getInt(3);
            envasedid = cursorDevoluciones.getInt(2);
            desde = cursorDevoluciones.getString(4);
            hasta = cursorDevoluciones.getString(5);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-dd-MM");
        Log.i("DESDE", simpleDateFormat.format(new Date(desde)));
        Log.i("HASTA", simpleDateFormat.format(new Date(hasta)));
        Cursor cursorPendientes = db.rawQuery("SELECT id , ep.cantidad FROM envasesPendientes ep WHERE productor_id ='" + productorid + "' AND fecha >= date('" + simpleDateFormat.format(new Date(desde.toString())) + "') AND fecha <= date('" + simpleDateFormat.format(new Date(hasta.toString())) + "') AND envase_id = '" + envasedid + "' ",null);


        int idregistroactualizar = 0;
        int nuevacantidad = 0;
        while (cursorPendientes.moveToNext()) {
            idregistroactualizar = cursorPendientes.getInt(0);
            nuevacantidad = cantidad + cursorPendientes.getInt(1);
        }

        db.execSQL("UPDATE envasesPendientes SET cantidad = '" + nuevacantidad + "' WHERE id ='" + idregistroactualizar + "' ");
        db.execSQL("DELETE FROM registros_devolucion WHERE id ='"+id+"'");
    }


    private void vaciarRegistroRecuperar(String id){
        SQLiteDatabase db = myDB.getWritableDatabase();
        Cursor cursorRecuperacion = db.rawQuery("SELECT ep.id, ep.productor_id, ep.envases_id, ep.cantidad, ep.desde, ep.hasta  FROM registros_recuperar ep WHERE ep.id = '" + id + "' ",null);

        int productorid = 0;
        int cantidad = 0;
        int envasedid = 0;
        String desde = "";
        String hasta = "";

        while(cursorRecuperacion.moveToNext()) {
            productorid = cursorRecuperacion.getInt(1);
            cantidad = cursorRecuperacion.getInt(3);
            envasedid = cursorRecuperacion.getInt(2);
            desde = cursorRecuperacion.getString(4);
            hasta = cursorRecuperacion.getString(5);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-dd-MM");
        Log.i("DESDE", simpleDateFormat.format(new Date(desde)));
        Log.i("HASTA", simpleDateFormat.format(new Date(hasta)));
        Cursor cursorRecuperar = db.rawQuery("SELECT id , ep.cantidad FROM envasesRecuperar ep WHERE productor_id ='" + productorid + "' AND fecha >= date('" + simpleDateFormat.format(new Date(desde.toString())) + "') AND fecha <= date('" + simpleDateFormat.format(new Date(hasta.toString())) + "') AND envase_id = '" + envasedid + "' ",null);


        int idregistroactualizar = 0;
        int nuevacantidad = 0;
        while (cursorRecuperar.moveToNext()) {
            idregistroactualizar = cursorRecuperar.getInt(0);
            nuevacantidad = cantidad + cursorRecuperar.getInt(1);
        }


        db.execSQL("UPDATE envasesRecuperar SET cantidad = '" + nuevacantidad + "' WHERE id ='" + idregistroactualizar + "' ");
        db.execSQL("DELETE FROM registros_recuperar WHERE id ='"+id+"'");
    }


}