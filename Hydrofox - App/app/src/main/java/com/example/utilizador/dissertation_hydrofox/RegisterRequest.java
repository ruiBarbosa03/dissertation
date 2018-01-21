package com.example.utilizador.dissertation_hydrofox;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "https://paginas.fe.up.pt/~ee12014/sigfoxDatabase/registerUser.php";
    private Map<String, String> params;

    public RegisterRequest(String login, String password, String username, String deviceID, String phonenumber, /*int firstread,*/ Response.Listener<String> listener){
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("login", login);
        params.put("password", password);
        params.put("deviceID", deviceID);
        params.put("username", username);
        params.put("phone", phonenumber);
        //params.put("firstread", firstread);
    }
    @Override
    public Map<String,String> getParams(){
        return params;
    }
}

