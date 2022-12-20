package cl.app.montes.view.detalle;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cl.app.montes.R;
import cl.app.montes.db.DatabaseHelper;

public class DetalleDevolverActivity extends AppCompatActivity {

    String id_registro = "";
    DatabaseHelper myDB;
    TextView productor, desde, hasta, envase, cantidad;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_devolver);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myDB = new DatabaseHelper(DetalleDevolverActivity.this);
        id_registro = getIntent().getExtras().getString("id_registro");
        productor = (TextView)findViewById(R.id.tvproductor);
        desde = (TextView)findViewById(R.id.tvdesde);
        hasta = (TextView)findViewById(R.id.tvhasta);
        envase = (TextView)findViewById(R.id.tvenvase);
        cantidad = (TextView)findViewById(R.id.tvcantidad);

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

            Cursor cursor = db.rawQuery("SELECT reg.desde, reg.hasta, pro.razon_social, " +
                    "ta.name_envase, reg.cantidad FROM registros_devolucion reg " +
                    "INNER JOIN productores pro ON reg.productor_id = pro.id " +
                    "INNER JOIN tara ta ON reg.envases_id = ta.envase_id " +
                    "WHERE reg.id ='"+id_registro+"'", null);

            if (cursor.moveToFirst()){

                productor.setText("Productor: "+cursor.getString(2));
                desde.setText("Desde: "+cursor.getString(0));
                hasta.setText("Hasta: "+cursor.getString(1));
                envase.setText("Envases: "+cursor.getString(3));
                cantidad.setText("Cantidad: "+cursor.getString(4));

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
