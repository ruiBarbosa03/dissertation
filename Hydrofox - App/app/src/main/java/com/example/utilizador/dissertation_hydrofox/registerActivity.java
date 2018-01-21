package com.example.utilizador.dissertation_hydrofox;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.util.Objects;

public class registerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Integer access = NetworkUtil.getConnectivityStatus(this);
        if (access == 0)
            Toast.makeText(registerActivity.this, "Ligar à Internet", Toast.LENGTH_LONG).show();

        final SharedPreferencesUtility newRegister = new SharedPreferencesUtility(this);
        if (newRegister.getPassword() != null && newRegister.getLogin() != null && newRegister.getUsername() != null){
            Intent home = new Intent (registerActivity.this, MainActivity.class);
            startActivity(home);
        }

        final EditText username = (EditText) findViewById(R.id.etUsername);
        final EditText deviceid = (EditText) findViewById(R.id.etDeviceID);
        final EditText phonenumber = (EditText) findViewById(R.id.etTelemovel);
        final EditText firstread = (EditText) findViewById(R.id.etFirstread);
        final String login = getIntent().getStringExtra("login");
        final String password = getIntent().getStringExtra("password");

        final Button btnRegister = (Button) findViewById(R.id.btnRegister);

        // Capture button clicks
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnRegister.setEnabled(false);

                final String usernameString = username.getText().toString();
                final String deviceidString = deviceid.getText().toString();
                final String phonenumberString = phonenumber.getText().toString();
                final String firstreadString = firstread.getText().toString();
                final Integer firstreadValue = Integer.parseInt(firstreadString);

                //1 - Verificar se todos os campos estão preenchidos
                if (usernameString.matches("") || deviceidString.matches("") || phonenumberString.matches("")){
                    Toast.makeText(registerActivity.this, "Preencha todos os dados.", Toast.LENGTH_LONG).show();
                }
                else {
                    //2 - Enviar para Base de Dados e verificar que não há erros

                    Response.Listener<String> responseListener = new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {

                            if (response.equals("good")) {
                                //Se não houver -> Guardar toda a info em cache e passar para home
                                newRegister.makeLogin(login, password);
                                newRegister.setUsername(usernameString);
                                newRegister.setPhonenumber(phonenumberString);
                                newRegister.setDeviceID(deviceidString);
                                newRegister.setFirstreading(firstreadValue);
                                newRegister.setFugas(false);
                                newRegister.setFugasLiters(0);
                                newRegister.setModo(false);
                                Toast.makeText(registerActivity.this, "Registado!", Toast.LENGTH_LONG).show();
                                Intent home = new Intent(registerActivity.this, MainActivity.class);
                                startActivity(home);
                            } else {
                                Toast.makeText(registerActivity.this, response, Toast.LENGTH_LONG).show();
                            }
                        }
                    };
                    RegisterRequest newRegister = new RegisterRequest(login, password, usernameString, deviceidString, phonenumberString, responseListener);
                    RequestQueue queueA = Volley.newRequestQueue(registerActivity.this);
                    newRegister.setRetryPolicy(new DefaultRetryPolicy(0,-1,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queueA.add(newRegister);

                }
                btnRegister.setEnabled(true);
            }
        });

    }
}
