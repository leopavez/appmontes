package cl.app.montes.view.drawer.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.database.CursorWindowCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cl.app.montes.R;
import cl.app.montes.clases.Productores;
import cl.app.montes.clases.ProductoresSearchAdapter;
import cl.app.montes.db.DatabaseHelper;

public class Formulario_fragment extends Fragment {


    SearchableSpinner productor;
    Spinner recorridospinner, productos, variedad, tara;
    TextInputEditText cantidad_envase, kilos_brutos, band_p, band_e, precio_usuario;
    Button guardar;
    DatabaseHelper myDB;
    TextView kilos_netos, total;
    ProgressDialog progressDialog;
    TextInputLayout cantidadlayout, bandejas_pendienteslayout, bandejas_entregadaslayout, kilos_brutoslayout, precio_usuariolayout;

    ArrayList<String>recorridoid = new ArrayList<>();
    ArrayList<String>productoresid = new ArrayList<>();
    ArrayList<String>productoid = new ArrayList<>();
    ArrayList<String>variedadid = new ArrayList<>();
    ArrayList<String>taraid = new ArrayList<>();
    ArrayList<String>tarapeso = new ArrayList<>();
    ArrayList<String>precioproducto = new ArrayList<>();
    String k_netos = "";
    String total__ = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_formulario_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Nuevo recorrido");
        recorridospinner = (Spinner)getView().findViewById(R.id.recorridoSpinner);
        productor = (SearchableSpinner)getView().findViewById(R.id.productorSpinner);
        productos = (Spinner)getView().findViewById(R.id.productoSpinner);
        variedad = (Spinner)getView().findViewById(R.id.especieSpinner);
        tara = (Spinner)getView().findViewById(R.id.taraSpinner);
        cantidad_envase = (TextInputEditText)getView().findViewById(R.id.txtcantidadenvase);
        kilos_brutos = (TextInputEditText)getView().findViewById(R.id.txtkilosbrutos);
        guardar = (Button)getView().findViewById(R.id.btnguardar);
        kilos_netos = (TextView)getView().findViewById(R.id.textviewkilosnetos);
        total = (TextView)getView().findViewById(R.id.texviewtotal);
        band_e = (TextInputEditText)getView().findViewById(R.id.txtbandejas_entregadas);
        band_p = (TextInputEditText)getView().findViewById(R.id.txtbandejas_pendientes);
        precio_usuario = (TextInputEditText)getView().findViewById(R.id.txtpreciousuario);
        precio_usuariolayout = (TextInputLayout)getView().findViewById(R.id.textinputlayoutprecio_usuario);
        cantidadlayout = (TextInputLayout)getView().findViewById(R.id.textinputLayoutcantidad);
        bandejas_entregadaslayout = (TextInputLayout)getView().findViewById(R.id.textinputlayoutbandejas_entregadas);
        bandejas_pendienteslayout = (TextInputLayout)getView().findViewById(R.id.textinputlayoutbandejas_pendientes);
        kilos_brutoslayout = (TextInputLayout)getView().findViewById(R.id.textinputlayoutkilos_brutos);
        myDB = new DatabaseHelper(getActivity().getApplicationContext());


        //ADAPTADORES DE SPINNER
        cargarrecorrido();
        cargarproductor();
        cargarproducto();
        cargarvariedad();
        cargarTara();

        productor.setTitle("Selecciona el productor");
        productor.setPositiveButton("OK");

