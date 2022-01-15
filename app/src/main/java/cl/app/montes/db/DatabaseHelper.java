package cl.app.montes.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.password4j.Password;

import java.util.ArrayList;
import java.util.List;

import cl.app.montes.clases.Productores;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "montes.db";
    public static final String TABLA_USUARIOS = "usuarios";
    public static final String TABLA_PRODUCTOS = "productos";
    public static final String TABLA_PRODUCTORES = "productores";
    public static final String TABLA_RECORRIDO = "recorrido";
    public static final String TABLA_VARIEDAD = "variedad";
    public static final String TABLA_TARA = "tara";
    public static final String TABLA_REGISTROS = "registros";

    //TABLA USUARIOS
    public static final String id_usuarios = "id";
    public static final String name_usuarios = "name";
    public static final String email_usuarios = "email";
    public static final String password_usuarios ="password";

    //TABLA PRODUCTOS
    public static final String id_productos = "id";
    public static final String name_productos = "name";
    public static final String precio_productos = "precio";

    //TABLA PRODUCTORES
    public static final String id_productores = "id";
    public static final String razon_social = "razon_social";
    public static final String rut_productores = "rut";

    //TABLA RECORRIDO
    public static final String id_recorrido = "id";
    public static final String name_recorrido = "name";

    //TABLA VARIEDAD
    public static final String id_variedad = "id";
    public static final String name_variedad = "name";

    //TABLA TARA
    public static final String id_tara = "id";
    public static final String peso_tara = "peso";
    public static final String name_tara_ = "name_envase";

    //TABLA REGISTROS
    public static final String id_usuario_registro = "id_usuario";
    public static final String fecha_registro = "fecha";
    public static final String hora_registro = "hora";
    public static final String recorrido_registro = "recorrido";
    public static final String productor_registro = "productor";
    public static final String producto_registro = "producto";
    public static final String variedad_registro = "variedad";
    public static final String tara_registro = "tara";
    public static final String bandeja_registro = "bandeja";
    public static final String cantidad_envase_registro = "cantidad_envase";
    public static final String kbrutos_registro = "kilos_brutos";
    public static final String knetos_registro = "kilos_netos";
    public static final String total_registro = "total";
    public static final String bandejas_pendientes ="bandejas_pendientes";
    public static final String bandejas_entregadas = "bandejas_entregadas";
    public static final String precio_usuario = "precio_usuario";



    final String CREAR_TABLA_USUARIOS = "CREATE TABLE usuarios (id INTEGER unique, name TEXT, email TEXT, password TEXT)";
    final String CREAR_TABLA_PRODUCTOS = "CREATE TABLE productos (id INTEGER unique, name TEXT, precio INTEGER)";
    final String CREAR_TABLA_PRODUCTORES = "CREATE TABLE productores (id INTEGER unique, razon_social TEXT, rut TEXT)";
    final String CREAR_TABLA_RECORRIDO = "CREATE TABLE recorrido (id INTEGER unique, name TEXT)";
    final String CREAR_TABLA_VARIEDAD = "CREATE TABLE variedad (id INTEGER unique, name TEXT)";
    final String CREAR_TABLA_TARA = "CREATE TABLE tara (id INTEGER unique, peso TEXT, name_envase TEXT)";
    final String CREAR_TABLA_REGISTROS = "CREATE TABLE registros(id INTEGER PRIMARY KEY AUTOINCREMENT unique, id_usuario INTEGER, fecha TEXT, hora TEXT," +
            "recorrido TEXT, productor TEXT, producto TEXT, variedad TEXT, tara TEXT, bandeja TEXT, cantidad_envase TEXT," +
            "kilos_brutos TEXT, kilos_netos TEXT, total TEXT, bandejas_pendientes TEXT, bandejas_entregadas TEXT, precio_usuario TEXT)";


    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null,9);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAR_TABLA_USUARIOS);
        db.execSQL(CREAR_TABLA_PRODUCTOS);
        db.execSQL(CREAR_TABLA_PRODUCTORES);
        db.execSQL(CREAR_TABLA_RECORRIDO);
        db.execSQL(CREAR_TABLA_VARIEDAD);
        db.execSQL(CREAR_TABLA_TARA);
        db.execSQL(CREAR_TABLA_REGISTROS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS productos");
        db.execSQL("DROP TABLE IF EXISTS productores");
        db.execSQL("DROP TABLE IF EXISTS recorrido");
        db.execSQL("DROP TABLE IF EXISTS variedad");
        db.execSQL("DROP TABLE IF EXISTS tara");
        db.execSQL("DROP TABLE IF EXISTS registros");
        db.execSQL(CREAR_TABLA_USUARIOS);
        db.execSQL(CREAR_TABLA_PRODUCTOS);
        db.execSQL(CREAR_TABLA_PRODUCTORES);
        db.execSQL(CREAR_TABLA_RECORRIDO);
        db.execSQL(CREAR_TABLA_VARIEDAD);
        db.execSQL(CREAR_TABLA_TARA);
        db.execSQL(CREAR_TABLA_REGISTROS);
    }

    //METODOS DE REGISTRO

    public boolean guardarUsuarios(int id, String name, String email, String password){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(id_usuarios, id);
            contentValues.put(name_usuarios, name);
            contentValues.put(email_usuarios, email);
            contentValues.put(password_usuarios, password);

            int resultado = (int) db.insert(TABLA_USUARIOS, null, contentValues);
            if(resultado == -1){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean guardarProductos(int id, String name, int precio){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(id_productos, id);
            contentValues.put(name_productos, name);
            contentValues.put(precio_productos, precio);

            int resultado = (int) db.insert(TABLA_PRODUCTOS, null, contentValues);
            if(resultado == -1){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean guardarProductores(int id, String rs, String rut){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(id_productores, id);
            contentValues.put(razon_social, rs);
            contentValues.put(rut_productores, rut);

            int resultado = (int) db.insert(TABLA_PRODUCTORES, null, contentValues);
            if(resultado == -1){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean guardarRecorrido(int id, String name){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(id_recorrido, id);
            contentValues.put(name_recorrido, name);

            int resultado = (int) db.insert(TABLA_RECORRIDO, null, contentValues);
            if(resultado == -1){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean guardarVariedad(int id, String name){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(id_variedad, id);
            contentValues.put(name_variedad, name);

            int resultado = (int) db.insert(TABLA_VARIEDAD, null, contentValues);
            if(resultado == -1){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean guardarTara(int id, String peso, String name_envase){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(id_tara, id);
            contentValues.put(peso_tara, peso);
            contentValues.put(name_tara_, name_envase);

            int resultado = (int) db.insert(TABLA_TARA, null, contentValues);
            if(resultado == -1){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean guardarRegistros(int id_usuario, String fecha, String hora, String recorrido, String productor,
                                    String producto, String variedad,String tara, String bandeja, String cantidad_envase,
                                    String kilos_brutos, String kilos_netos, String total, String bandejas_p, String bandejas_e, String precio_u){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(id_usuario_registro, id_usuario);
            contentValues.put(fecha_registro, fecha);
            contentValues.put(hora_registro, hora);
            contentValues.put(recorrido_registro, recorrido);
            contentValues.put(productor_registro, productor);
            contentValues.put(producto_registro, producto);
            contentValues.put(variedad_registro, variedad);
            contentValues.put(tara_registro, tara);
            contentValues.put(bandeja_registro, bandeja);
            contentValues.put(cantidad_envase_registro, cantidad_envase);
            contentValues.put(kbrutos_registro, kilos_brutos);
            contentValues.put(knetos_registro, kilos_netos);
            contentValues.put(total_registro, total);
            contentValues.put(bandejas_pendientes,bandejas_p);
            contentValues.put(bandejas_entregadas, bandejas_e);
            contentValues.put(precio_usuario, precio_u);

            int resultado = (int) db.insert(TABLA_REGISTROS, null, contentValues);
            if(resultado == -1){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean LoginUsers(String email, String password, Context context){

        String[] columns = {
                id_usuarios
        };
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor pwd = db.rawQuery("SELECT password, id, name FROM usuarios where email ='"+email+"'",null);

        if(pwd.getCount() > 0){
            String passhash = null;
            int id = 0;
            String name = null;
            if(pwd.moveToFirst()){
                passhash = pwd.getString(0);
                id = pwd.getInt(1);
                name = pwd.getString(2);
            }

            boolean verified = Password.check(password, passhash).withBCrypt();

            if (verified){
                SharedPreferences sharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("id", id);
                editor.putString("nombre", name);
                editor.apply();

                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public boolean editarRegistros(String id_registro, String recorrido, String productor,
                                    String producto, String variedad,String tara, String bandeja, String cantidad_envase,
                                    String kilos_brutos, String kilos_netos, String total, String bandejas_p, String bandejas_e, String precio_u){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(recorrido_registro, recorrido);
            contentValues.put(productor_registro, productor);
            contentValues.put(producto_registro, producto);
            contentValues.put(variedad_registro, variedad);
            contentValues.put(tara_registro, tara);
            contentValues.put(bandeja_registro, bandeja);
            contentValues.put(cantidad_envase_registro, cantidad_envase);
            contentValues.put(kbrutos_registro, kilos_brutos);
            contentValues.put(knetos_registro, kilos_netos);
            contentValues.put(total_registro, total);
            contentValues.put(bandejas_pendientes,bandejas_p);
            contentValues.put(bandejas_entregadas, bandejas_e);
            contentValues.put(precio_usuario, precio_u);

            int resultado = (int) db.update(TABLA_REGISTROS,contentValues,"id = ?",new String[]{id_registro});
            if(resultado == -1){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public List<Productores> search (String keyword){
        List<Productores> vh = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT razon_social FROM productores where razon_social like ?"
                    , new String[] {"%" + keyword + "%"});
            if (cursor.moveToFirst()){
                vh = new ArrayList<Productores>();
                do {
                    Productores productores = new Productores();
                    productores.setRazon_social(cursor.getString(0));
                    vh.add(productores);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            vh = null;
        }
        return vh;
    }
}
