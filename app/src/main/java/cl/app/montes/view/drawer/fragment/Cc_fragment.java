package cl.app.montes.view.drawer.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;

import cl.app.montes.R;
import cl.app.montes.db.DatabaseHelper;

public class Cc_fragment extends Fragment {

    SearchableSpinner productor;
    DatabaseHelper myDB;
    ArrayList<String>productoresid = new ArrayList<>();

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
        myDB = new DatabaseHelper(getActivity().getApplicationContext());
        cargarproductor();

        productor.setTitle("Selecciona el productor");
        productor.setPositiveButton("OK");
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
