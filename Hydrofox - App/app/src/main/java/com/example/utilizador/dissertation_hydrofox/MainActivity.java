package com.example.utilizador.dissertation_hydrofox;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.R.*;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.button;
import static com.example.utilizador.dissertation_hydrofox.R.styleable.AlertDialog;

public class MainActivity extends AppCompatActivity {
    private Button btnDate;
    private TextView tvDayUse;
    private TextView tvMonthUse;
    private TextView tvMonthcost;
    private TextView tvUsername;
    private Integer year, month, day;
    static final int DIALOG_ID = 0;
    String newDate;
    String deviceidString;
    private String[] arraySpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Verificar se há login feito
        final SharedPreferencesUtility loginHelper = new SharedPreferencesUtility(MainActivity.this);
        if (loginHelper.getPassword() == null || loginHelper.getLogin() == null || loginHelper.getUsername() == null){
            Intent loginIntent = new Intent (MainActivity.this, loginActivity.class);
            startActivity(loginIntent);
        }
        deviceidString = loginHelper.getDeviceID();
        String usernameString = loginHelper.getUsername();

        //Verificar ligação à Internet
        Integer access = NetworkUtil.getConnectivityStatus(this);
        if (access == 0)
            Toast.makeText(MainActivity.this, "Ligar à Internet", Toast.LENGTH_LONG).show();

        //Setup da Toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Inicializar coisas
        tvUsername = (TextView)findViewById(R.id.tvUsername);
        tvDayUse = (TextView)findViewById(R.id.tvDayUse);
        tvMonthUse = (TextView)findViewById(R.id.tvMonthUse);
        tvMonthcost = (TextView)findViewById(R.id.tvMonthcost);

        tvUsername.setText("Bem-vindo, "+usernameString);
        btnDate = (Button)findViewById(R.id.btnDate);

        //Ir buscar data
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/d");
        cal.add(Calendar.DATE, -1);
        String monthString  = (String) DateFormat.format("MMMM", cal);
        String dateString = sdf.format(cal.getTime());
        btnDate.setText(dateString);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        //Ir buscar consumos do dia/ mês
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean good = jsonResponse.getBoolean("good");
                    if (good){
                        tvDayUse.setText("Consumo do dia: "+jsonResponse.getString("dia")+" Litros");
                        tvMonthUse.setText("Consumo do mês: "+jsonResponse.getString("mes")+" Litros");
                        Integer consumoMes = Integer.parseInt(jsonResponse.getString("mes"));
                        Float custoMes = 0.1f;
                        Float custo = (float)consumoMes*custoMes;
                        tvMonthcost.setText("Valor estimado do custo: "+custo.toString()+" €");
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Impossível de receber resultados. Verifique a sua ligação ou tente mais tarde.",Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        GetReadingsMain newReading = new GetReadingsMain(deviceidString, dateString, month+1, responseListener);
        RequestQueue queueA = Volley.newRequestQueue(MainActivity.this);
        newReading.setRetryPolicy(new DefaultRetryPolicy(0,-1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queueA.add(newReading);

        showDialogOnBtnClick();
    }

    //Ter a seleção da data e atualizar consumos ao mostrar data.
    public void showDialogOnBtnClick(){
        btnDate = (Button)findViewById(R.id.btnDate);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if (id == DIALOG_ID)
            return new DatePickerDialog(this, datePickListener, year, month, day);
        else
            return null;
    }

    private DatePickerDialog.OnDateSetListener datePickListener = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year_x, int month_x, int date_x){
            year = year_x;
            month = month_x+1;
            day = date_x;
            newDate = year+"/"+month+"/"+day;
            btnDate.setText(newDate);

            //Ir buscar consumos do dia/ mês
            Response.Listener<String> responseListener = new Response.Listener<String>(){
                @Override
                public void onResponse(String response) {
                    try {
                        //Toast.makeText(MainActivity.this, response,Toast.LENGTH_LONG).show();
                        //tvUsername.setText(response);
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean good = jsonResponse.getBoolean("good");
                        if (good){
                            tvDayUse.setText("Consumo do dia: "+jsonResponse.getString("dia")+" Litros");
                            tvMonthUse.setText("Consumo do mês: "+jsonResponse.getString("mes")+" Litros");
                            Integer consumoMes = Integer.parseInt(jsonResponse.getString("mes"));
                            Float custoMes = 0.1f;
                            Float custo = (float)consumoMes*custoMes;
                            tvMonthcost.setText("Valor estimado do custo: "+custo.toString()+" €");
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Impossível de receber resultados. Verifique a sua ligação ou tente mais tarde.",Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            GetReadingsMain reading = new GetReadingsMain(deviceidString, newDate, month, responseListener);
            RequestQueue queueB = Volley.newRequestQueue(MainActivity.this);
            reading.setRetryPolicy(new DefaultRetryPolicy(0,-1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queueB.add(reading);
        }
    };


    //Inflate the menu + put some links
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.my_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_aboutUs){
            Intent about = new Intent(MainActivity.this, aboutActivity.class);
            startActivity(about);
        }

        if (item.getItemId() == R.id.action_setting){
            Intent setting = new Intent(MainActivity.this, settingsActivity.class);
            startActivity(setting);
        }

        if (item.getItemId() == R.id.action_graph){
            Intent about = new Intent(MainActivity.this, graphsActivity.class);
            startActivity(about);
        }

        if (item.getItemId() == R.id.action_stats){
            Intent setting = new Intent(MainActivity.this, statsActivity.class);
            startActivity(setting);
        }

        if (item.getItemId() == R.id.action_logout){
            SharedPreferencesUtility logout = new SharedPreferencesUtility(this);
            logout.deleteAll();
            Intent about = new Intent(MainActivity.this, loginActivity.class);
            startActivity(about);
        }

        return super.onOptionsItemSelected(item);
    }
}
