package com.example.utilizador.dissertation_hydrofox;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by utilizador on 04/05/2017.
 */

public class GetReadingsMain extends StringRequest{
    private static final String REGISTER_REQUEST_URL = "https://paginas.fe.up.pt/~ee12014/sigfoxDatabase/getReadings.php";
    private Map<String, String> params;

    public GetReadingsMain(String deviceID, String data, Integer month, Response.Listener<String> listener){
        super(Request.Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("data", data);
        params.put("month", month+"");
        params.put("deviceID", deviceID);
    }
    @Override
    public Map<String,String> getParams(){
        return params;
    }
}
