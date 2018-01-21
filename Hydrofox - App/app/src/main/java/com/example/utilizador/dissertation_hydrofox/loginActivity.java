package com.example.utilizador.dissertation_hydrofox;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import android.R.*;

public class loginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Se já existe login feito em cache entra diretamente
        //Isto é um problema de segurança grande já que a password fica desencriptada no telemóvel. Acessos root conseguem chegar.
        final SharedPreferencesUtility loginHelper = new SharedPreferencesUtility(loginActivity.this);
        if (loginHelper.getPassword() != null && loginHelper.getLogin() != null && loginHelper.getUsername() != null){
            Intent home = new Intent (loginActivity.this, MainActivity.class);
            startActivity(home);
        }

        //Inicialização (EditText - login; password) + (Button btnLogin)
        final EditText login = (EditText) findViewById(R.id.etLogin);
        final EditText password = (EditText) findViewById(R.id.etPassword);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);

        Integer access = NetworkUtil.getConnectivityStatus(this);
        if (access == 0)
            Toast.makeText(loginActivity.this, "Ligar à Internet", Toast.LENGTH_LONG).show();

        // Ao pressionar botão faz pedido HTTP com dados inseridos
        // Resposta com HTTP 200 -> É enviado para página registo
        // Qualquer outra resposta dá mensagem de erro
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                final Intent register = new Intent(loginActivity.this, registerActivity.class);
                final String loginText = login.getText().toString();
                final String passwordText = password.getText().toString();

                //1º Passo - Verificar se estão vazios
                if (loginText.matches("") || passwordText.matches("")){
                    Toast.makeText(loginActivity.this, "Preencha todos os dados.", Toast.LENGTH_LONG).show();
                }
                else {
                    //2º Passo - Verificar se há info em BD (pedido de JSON)
                    //Se sim -> guardar info em cache e ir para MainActivity
                    //Se não -> Ardeu

                    //3º Passo - Fazer pedido HTTP para verificar se login é valido
                    RequestQueue queue = Volley.newRequestQueue(loginActivity.this);
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://backend.sigfox.com/api/devicetypes", null, new Response.Listener<JSONObject>() {
                        //Pedido tem resposta HTTP200
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(loginActivity.this, "Login bem sucedido", Toast.LENGTH_LONG).show();
                            register.putExtra("login", loginText);
                            register.putExtra("password", passwordText);
                            startActivity(register);
                        }
                        //Dá erro
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error", "Error with authentication");
                            Toast.makeText(loginActivity.this, "Erro no login. Verifique credenciais.", Toast.LENGTH_LONG).show();
                            login.setText("");
                            password.setText("");
                            login.setHint("Login");
                            password.setHint("Password");
                        }
                    }) {
                        //Faz autorização
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<String, String>();
                            String loginEncoded = new String(Base64.encode((loginText + ":" + passwordText).getBytes(), Base64.NO_WRAP));
                            headers.put("Authorization", "Basic " + loginEncoded);
                            headers.put("Content-Type", "application/json");
                            return headers;
                        }
                    };
                    queue.add(request);

                }
            }
        });

    }



}
