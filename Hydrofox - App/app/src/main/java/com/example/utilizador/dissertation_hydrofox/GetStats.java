package com.example.utilizador.dissertation_hydrofox;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by utilizador on 07/05/2017.
 */

public class GetStats extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "https://paginas.fe.up.pt/~ee12014/sigfoxDatabase/getStats.php";
    private Map<String, String> params;

    public GetStats(String deviceID, Response.Listener<String> listener){
        super(Request.Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("deviceID", deviceID);
    }
    @Override
    public Map<String,String> getParams(){
        return params;
    }
}
