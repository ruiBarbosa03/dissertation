package com.example.utilizador.dissertation_hydrofox;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;

public class statsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        //Acesso à Internet
        Integer access = NetworkUtil.getConnectivityStatus(this);
        if (access == 0)
            Toast.makeText(statsActivity.this, "Ligar à Internet", Toast.LENGTH_LONG).show();

        //Set-up da Toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Ir buscar info ao SharedPreferences (só tem string do devideID)
        final SharedPreferencesUtility statsHelper = new SharedPreferencesUtility(statsActivity.this);
        final String deviceID = statsHelper.getDeviceID();

        //Construir ListedView com todas as estatísticas
        Response.Listener<String> statListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    populateListView(jsonResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        GetStats stats = new GetStats(deviceID, statListener);
        RequestQueue queueStats = Volley.newRequestQueue(statsActivity.this);
        stats.setRetryPolicy(new DefaultRetryPolicy(0,-1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queueStats.add(stats);

    }

    //Populate ListView
    private void populateListView(JSONObject jsonList) throws JSONException {
        final JSONObject list = jsonList;
        //Declarar títulos das views + parse do JSONObject para obter os valores
        String[] titles = new String[]{"Mês com maior consumo", "Mês com menor consumo", "Consumo mensal médio",
                           "Dia com maior consumo", "Dia com menor consumo", "Consumo diário médio",
                            "Consumo por dia da semana", "Consumo por ano"};
        String[] values = {list.getString("maxMonth")+" - "+list.getString("maxMonthLiters") + " Litros",
                           list.getString("minMonth")+" - "+list.getString("minMonthLiters") + " Litros",
                           list.getString("avgMonthLiters") + " Litros",
                           list.getString("maxDay")+" - "+list.getString("maxDayLiters") + " Litros",
                           list.getString("minDay")+" - "+list.getString("minDayLiters") + " Litros",
                           list.getString("avgDayLiters") + " Litros",
                            "Clique aqui", "Clique Aqui"};


        final ListView stats = (ListView) findViewById(R.id.listStats);
        ListViewAdapter adapter = new ListViewAdapter(this, titles, values);
        stats.setAdapter(adapter);

        stats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;

                if (itemPosition == 6) {
                    //Parse weekday INFO
                    String[] weekdays = {"2ªfeira", "3ªfeira", "4ªfeira", "5ªfeira", "6ªfeira", "Sábado", "Domingo"};
                    String weekdayConsumption = "";

                    for (int i = 0; i < 7; i++) {
                        try {
                            weekdayConsumption = weekdayConsumption + weekdays[i] + " - " + list.getJSONArray("wkdayStats").getJSONObject(i).getString("soma") + " Litros\n\n";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    //Show consumo por dia da semana
                    AlertDialog.Builder weekday = new AlertDialog.Builder(statsActivity.this);
                    weekday.setMessage(weekdayConsumption)
                            .setTitle("Consumo Semanal")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    AlertDialog weekdayDialog = weekday.create();
                    weekdayDialog.show();
                }
                else if (itemPosition == 7){
                //show consumo por ano
                    String  yearConsumption = "";
                    int size = 0;
                    try {
                        size = list.getJSONObject("yearStats").length();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < size-1 ; i++) {
                            try {
                                yearConsumption = yearConsumption + list.getJSONObject("yearStats").getString("ano") + " - " + list.getJSONObject("yearStats").getString("soma") + " Litros\n\n";
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    AlertDialog.Builder year = new AlertDialog.Builder(statsActivity.this);
                    year.setMessage(yearConsumption)
                            .setTitle("Consumo por Ano")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    AlertDialog yearDialog = year.create();
                    yearDialog.show();
                }



            }
        });
    }

    //Create and inflate toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.my_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_aboutUs){
            Intent about = new Intent(statsActivity.this, aboutActivity.class);
            startActivity(about);
        }

        if (item.getItemId() == R.id.action_setting){
            Intent setting = new Intent(statsActivity.this, settingsActivity.class);
            startActivity(setting);
        }

        if (item.getItemId() == R.id.action_graph){
            Intent about = new Intent(statsActivity.this, graphsActivity.class);
            startActivity(about);
        }

        if (item.getItemId() == R.id.action_stats){
            Intent setting = new Intent(statsActivity.this, statsActivity.class);
            startActivity(setting);
        }

        if (item.getItemId() == R.id.action_logout){
            SharedPreferencesUtility logout = new SharedPreferencesUtility(this);
            logout.deleteAll();
            Intent about = new Intent(statsActivity.this, loginActivity.class);
            startActivity(about);
        }

        return super.onOptionsItemSelected(item);
    }
}
