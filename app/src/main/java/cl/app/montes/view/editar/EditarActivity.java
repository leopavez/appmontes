package cl.app.montes.view.editar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cl.app.montes.R;
import cl.app.montes.db.DatabaseHelper;
import cl.app.montes.view.drawer.MenuDrawerActivity;
import cl.app.montes.view.drawer.fragment.Formulario_fragment;
import cl.app.montes.view.drawer.fragment.Listado_fragment;

public class EditarActivity extends AppCompatActivity {

    String id_registro = "";
    SearchableSpinner productor;
    Spinner recorridospinner, productos, variedad, tara;
    TextInputEditText cantidad_envase, kilos_brutos, band_p, band_e, precio_usuario;
    ArrayList<String> recorridoid = new ArrayList<>();
    ArrayList<String> recorridoname = new ArrayList<>();
    ArrayList<String>productoresid = new ArrayList<>();
    ArrayList<String>productoid = new ArrayList<>();
    ArrayList<String>productoname = new ArrayList<>();
    ArrayList<String>productorname = new ArrayList<>();
    ArrayList<String>variedadid = new ArrayList<>();
    ArrayList<String>variedadname = new ArrayList<>();
    ArrayList<String>taraid = new ArrayList<>();
    ArrayList<String>tarapeso = new ArrayList<>();
    ArrayList<String>taraname = new ArrayList<>();
    ArrayList<String>precioproducto = new ArrayList<>();
    TextView kilos_netos, total;
    String k_netos = "";
    String total__ = "";
    Button editar;
    DatabaseHelper myDB;
    ProgressDialog progressDialog;

    TextInputLayout cantidadlayout, bandejas_pendienteslayout, bandejas_entregadaslayout, kilos_brutoslayout, precio_usuariolayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Editar recorrido");
        id_registro = getIntent().getExtras().getString("id_registro");
        recorridospinner = (Spinner)findViewById(R.id.recorridoSpinner);
        productor = (SearchableSpinner) findViewById(R.id.productorSpinner);
        productos = (Spinner)findViewById(R.id.productoSpinner);
        variedad = (Spinner)findViewById(R.id.especieSpinner);
        tara = (Spinner)findViewById(R.id.taraSpinner);
        cantidad_envase = (TextInputEditText)findViewById(R.id.txtcantidadenvase);
        kilos_brutos = (TextInputEditText)findViewById(R.id.txtkilosbrutos);
        band_e = (TextInputEditText)findViewById(R.id.txtbandejas_entregadas);
        band_p = (TextInputEditText)findViewById(R.id.txtbandejas_pendientes);
        kilos_netos = (TextView)findViewById(R.id.textviewkilosnetos);
        precio_usuario = (TextInputEditText)findViewById(R.id.txtpreciousuario);
        precio_usuariolayout = (TextInputLayout)findViewById(R.id.textinputlayoutprecio_usuario);
        total = (TextView)findViewById(R.id.texviewtotal);
        editar = (Button)findViewById(R.id.btneditar);
        cantidadlayout = (TextInputLayout)findViewById(R.id.textinputLayoutcantidad);
        bandejas_entregadaslayout = (TextInputLayout)findViewById(R.id.textinputlayoutbandejas_entregadas);
        bandejas_pendienteslayout = (TextInputLayout)findViewById(R.id.textinputlayoutbandejas_pendientes);
        kilos_brutoslayout = (TextInputLayout)findViewById(R.id.textinputlayoutkilos_brutos);
        myDB = new DatabaseHelper(EditarActivity.this);

        //ADAPTADORES DE SPINNER
        cargarrecorrido();
        cargarproductor();
        cargarproducto();
        cargarvariedad();
        cargarTara();

        cargarDatos();

        productor.setTitle("Seleccione productor");
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

        productos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int posiciontara = tara.getSelectedItemPosition();
                String pesotara = tarapeso.get(posiciontara);
                Double peso = Double.parseDouble(pesotara.toString());
                String idtara = taraid.get(posiciontara);

