package com.example.utilizador.dissertation_hydrofox;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ImageButton;
import android.view.View;
import android.view.View.OnClickListener;

public class settingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Check internet access
        Integer access = NetworkUtil.getConnectivityStatus(this);
        if (access == 0)
            Toast.makeText(settingsActivity.this, "Ligar à Internet", Toast.LENGTH_LONG).show();

        //Make the toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Inicializar coisas
        SharedPreferencesUtility settingsHelper = new SharedPreferencesUtility(this);
        Boolean switchFugas = settingsHelper.getFugas();
        Boolean switchModo = settingsHelper.getModo();
        Integer fugasLiters = settingsHelper.getFugasLiters();
        String usernameEdit = settingsHelper.getUsername();

        final EditText etUsernameEdit = (EditText)findViewById(R.id.etUsernameEdit);
        final Switch swFerias = (Switch)findViewById(R.id.swModoFunc);
        final Switch swFugas = (Switch)findViewById(R.id.swFugas);
        final TextView tvFugas = (TextView)findViewById(R.id.tvLitersPerHour);
        final TextView tvSendTime = (TextView)findViewById(R.id.tvSendTime);
        final SeekBar sbFugas = (SeekBar)findViewById(R.id.sbFugas);
        final SeekBar sbSendTime = (SeekBar) findViewById(R.id.sbSendTime);
        final ImageButton btnInfoModo = (ImageButton)findViewById(R.id.btnInfoModo);
        final ImageButton btnInfoFugas = (ImageButton)findViewById(R.id.btnInfoFugas);

        //BUTÕES DE INFO
        btnInfoModo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(settingsActivity.this);
                builder.setMessage("Recebe uma notificação quando for detetado consumo de água.")
                        .setTitle("Modo Férias")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        btnInfoFugas.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(settingsActivity.this);
                builder.setMessage("Recebe uma notificação quando quantidade de água medida for superior ao estipulado.")
                        .setTitle("Deteção de Fugas")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        //FUGAS

        //Ir buscar valor do switch
        etUsernameEdit.setText(usernameEdit);
        swFugas.setChecked(switchFugas);
        swFerias.setChecked(switchModo);
        sbFugas.setProgress(fugasLiters);
        tvFugas.setText("Litros por hora: " + fugasLiters);

        swFugas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sbFugas.setVisibility(View.VISIBLE);
                    tvFugas.setVisibility(View.VISIBLE);
                }
                else {
                    sbFugas.setVisibility(View.GONE);
                    tvFugas.setVisibility(View.GONE);
                }
            }
        });

        if (swFugas.isChecked()){
            sbFugas.setVisibility(View.VISIBLE);
            tvFugas.setVisibility(View.VISIBLE);
        }
        else{
            sbFugas.setVisibility(View.GONE);
            tvFugas.setVisibility(View.GONE);
        }

        sbFugas.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvFugas.setText("Litros por hora: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbSendTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress){
                    case 0: tvSendTime.setText("Tempo de Envio: 6h"); break;
                    case 1: tvSendTime.setText("Tempo de Envio: 12h"); break;
                    case 2: tvSendTime.setText("Tempo de Envio: 24h"); break;
                    default: break;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }



    //Create toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.my_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_aboutUs){
            Intent about = new Intent(settingsActivity.this, aboutActivity.class);
            startActivity(about);
        }

        if (item.getItemId() == R.id.action_setting){
            Intent setting = new Intent(settingsActivity.this, settingsActivity.class);
            startActivity(setting);
        }

        if (item.getItemId() == R.id.action_graph){
            Intent about = new Intent(settingsActivity.this, graphsActivity.class);
            startActivity(about);
        }

        if (item.getItemId() == R.id.action_stats){
            Intent setting = new Intent(settingsActivity.this, statsActivity.class);
            startActivity(setting);
        }

        if (item.getItemId() == R.id.action_logout){
            SharedPreferencesUtility logout = new SharedPreferencesUtility(this);
            logout.deleteAll();
            Intent about = new Intent(settingsActivity.this, loginActivity.class);
            startActivity(about);
        }

        return super.onOptionsItemSelected(item);
    }
}
