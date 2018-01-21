package com.example.utilizador.dissertation_hydrofox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class graphsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);

        Integer access = NetworkUtil.getConnectivityStatus(this);
        if (access == 0)
            Toast.makeText(graphsActivity.this, "Ligar à Internet", Toast.LENGTH_LONG).show();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.my_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_aboutUs){
            Intent about = new Intent(graphsActivity.this, aboutActivity.class);
            startActivity(about);
        }

        if (item.getItemId() == R.id.action_setting){
            Intent setting = new Intent(graphsActivity.this, settingsActivity.class);
            startActivity(setting);
        }

        if (item.getItemId() == R.id.action_graph){
            Intent about = new Intent(graphsActivity.this, graphsActivity.class);
            startActivity(about);
        }

        if (item.getItemId() == R.id.action_stats){
            Intent setting = new Intent(graphsActivity.this, statsActivity.class);
            startActivity(setting);
        }

        if (item.getItemId() == R.id.action_logout){
            SharedPreferencesUtility logout = new SharedPreferencesUtility(this);
            logout.deleteAll();
            Intent about = new Intent(graphsActivity.this, loginActivity.class);
            startActivity(about);
        }

        return super.onOptionsItemSelected(item);
    }
}