        productos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int posicionprod = productos.getSelectedItemPosition();
                String precioprod = precioproducto.get(posicionprod);
                precio_usuario.setText(precioprod);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        band_p.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!band_p.getText().toString().equals("")){
                    bandejas_pendienteslayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        band_e.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!band_e.getText().toString().equals("")){
                    bandejas_entregadaslayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tara.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int posiciontara = tara.getSelectedItemPosition();
                String pesotara = tarapeso.get(posiciontara);
                Double peso = Double.parseDouble(pesotara.toString());
                String idtara = taraid.get(posiciontara);

                if(!cantidad_envase.getText().toString().equals("") && !kilos_brutos.getText().toString().equals("")
                        && !idtara.equals("0") && !precio_usuario.equals("")){
                    Double kbrutos = Double.parseDouble(kilos_brutos.getText().toString());
                    int cant = Integer.parseInt(cantidad_envase.getText().toString());


                    int posicionproducto = productos.getSelectedItemPosition();
                    String precioprod = precioproducto.get(posicionproducto);
                    int preciopr = Integer.parseInt(precio_usuario.getText().toString());

                    Double knetos = kbrutos - (cant * peso);
                    Double total_ =  knetos * preciopr;

                    DecimalFormat format = new DecimalFormat();
                    format.setMaximumFractionDigits(2);

                    kilos_netos.setText("Kilos netos: "+format.format(knetos).toString());
                    k_netos = String.valueOf(format.format(knetos).toString()).toString();
                    total.setText("Total: "+  format.format(total_).toString());
                    total__ = String.valueOf(format.format(total_).toString()).toString();
                }else{
                    kilos_netos.setText("Kilos netos: 0");
                    total.setText("Total: 0");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        precio_usuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int posiciontara = tara.getSelectedItemPosition();
                String pesotara = tarapeso.get(posiciontara);
                Double peso = Double.parseDouble(pesotara.toString());
                String idtara = taraid.get(posiciontara);

                if(!cantidad_envase.getText().toString().equals("") && !kilos_brutos.getText().toString().equals("")
                        && !idtara.equals("0") && !precio_usuario.equals("")){

                    Double kbrutos = Double.parseDouble(kilos_brutos.getText().toString());
                    int cant = Integer.parseInt(cantidad_envase.getText().toString());


                    int posicionproducto = productos.getSelectedItemPosition();
                    String precioprod = precioproducto.get(posicionproducto);
                    int preciopr = Integer.parseInt(precio_usuario.getText().toString());

                    Double knetos = kbrutos - (cant * peso);
                    Double total_ =  knetos * preciopr;

                    DecimalFormat format = new DecimalFormat();
                    format.setMaximumFractionDigits(2);

                    kilos_netos.setText("Kilos netos: "+format.format(knetos).toString());
                    k_netos = String.valueOf(format.format(knetos).toString()).toString();
                    total.setText("Total: "+  format.format(total_).toString());
                    total__ = String.valueOf(format.format(total_).toString()).toString();
                }else{
                    kilos_netos.setText("Kilos netos: 0");
                    total.setText("Total: 0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(precio_usuario.getText().toString().equals("")){
                    precio_usuario.setText("0");
                }
            }
        });
        cantidad_envase.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                int posiciontara = tara.getSelectedItemPosition();
                String pesotara = tarapeso.get(posiciontara);
                Double peso = Double.parseDouble(pesotara.toString());
                String idtara = taraid.get(posiciontara);

                if (!cantidad_envase.getText().toString().equals("")){
                    cantidadlayout.setErrorEnabled(false);
                }

                if(!cantidad_envase.getText().toString().equals("") && !kilos_brutos.getText().toString().equals("")
                && !idtara.equals("0") && !precio_usuario.equals("")){

                    Double kbrutos = Double.parseDouble(kilos_brutos.getText().toString());
                    int cant = Integer.parseInt(cantidad_envase.getText().toString());


                    int posicionproducto = productos.getSelectedItemPosition();
                    String precioprod = precioproducto.get(posicionproducto);
                    int preciopr = Integer.parseInt(precio_usuario.getText().toString());

                    Double knetos = kbrutos - (cant * peso);
                    Double total_ =  knetos * preciopr;

                    DecimalFormat format = new DecimalFormat();
                    format.setMaximumFractionDigits(2);

                    kilos_netos.setText("Kilos netos: "+format.format(knetos).toString());
                    k_netos = String.valueOf(format.format(knetos).toString()).toString();
                    total.setText("Total: "+  format.format(total_).toString());
                    total__ = String.valueOf(format.format(total_).toString()).toString();
                }else{
                    kilos_netos.setText("Kilos netos: 0");
                    total.setText("Total: 0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        kilos_brutos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                int posiciontara = tara.getSelectedItemPosition();
                String pesotara = tarapeso.get(posiciontara);
                Double peso = Double.parseDouble(pesotara.toString());
                String idtara = taraid.get(posiciontara);

                if (!kilos_brutos.getText().toString().equals("")){
                    kilos_brutoslayout.setErrorEnabled(false);
                }

                if(!cantidad_envase.getText().toString().equals("") && !kilos_brutos.getText().toString().equals("")
                        && !idtara.equals("0") && !precio_usuario.equals("")){
                    Double kbrutos = Double.parseDouble(kilos_brutos.getText().toString());
                    int cant = Integer.parseInt(cantidad_envase.getText().toString());


                    int posicionproducto = productos.getSelectedItemPosition();
                    String precioprod = precioproducto.get(posicionproducto);
                    int preciopr = Integer.parseInt(precio_usuario.getText().toString());

                    Double knetos = kbrutos - (cant * peso);
                    Double total_ =  knetos * preciopr;

                    DecimalFormat format = new DecimalFormat();
                    format.setMaximumFractionDigits(2);

                    kilos_netos.setText("Kilos netos: "+format.format(knetos).toString());
                    k_netos = String.valueOf(format.format(knetos).toString()).toString();
                    total.setText("Total: "+  format.format(total_).toString());
                    total__ = String.valueOf(format.format(total_).toString()).toString();
                }else{
                    kilos_netos.setText("Kilos netos: 0");
                    total.setText("Total: 0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int posicionrecorrido = recorridospinner.getSelectedItemPosition();
                String idrecorrido = recorridoid.get(posicionrecorrido);

                int posicionproductor = productor.getSelectedItemPosition();
                String idproductor = productoresid.get(posicionproductor);

                int posicionproductos = productos.getSelectedItemPosition();
                String idproductos = productoid.get(posicionproductos);

                int posicionvariedad = variedad.getSelectedItemPosition();
                String idvariedad = variedadid.get(posicionvariedad);

                int posiciontara = tara.getSelectedItemPosition();
                String idtara = taraid.get(posiciontara);

                String cantidad = cantidad_envase.getText().toString();
                String kilosb = kilos_brutos.getText().toString();

                String bpendientes = band_p.getText().toString();
                String bentregadas = band_e.getText().toString();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat horaformat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                Date date = new Date();
                String fecha = dateFormat.format(date);
                String hora = horaformat.format(date);

                if (idrecorrido.equals("0") || idproductos.equals("0")
                || idvariedad.equals("0") || idtara.equals("0") || cantidad.equals("") || kilosb.equals("") || bpendientes.equals("")
                || bentregadas.equals("") || idproductor.equals("0")){

                    if (idrecorrido.equals("0")){
                        TextView errorText = (TextView)recorridospinner.getSelectedView();
                        errorText.setError("");
                        errorText.setTextColor(Color.RED);//just to highlight that this is an error
                        errorText.setText("Seleccione recorrido");//changes the selected item text to this
                    }
                    if (idproductos.equals("0")){
                        TextView errorText = (TextView)productos.getSelectedView();
                        errorText.setError("");
                        errorText.setTextColor(Color.RED);//just to highlight that this is an error
                        errorText.setText("Seleccione producto");//changes the selected item text to this
                    }
                    if (idvariedad.equals("0")){
                        TextView errorText = (TextView)variedad.getSelectedView();
                        errorText.setError("");
                        errorText.setTextColor(Color.RED);//just to highlight that this is an error
                        errorText.setText("Seleccione variedad");//changes the selected item text to this
                    }
                    if (idtara.equals("0")){
                        TextView errorText = (TextView)tara.getSelectedView();
                        errorText.setError("");
                        errorText.setTextColor(Color.RED);//just to highlight that this is an error
                        errorText.setText("Seleccione tara");//changes the selected item text to this
                    }
                    if (cantidad.equals("")){
                        cantidadlayout.setErrorEnabled(true);
                        cantidadlayout.getEditText().setText("");
                        cantidadlayout.setError("Ingrese la cantidad");
                    }
                    if (kilosb.equals("")){
                        kilos_brutoslayout.setErrorEnabled(true);
                        kilos_brutoslayout.setError("Ingrese kilos brutos");
                    }
                    if (bpendientes.equals("")){
                        bandejas_pendienteslayout.setErrorEnabled(true);
                        bandejas_pendienteslayout.setError("Ingrese bandejas pendientes");
                    }
                    if (bentregadas.equals("")){
                        bandejas_entregadaslayout.setErrorEnabled(true);
                        bandejas_entregadaslayout.setError("Ingrese bandejas entregadas");
                    }

                    if(idproductor.equals("0")){
                        TextView errorText = (TextView)productor.getSelectedView();
                        errorText.setError("");
                        errorText.setTextColor(Color.RED);//just to highlight that this is an error
                        errorText.setText("Seleccione productor");//changes the selected item text to this
                    }
                    
                    Toast.makeText(getActivity().getApplicationContext(), "Falta ingresar datos ", Toast.LENGTH_SHORT).show();
                }else{
                    //
                    progressDialog = new ProgressDialog(getView().getContext());
                    progressDialog.setTitle("Guardando recorrido");
                    progressDialog.setMessage("Espere un momento...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                   boolean resultado =  myDB.guardarRegistros(1,fecha,hora,idrecorrido,idproductor,idproductos,idvariedad,
                            idtara,"bandeja",cantidad,kilosb,k_netos,total__,bpendientes, bentregadas, precio_usuario.toString());

                   if (resultado){
                       progressDialog.dismiss();
                       Toast.makeText(getActivity().getApplicationContext(), "Recorrido guardado correctamente", Toast.LENGTH_SHORT).show();
                       Fragment fragment = new Formulario_fragment();
                       getFragmentManager().beginTransaction().add(R.id.content_frame, fragment).commit();
                   }else{
                       Toast.makeText(getActivity().getApplicationContext(),"Error. No se pudo guardar el registro",Toast.LENGTH_SHORT).show();
                       progressDialog.dismiss();
                   }
                }
            }
        });
    }

    private void cargarrecorrido(){
        //SPINNER RECORRIDO
        ArrayList<String>recorrido = new ArrayList<>();
        recorrido.add("Seleccione recorrido");
        recorridoid.add("0");

        SQLiteDatabase db = myDB.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT id, name FROM recorrido ORDER BY name ASC", null);
        while (c.moveToNext()){
            recorrido.add(c.getString(c.getColumnIndex("name")));
            recorridoid.add(c.getString(c.getColumnIndex("id")));
        }
        ArrayAdapter adapterRecorrido = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, recorrido);
        adapterRecorrido.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recorridospinner.setAdapter(adapterRecorrido);
    }

    private void cargarproductor(){
        //SPINNER PRODUCTOR
        ArrayList<String>productores = new ArrayList<>();
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

    private void cargarproducto(){
        //SPINNER PRODUCTO
        ArrayList<String>producto = new ArrayList<>();
        producto.add("Seleccione producto");
        productoid.add("0");
        precioproducto.add("0");

        SQLiteDatabase db = myDB.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT id, name, precio FROM productos ORDER BY name ASC", null);
        while (c.moveToNext()){
            String nombre = c.getString(c.getColumnIndex("name"));
            String precio = "- $ "+c.getString(c.getColumnIndex("precio"));
            String nombreprecio = nombre + " " + precio;
            producto.add(nombreprecio);
            productoid.add(c.getString(c.getColumnIndex("id")));
            precioproducto.add(c.getString(c.getColumnIndex("precio")));
        }
        ArrayAdapter adapterProductos = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, producto);
        adapterProductos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productos.setAdapter(adapterProductos);
    }

    private void cargarvariedad(){
        //SPINNER VARIEDAD
        ArrayList<String>variedadarr = new ArrayList<>();
        variedadarr.add("Seleccione variedad");
        variedadid.add("0");

        SQLiteDatabase db = myDB.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT id, name FROM variedad ORDER BY name ASC", null);
        while (c.moveToNext()){
            variedadarr.add(c.getString(c.getColumnIndex("name")));
            variedadid.add(c.getString(c.getColumnIndex("id")));
        }
        ArrayAdapter adapterVariedad = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, variedadarr);
        adapterVariedad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        variedad.setAdapter(adapterVariedad);
    }


    private void cargarTara(){
        //SPINNER VARIEDAD
        ArrayList<String>taraarray = new ArrayList<>();
        taraarray.add("Seleccione tara");
        taraid.add("0");
        tarapeso.add("0");

        SQLiteDatabase db = myDB.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT id, name_envase, peso FROM tara ORDER BY name_envase ASC", null);
        while (c.moveToNext()){
            String nombre = c.getString(c.getColumnIndex("name_envase"));
            String peso = "- "+c.getString(c.getColumnIndex("peso"));
            String nombrepeso = nombre + " " + peso;
            taraarray.add(nombrepeso);
            taraid.add(c.getString(c.getColumnIndex("id")));
            tarapeso.add(c.getString(c.getColumnIndex("peso")));
        }
        ArrayAdapter adapterTara = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, taraarray);
        adapterTara.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tara.setAdapter(adapterTara);
    }


}