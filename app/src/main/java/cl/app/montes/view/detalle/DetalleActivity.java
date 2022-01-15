package cl.app.montes.view.detalle;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cl.app.montes.R;
import cl.app.montes.db.DatabaseHelper;

public class DetalleActivity extends AppCompatActivity {

    String id_registro = "";
    DatabaseHelper myDB;
    TextView recorrido, productor, fechahora, producto, variedad, tara, bandejas_p, bandejas_e, cantidad, kb, kn, total, precio_usuario;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myDB = new DatabaseHelper(DetalleActivity.this);
        id_registro = getIntent().getExtras().getString("id_registro");
        recorrido = (TextView)findViewById(R.id.tvrecorrido);
        productor = (TextView)findViewById(R.id.tvproductor);
        fechahora = (TextView)findViewById(R.id.tvfechahora);
        producto = (TextView)findViewById(R.id.tvproducto);
        variedad = (TextView)findViewById(R.id.tvvariedad);
        tara = (TextView)findViewById(R.id.tvtara);
        bandejas_p = (TextView)findViewById(R.id.tvbandejaspendientes);
        bandejas_e = (TextView)findViewById(R.id.tvbandejasentregadas);
        kb = (TextView)findViewById(R.id.tvkilosbrutos);
        kn = (TextView)findViewById(R.id.tvkilosnetos);
        total = (TextView)findViewById(R.id.tvtotal);
        cantidad = (TextView)findViewById(R.id.tvcantidad);
        precio_usuario = (TextView)findViewById(R.id.tvprecio_usuario);

        cargarDetalle();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }

    private void cargarDetalle(){
        try {
            SQLiteDatabase db = myDB.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT reg.fecha, reg.hora, reco.name, pro.razon_social, prod.name, va.name, " +
                    "ta.name_envase, reg.bandejas_pendientes, reg.bandejas_entregadas, reg.cantidad_envase, reg.kilos_brutos, " +
                    "reg.kilos_netos, reg.total, reg.precio_usuario FROM registros reg " +
                    "INNER JOIN recorrido reco ON reg.recorrido = reco.id " +
                    "INNER JOIN productores pro ON reg.productor = pro.id " +
                    "INNER JOIN productos prod ON reg.producto = prod.id " +
                    "INNER JOIN variedad va ON reg.variedad = va.id " +
                    "INNER JOIN tara ta ON reg.tara = ta.id " +
                    "WHERE reg.id ='"+id_registro+"'", null);

            if (cursor.moveToFirst()){

                fechahora.setText("Fecha: "+cursor.getString(0)+"   Hora: "+cursor.getString(1));
                recorrido.setText("Recorrido: "+cursor.getString(2));
                productor.setText("Productor: "+cursor.getString(3));
                producto.setText("Producto: "+cursor.getString(4));
                variedad.setText("Variedad: "+cursor.getString(5));
                tara.setText("Tara: "+cursor.getString(6));
                bandejas_p.setText("Bandejas pendientes: "+cursor.getString(7));
                bandejas_e.setText("Bandejas entregadas: "+cursor.getString(8));
                cantidad.setText("Cantidad: "+cursor.getString(9));
                kb.setText("Kilos brutos: "+cursor.getString(10));
                kn.setText("Kilos netos: "+cursor.getString(11));
                total.setText("Total: "+cursor.getString(12));
                precio_usuario.setText("Precio: "+cursor.getString(13));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