                if(!cantidad_envase.getText().toString().equals("") && !kilos_brutos.getText().toString().equals("")
                        && !idtara.equals("0")){
                    Double kbrutos = Double.parseDouble(kilos_brutos.getText().toString());
                    int cant = Integer.parseInt(cantidad_envase.getText().toString());


                    int posicionproducto = productos.getSelectedItemPosition();
                    String precioprod = precioproducto.get(posicionproducto);
                    int preciopr = Integer.parseInt(precioprod.toString());

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

        tara.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int posiciontara = tara.getSelectedItemPosition();
                String pesotara = tarapeso.get(posiciontara);
                Double peso = Double.parseDouble(pesotara.toString());
                String idtara = taraid.get(posiciontara);

                if(!cantidad_envase.getText().toString().equals("") && !kilos_brutos.getText().toString().equals("")
                        && !idtara.equals("0")){
                    Double kbrutos = Double.parseDouble(kilos_brutos.getText().toString());
                    int cant = Integer.parseInt(cantidad_envase.getText().toString());


                    int posicionproducto = productos.getSelectedItemPosition();
                    String precioprod = precioproducto.get(posicionproducto);
                    int preciopr = Integer.parseInt(precioprod.toString());

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
                        && !idtara.equals("0")){
                    Double kbrutos = Double.parseDouble(kilos_brutos.getText().toString());
                    int cant = Integer.parseInt(cantidad_envase.getText().toString());


                    int posicionproducto = productos.getSelectedItemPosition();
                    String precioprod = precioproducto.get(posicionproducto);
                    int preciopr = Integer.parseInt(precioprod.toString());

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
                        && !idtara.equals("0")){
                    Double kbrutos = Double.parseDouble(kilos_brutos.getText().toString());
                    int cant = Integer.parseInt(cantidad_envase.getText().toString());


                    int posicionproducto = productos.getSelectedItemPosition();
                    String precioprod = precioproducto.get(posicionproducto);
                    int preciopr = Integer.parseInt(precioprod.toString());

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

        editar.setOnClickListener(new View.OnClickListener() {
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
                    if(idproductor.equals("0")){
                        TextView errorText = (TextView)productor.getSelectedView();
                        errorText.setError("");
                        errorText.setTextColor(Color.RED);//just to highlight that this is an error
                        errorText.setText("Seleccione productor");//changes the selected item text to this
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



                    Toast.makeText(EditarActivity.this, "Falta ingresar datos", Toast.LENGTH_SHORT).show();
                }else {
                    //
                    progressDialog = new ProgressDialog(EditarActivity.this);
                    progressDialog.setTitle("Editando recorrido");
                    progressDialog.setMessage("Espere un momento...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    boolean resultado = myDB.editarRegistros(id_registro, idrecorrido, idproductor, idproductos, idvariedad,
                            idtara, "bandeja", cantidad, kilosb, k_netos, total__, bpendientes, bentregadas, precio_usuario.toString());

                    if (resultado) {
                        progressDialog.dismiss();
                        Toast.makeText(EditarActivity.this, "Recorrido editado correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditarActivity.this, MenuDrawerActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(EditarActivity.this, "Error. No se pudo editar el registro", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }

    private void cargarDatos(){

        SQLiteDatabase db = myDB.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT recorrido, productor, producto, variedad, tara, cantidad_envase, kilos_brutos, kilos_netos," +
                " total, bandejas_pendientes, bandejas_entregadas, precio_usuario FROM registros WHERE id ='"+id_registro+"'", null);


        if (cursor.moveToFirst()){

            int idreco = Integer.parseInt(cursor.getString(0));
            String posreco = "";

            int idproductor = Integer.parseInt(cursor.getString(1));
            String posprod = "";

            int idproducto = Integer.parseInt(cursor.getString(2));
            String posproducto = "";

            int idvariedad = Integer.parseInt(cursor.getString(3));
            String posvar = "";

            int idtara = Integer.parseInt(cursor.getString(4));
            String postara = "";

            Cursor cursor1 = db.rawQuery("SELECT name FROM variedad WHERE id = '"+idvariedad+"'", null);
            Cursor cursor2 = db.rawQuery("SELECT razon_social FROM productores WHERE id = '"+idproductor+"'",null);
            Cursor cursor3 = db.rawQuery("SELECT name from recorrido WHERE id = '"+idreco+"'", null);
            Cursor cursor4 = db.rawQuery("SELECT id, name, precio FROM productos WHERE id = '"+idproducto+"'", null);
            Cursor cursor5 = db.rawQuery("SELECT id, name_envase, peso FROM tara WHERE id = '"+idtara+"'", null);
            if (cursor1.moveToFirst()){
                int posvariedad = variedadname.indexOf(cursor1.getString(0));
                posvar = String.valueOf(posvariedad);
            }
            if (cursor2.moveToFirst()){
                int posproductorr = productorname.indexOf(cursor2.getString(0));
                posprod = String.valueOf(posproductorr);
            }
            if (cursor3.moveToFirst()){
                int posrecorrido = recorridoname.indexOf(cursor3.getString(0));
                posreco = String.valueOf(posrecorrido);
            }
            if (cursor4.moveToFirst()){
                int posproductos = productoname.indexOf(cursor4.getString(1));
                posproducto = String.valueOf(posproductos);
            }
            if (cursor5.moveToFirst()){
                int positara = taraname.indexOf(cursor5.getString(1));
                postara = String.valueOf(positara);
            }

            recorridospinner.setSelection(Integer.parseInt(posreco));
            productor.setSelection(Integer.parseInt(posprod));
            productos.setSelection(Integer.parseInt(posproducto));
            variedad.setSelection(Integer.parseInt(posvar));
            tara.setSelection(Integer.parseInt(postara));
            band_p.setText(cursor.getString(9));
            band_e.setText(cursor.getString(10));
            cantidad_envase.setText(cursor.getString(5));
            kilos_brutos.setText(cursor.getString(6));
            precio_usuario.setText(cursor.getString(11));
            kilos_netos.setText("Kilos netos: "+cursor.getString(7));
            total.setText("Total: "+cursor.getString(8));
        }
    }

    private void cargarrecorrido(){
        //SPINNER RECORRIDO
        ArrayList<String>recorrido = new ArrayList<>();
        recorrido.add("Seleccione recorrido");
        recorridoid.add("0");
        recorridoname.add("Seleccione");

        SQLiteDatabase db = myDB.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT id, name FROM recorrido ORDER BY name ASC", null);
        while (c.moveToNext()){
            recorrido.add(c.getString(c.getColumnIndex("name")));
            recorridoid.add(c.getString(c.getColumnIndex("id")));
            recorridoname.add(c.getString(c.getColumnIndex("name")));
        }
        ArrayAdapter adapterRecorrido = new ArrayAdapter<>(EditarActivity.this, android.R.layout.simple_spinner_item, recorrido);
        adapterRecorrido.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recorridospinner.setAdapter(adapterRecorrido);
    }

    private void cargarproductor(){
        //SPINNER PRODUCTOR
        ArrayList<String>productores = new ArrayList<>();
        productores.add("Seleccione productor");
        productoresid.add("0");
        productorname.add("Seleccione");
        SQLiteDatabase db = myDB.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT id, razon_social FROM productores ORDER BY razon_social ASC", null);
        while (c.moveToNext()){
            productores.add(c.getString(c.getColumnIndex("razon_social")));
            productorname.add(c.getString(c.getColumnIndex("razon_social")));
            productoresid.add(c.getString(c.getColumnIndex("id")));
        }
        ArrayAdapter adapterProductores = new ArrayAdapter<>(EditarActivity.this, android.R.layout.simple_spinner_item, productores);
        adapterProductores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productor.setAdapter(adapterProductores);
    }

    private void cargarproducto(){
        //SPINNER PRODUCTO
        ArrayList<String>producto = new ArrayList<>();
        producto.add("Seleccione producto");
        productoid.add("0");
        precioproducto.add("0");
        productoname.add("Seleccione");

        SQLiteDatabase db = myDB.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT id, name, precio FROM productos ORDER BY name ASC", null);
        while (c.moveToNext()){
            String nombre = c.getString(c.getColumnIndex("name"));
            String precio = "- $ "+c.getString(c.getColumnIndex("precio"));
            String nombreprecio = nombre + " " + precio;
            producto.add(nombreprecio);
            productoid.add(c.getString(c.getColumnIndex("id")));
            precioproducto.add(c.getString(c.getColumnIndex("precio")));
            productoname.add(c.getString(c.getColumnIndex("name")));
        }
        ArrayAdapter adapterProductos = new ArrayAdapter<>(EditarActivity.this, android.R.layout.simple_spinner_item, producto);
        adapterProductos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productos.setAdapter(adapterProductos);
    }

    private void cargarvariedad(){
        //SPINNER VARIEDAD
        ArrayList<String>variedadarr = new ArrayList<>();
        variedadarr.add("Seleccione variedad");
        variedadid.add("0");
        variedadname.add("Seleccione");

        SQLiteDatabase db = myDB.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT id, name FROM variedad ORDER BY name ASC", null);
        while (c.moveToNext()){
            variedadarr.add(c.getString(c.getColumnIndex("name")));
            variedadname.add(c.getString(c.getColumnIndex("name")));
            variedadid.add(c.getString(c.getColumnIndex("id")));
        }
        ArrayAdapter adapterVariedad = new ArrayAdapter<>(EditarActivity.this, android.R.layout.simple_spinner_item, variedadarr);
        adapterVariedad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        variedad.setAdapter(adapterVariedad);
    }


    private void cargarTara(){
        //SPINNER VARIEDAD
        ArrayList<String>taraarray = new ArrayList<>();
        taraarray.add("Seleccione tara");
        taraid.add("0");
        tarapeso.add("0");
        taraname.add("Seleccione");

        SQLiteDatabase db = myDB.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT id, name_envase, peso FROM tara ORDER BY name_envase ASC", null);
        while (c.moveToNext()){
            String nombre = c.getString(c.getColumnIndex("name_envase"));
            String peso = "- "+c.getString(c.getColumnIndex("peso"));
            String nombrepeso = nombre + " " + peso;
            taraarray.add(nombrepeso);
            taraid.add(c.getString(c.getColumnIndex("id")));
            tarapeso.add(c.getString(c.getColumnIndex("peso")));
            taraname.add(c.getString(c.getColumnIndex("name_envase")));
        }
        ArrayAdapter adapterTara = new ArrayAdapter<>(EditarActivity.this, android.R.layout.simple_spinner_item, taraarray);
        adapterTara.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tara.setAdapter(adapterTara);
    }
}
