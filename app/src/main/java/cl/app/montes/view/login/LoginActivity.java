package cl.app.montes.view.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import cl.app.montes.R;
import cl.app.montes.db.DatabaseHelper;
import cl.app.montes.validation.InputValidation;
import cl.app.montes.view.drawer.MenuDrawerActivity;
import cl.app.montes.view.main.MainActivity;

public class LoginActivity extends AppCompatActivity {


    TextInputEditText email;
    TextInputEditText password;
    TextInputLayout tlemail;
    TextInputLayout tlpassword;
    Button ingresar;
    SwitchCompat switchCompat;
    InputValidation inputValidation;
    DatabaseHelper myDB;
    SharedPreferences preferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        preferences = getSharedPreferences("recordar", Context.MODE_PRIVATE);
        boolean stateSwitch = preferences.getBoolean("stateSwitch", false);
        email = (TextInputEditText)findViewById(R.id.txtemail);
        tlemail = (TextInputLayout)findViewById(R.id.textinputLayoutusers);
        password = (TextInputEditText)findViewById(R.id.txtpassword);
        tlpassword = (TextInputLayout)findViewById(R.id.textInputLayoutpass);
        switchCompat = (SwitchCompat)findViewById(R.id.switch_recordar);
        ingresar = (Button)findViewById(R.id.btniniciar);
        inputValidation = new InputValidation(this);
        myDB = new DatabaseHelper(this);
        switchCompat.setChecked(stateSwitch);

        cargarChecked();

        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              verificarUsuario();
            }
        });
    }

    private void verificarUsuario(){
        if (!inputValidation.isInputEditTextFilled((TextInputEditText) email, tlemail, "Email invalido")){
            return;
        }
        if (!inputValidation.isInputEditTextFilled((TextInputEditText) password, tlpassword, "Contraseña invalida")){
            return;
        }
        if (myDB.LoginUsers(email.getText().toString().trim(), password.getText().toString().trim(), LoginActivity.this)){

            if(switchCompat.isChecked()){
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("email", email.getText().toString());
                editor.putString("password", password.getText().toString());
                editor.putBoolean("stateSwitch", switchCompat.isChecked());
                editor.commit();
            }else{
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("email","");
                editor.putString("password", "");
                editor.putBoolean("stateSwitch", switchCompat.isChecked());
                editor.commit();
            }


             Intent intent = new Intent(LoginActivity.this, MenuDrawerActivity.class);
             startActivity(intent);
             finish();
        } else{
            Toast.makeText(this, "Usuario o password incorrectos", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarChecked(){
        SharedPreferences preferences = getSharedPreferences("recordar", Context.MODE_PRIVATE);
        String emailrecord = preferences.getString("email", "");
        String passwordrecord = preferences.getString("password","");

        if(!emailrecord.toString().equals("") && !passwordrecord.toString().equals("")){
            email.setText(emailrecord);
            password.setText(passwordrecord);
        }
    }
}
