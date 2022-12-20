package cl.app.montes.view.drawer.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cl.app.montes.R;
import cl.app.montes.db.DatabaseHelper;

public class Cc_fragment extends Fragment {

    SearchableSpinner productor;
    DatabaseHelper myDB;
    ArrayList<String>productoresid = new ArrayList<>();
    TextView dateDesde, dateHasta;
    DatePickerDialog datePickerDialog;
    Button filtrar, devolver, recuperar;
    Spinner spinnerEnvaseDevolver, spinnerEnvaseRecuperar;
    ArrayList<String>idenvasesdevolver = new ArrayList<>();
    ArrayList<String>idenvasesrecuperar = new ArrayList<>();
    TextInputEditText cantidaddevolver, cantidadrecuperar;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cc_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Cuenta corriente envases");
        productor = (SearchableSpinner)getView().findViewById(R.id.productorSpinner);
        dateDesde = (TextView)getView().findViewById(R.id.dateDesde);
        dateHasta = (TextView)getView().findViewById(R.id.dateHasta);
        spinnerEnvaseDevolver = (Spinner)getView().findViewById(R.id.envaseDevolverSpinner);
        spinnerEnvaseRecuperar = (Spinner)getView().findViewById(R.id.envaseRecuperarSpinner);
        cantidaddevolver = (TextInputEditText)getView().findViewById(R.id.txtcantidadenvasesdevolver);
        cantidadrecuperar = (TextInputEditText)getView().findViewById(R.id.txtcantidadenvasesrecuperar);
        myDB = new DatabaseHelper(getActivity().getApplicationContext());
        filtrar = (Button)getView().findViewById(R.id.btnFiltrar);
        devolver = (Button)getView().findViewById(R.id.btnDevolver);
        recuperar = (Button)getView().findViewById(R.id.btnRecuperar);

        cargarproductor();
        spinnerEnvaseDevolver.setEnabled(false);
        spinnerEnvaseRecuperar.setEnabled(false);
        cantidaddevolver.setEnabled(false);
        cantidadrecuperar.setEnabled(false);
        ArrayList<String>envasesdevolver = new ArrayList<>();
        envasesdevolver.add("-------");
        ArrayList<String>envasesrecuperar = new ArrayList<>();
        envasesrecuperar.add("-------");

        ArrayAdapter adapterTara = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, envasesdevolver);
        adapterTara.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEnvaseDevolver.setAdapter(adapterTara);
        idenvasesdevolver.add("0");

        ArrayAdapter adapterrecuperar = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, envasesrecuperar);
        adapterrecuperar.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEnvaseRecuperar.setAdapter(adapterrecuperar);
        idenvasesrecuperar.add("0");


        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

        dateDesde.setText("9/9/2022");
        dateHasta.setText(mDay + "/"
                + (mMonth + 1) + "/" + mYear);
        productor.setTitle("Selecciona el productor");
        productor.setPositiveButton("OK");

        dateDesde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(getView().getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text



                                dateDesde.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        dateHasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(getView().getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                dateHasta.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        filtrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int posicionproductor = productor.getSelectedItemPosition();
                String idproductor = productoresid.get(posicionproductor);

                envasesdevolver.removeAll(envasesdevolver);
                envasesdevolver.add("-------");
                idenvasesdevolver.removeAll(idenvasesdevolver);
                idenvasesdevolver.add("0");

                envasesrecuperar.removeAll(envasesrecuperar);
                envasesrecuperar.add("-------");
                idenvasesrecuperar.removeAll(idenvasesrecuperar);
                idenvasesrecuperar.add("0");

                if (idproductor.equals("0")){
                    TextView errorText = (TextView)productor.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("Selecciona el productor");
                } else {
                    SQLiteDatabase db = myDB.getReadableDatabase();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-dd-MM");
                    Log.i("DESDE", simpleDateFormat.format(new Date(dateDesde.getText().toString())));
                    Log.i("HASTA", simpleDateFormat.format(new Date(dateHasta.getText().toString())));
                    Cursor cursorPendientes = db.rawQuery("SELECT ep.id, ep.productor_id, ep.envase_id, ep.cantidad, ep.fecha,prod.razon_social, ta.name_envase, ta.envase_id FROM envasesPendientes ep INNER JOIN productores prod ON ep.productor_id = prod.id INNER JOIN tara ta ON ep.envase_id = ta.envase_id WHERE productor_id ='" + idproductor + "' AND fecha >= date('" + simpleDateFormat.format(new Date(dateDesde.getText().toString())) + "') AND fecha <= date('" + simpleDateFormat.format(new Date(dateHasta.getText().toString())) + "') ",null);
                    Cursor cursorRecuperar = db.rawQuery("SELECT ep.id, ep.productor_id, ep.envase_id, ep.cantidad, ep.fecha,prod.razon_social, ta.name_envase, ta.envase_id FROM envasesRecuperar ep INNER JOIN productores prod ON ep.productor_id = prod.id INNER JOIN tara ta ON ep.envase_id = ta.envase_id WHERE productor_id ='" + idproductor + "' AND fecha >= date('" + simpleDateFormat.format(new Date(dateDesde.getText().toString())) + "') AND fecha <= date('" + simpleDateFormat.format(new Date(dateHasta.getText().toString())) + "') ",null);

                    String tvpendientesText = "";
                    String tvrecuperarText = "";
                    int totalpendientes = 0;
                    int totalrecuperar = 0;

                    while (cursorPendientes.moveToNext()){
                        String nombre_envase = cursorPendientes.getString(6);
                        int cantidad_pendiente = cursorPendientes.getInt(3);
                        String id_envase_devolver = String.valueOf(cursorPendientes.getInt(7));
                        totalpendientes = totalpendientes + cantidad_pendiente;
                        tvpendientesText += nombre_envase + " - " + cantidad_pendiente + System.getProperty("line.separator");

                        envasesdevolver.add(nombre_envase);
                        idenvasesdevolver.add(id_envase_devolver);


                    }

                    ArrayAdapter adapterTara = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, envasesdevolver);
                    adapterTara.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerEnvaseDevolver.setAdapter(adapterTara);

                    while (cursorRecuperar.moveToNext()){
                        String nombre_envase = cursorRecuperar.getString(6);
                        int cantidad_recuperar = cursorRecuperar.getInt(3);
                        String id_envase_recuperar = String.valueOf(cursorRecuperar.getInt(7));
                        totalrecuperar = totalrecuperar + cantidad_recuperar;
                        tvrecuperarText += nombre_envase + " - " + cantidad_recuperar + System.getProperty("line.separator");

                        envasesrecuperar.add(nombre_envase);
                        idenvasesrecuperar.add(id_envase_recuperar);
                    }

                    ArrayAdapter adapterRecuperar = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, envasesrecuperar);
                    adapterRecuperar.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerEnvaseRecuperar.setAdapter(adapterRecuperar);


                    TextView tvtipopend = (TextView)getView().findViewById(R.id.tvtipopendiente);
                    tvtipopend.setText(tvpendientesText);
                    TextView tvpendientesDevolucion = (TextView)getView().findViewById(R.id.tvpendientesDevolucion);
                    tvpendientesDevolucion.setText("Pendientes devolución: " + totalpendientes);
                    TextView tvtotalPendientes = (TextView)getView().findViewById(R.id.tvtotalPendientes);
                    tvtotalPendientes.setText("TOTAL: " + totalpendientes);

                    TextView tvtiporecu = (TextView)getView().findViewById(R.id.tvtiporecuperar);
                    tvtiporecu.setText(tvrecuperarText);
                    TextView tvpendientesRecuperar = (TextView)getView().findViewById(R.id.tvpendientesRecuperar);
                    tvpendientesRecuperar.setText("Pendientes recuperar: " + totalrecuperar);
                    TextView tvtotalRecuperar = (TextView)getView().findViewById(R.id.tvtotalRecuperar);
                    tvtotalRecuperar.setText("TOTAL: " + totalrecuperar);

                    if (cursorPendientes.getCount() == 0 && cursorRecuperar.getCount() == 0) {
                        Toast.makeText(getActivity().getApplicationContext(), "No se encontraron datos", Toast.LENGTH_SHORT).show();
                    }
                    if (cursorPendientes.getCount() > 0) {
                        spinnerEnvaseDevolver.setEnabled(true);
                        cantidaddevolver.setEnabled(true);
                    }
                    if (cursorRecuperar.getCount() > 0) {
                        spinnerEnvaseRecuperar.setEnabled(true);
                        cantidadrecuperar.setEnabled(true);
                    }
                }
            }
        });

        devolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int posicionproductor = productor.getSelectedItemPosition();
                String idproductor = productoresid.get(posicionproductor);

                if (idproductor.equals("0")){
                    TextView errorText = (TextView)productor.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("Selecciona el productor");
                }

                int posicionenvasedevolver = spinnerEnvaseDevolver.getSelectedItemPosition();
                String idenvasedevolver = idenvasesdevolver.get(posicionenvasedevolver);

                if (idenvasedevolver.equals("0")){
                    TextView errorText = (TextView)spinnerEnvaseDevolver.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("Selecciona el envase");
                }

                if (cantidaddevolver.getText().toString().equals("")) {
                    cantidaddevolver.setError("Ingresa cantidad");
                }



                if(!idenvasedevolver.equals("0") && !idproductor.equals("0") &&
                        !cantidaddevolver.getText().toString().equals("")) {

                    if (Integer.parseInt(cantidaddevolver.getText().toString()) <= 0){
                        Toast.makeText(getActivity().getApplicationContext(), "La cantidad a recuperar debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                        cantidaddevolver.setError("Cantidad debe ser mayor a 0");
                    } else {
                        SQLiteDatabase db = myDB.getReadableDatabase();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-dd-MM");
                        Log.i("DESDE", simpleDateFormat.format(new Date(dateDesde.getText().toString())));
                        Log.i("HASTA", simpleDateFormat.format(new Date(dateHasta.getText().toString())));
                        Cursor cursorInfo = db.rawQuery("SELECT id , ep.cantidad FROM envasesPendientes ep WHERE productor_id ='" + idproductor + "' AND fecha >= date('" + simpleDateFormat.format(new Date(dateDesde.getText().toString())) + "') AND fecha <= date('" + simpleDateFormat.format(new Date(dateHasta.getText().toString())) + "') AND envase_id = '" + idenvasedevolver + "' ",null);

                        int cant = 0;

                        while (cursorInfo.moveToNext()) {
                            cant = cursorInfo.getInt(1);
                        }

                        if (Integer.parseInt(cantidaddevolver.getText().toString()) > cant ) {
                            Toast.makeText(getActivity().getApplicationContext(), "La cantidad a devolver no puede ser mayor a la pendiente " + cant, Toast.LENGTH_SHORT).show();
                            cantidaddevolver.setError("Cantidad mayor a lo pendiente: " + cant);
                        } else {

                            progressDialog = new ProgressDialog(getView().getContext());
                            progressDialog.setTitle("Guardando registro de devolución");
                            progressDialog.setMessage("Espere un momento...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            boolean resultado =  myDB.guardarRegistrosDevolucion(Integer.parseInt(idproductor),dateDesde.getText().toString(), dateHasta.getText().toString(), Integer.parseInt(cantidaddevolver.getText().toString()), Integer.parseInt(idenvasedevolver));

                            if (resultado){


                                Log.i("DESDE", simpleDateFormat.format(new Date(dateDesde.getText().toString())));
                                Log.i("HASTA", simpleDateFormat.format(new Date(dateHasta.getText().toString())));

                                Cursor cursorPendientes = db.rawQuery("SELECT id , ep.cantidad FROM envasesPendientes ep WHERE productor_id ='" + idproductor + "' AND fecha >= date('" + simpleDateFormat.format(new Date(dateDesde.getText().toString())) + "') AND fecha <= date('" + simpleDateFormat.format(new Date(dateHasta.getText().toString())) + "') AND envase_id = '" + idenvasedevolver + "' ",null);

                                int nueva_cantidad = 0;
                                int idregdev = 0;
                                while (cursorPendientes.moveToNext()){
                                    int cantidad_pendiente = cursorPendientes.getInt(1);
                                    idregdev = cursorPendientes.getInt(0);

                                    nueva_cantidad = cantidad_pendiente - Integer.parseInt(cantidaddevolver.getText().toString());

                                }

                                db.execSQL("UPDATE envasesPendientes SET cantidad = '" + nueva_cantidad + "' WHERE id ='" + idregdev + "' ");

                                progressDialog.dismiss();
                                Toast.makeText(getActivity().getApplicationContext(), "Devolución guardada correctamente", Toast.LENGTH_SHORT).show();
                                Fragment fragment = new Formulario_fragment();

                                getFragmentManager().beginTransaction().add(R.id.content_frame, fragment).commit();
                            }else{
                                Toast.makeText(getActivity().getApplicationContext(),"Error. No se pudo guardar el registro",Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    }
                }
            }
        });

        recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int posicionproductor = productor.getSelectedItemPosition();
                String idproductor = productoresid.get(posicionproductor);

                if (idproductor.equals("0")){
                    TextView errorText = (TextView)productor.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("Selecciona el productor");
                }

                int posicionenvaserecuperar = spinnerEnvaseRecuperar.getSelectedItemPosition();
                String idenvaserecuperar = idenvasesrecuperar.get(posicionenvaserecuperar);

                if (idenvaserecuperar.equals("0")){
                    TextView errorText = (TextView)spinnerEnvaseRecuperar.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("Selecciona el envase");
                }

                if (cantidadrecuperar.getText().toString().equals("")) {
                    cantidadrecuperar.setError("Ingresa cantidad");
                }

                if(!idenvaserecuperar.equals("0") && !idproductor.equals("0") &&
                        !cantidadrecuperar.getText().toString().equals("")) {

                    if (Integer.parseInt(cantidadrecuperar.getText().toString()) <= 0){
                        Toast.makeText(getActivity().getApplicationContext(), "La cantidad a recuperar debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                        cantidadrecuperar.setError("Cantidad debe ser mayor a 0");
                    } else {
                        SQLiteDatabase db = myDB.getReadableDatabase();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-dd-MM");
                        Log.i("DESDE", simpleDateFormat.format(new Date(dateDesde.getText().toString())));
                        Log.i("HASTA", simpleDateFormat.format(new Date(dateHasta.getText().toString())));
                        Cursor cursorInfo = db.rawQuery("SELECT id , ep.cantidad FROM envasesRecuperar ep WHERE productor_id ='" + idproductor + "' AND fecha >= date('" + simpleDateFormat.format(new Date(dateDesde.getText().toString())) + "') AND fecha <= date('" + simpleDateFormat.format(new Date(dateHasta.getText().toString())) + "') AND envase_id = '" + idenvaserecuperar + "' ",null);

                        int cant = 0;

                        while (cursorInfo.moveToNext()) {
                            cant = cursorInfo.getInt(1);
                        }

                        if (Integer.parseInt(cantidadrecuperar.getText().toString()) > cant ) {
                            Toast.makeText(getActivity().getApplicationContext(), "La cantidad a recuperar no puede ser mayor a la pendiente " + cant, Toast.LENGTH_SHORT).show();
                            cantidadrecuperar.setError("Cantidad mayor a lo pendiente: " + cant);
                        } else {
                            progressDialog = new ProgressDialog(getView().getContext());
                            progressDialog.setTitle("Guardando registro de recuperación");
                            progressDialog.setMessage("Espere un momento...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            boolean resultado =  myDB.guardarRegistrosRecuperar(Integer.parseInt(idproductor),dateDesde.getText().toString(), dateHasta.getText().toString(), Integer.parseInt(cantidadrecuperar.getText().toString()), Integer.parseInt(idenvaserecuperar));

                            if (resultado){

                                Log.i("DESDE", simpleDateFormat.format(new Date(dateDesde.getText().toString())));
                                Log.i("HASTA", simpleDateFormat.format(new Date(dateHasta.getText().toString())));
                                Cursor cursorRecuperar = db.rawQuery("SELECT id , ep.cantidad FROM envasesRecuperar ep WHERE productor_id ='" + idproductor + "' AND fecha >= date('" + simpleDateFormat.format(new Date(dateDesde.getText().toString())) + "') AND fecha <= date('" + simpleDateFormat.format(new Date(dateHasta.getText().toString())) + "') AND envase_id = '" + idenvaserecuperar + "' ",null);

                                int nueva_cantidad = 0;
                                int idregrec = 0;
                                while (cursorRecuperar.moveToNext()){
                                    int cantidad_pendiente = cursorRecuperar.getInt(1);
                                    idregrec = cursorRecuperar.getInt(0);

                                    nueva_cantidad = cantidad_pendiente - Integer.parseInt(cantidadrecuperar.getText().toString());

                                }

                                db.execSQL("UPDATE envasesRecuperar SET cantidad = '" + nueva_cantidad + "' WHERE id ='" + idregrec + "' ");

                                progressDialog.dismiss();
                                Toast.makeText(getActivity().getApplicationContext(), "Recuperación guardada correctamente", Toast.LENGTH_SHORT).show();
                                Fragment fragment = new Formulario_fragment();

                                getFragmentManager().beginTransaction().add(R.id.content_frame, fragment).commit();
                            }else{
                                Toast.makeText(getActivity().getApplicationContext(),"Error. No se pudo guardar el registro",Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    }
                }
            }
        });
    }


    private void cargarproductor(){
        //SPINNER PRODUCTOR
        ArrayList<String> productores = new ArrayList<>();
        productores.add("Seleccione productor");
        productoresid.add("0");

        SQLiteDatabase db = myDB.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT id, razon_social FROM productores ORDER BY razon_social ASC", null);
        while (c.moveToNext()){
            productores.add(c.getString(c.getColumnIndex("razon_social")));
            productoresid.add(c.getString(c.getColumnIndex("id")));
        }
        ArrayAdapter adapterProductores = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, productores);
        adapterProductores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productor.setAdapter(adapterProductores);

        ////
    }
}
