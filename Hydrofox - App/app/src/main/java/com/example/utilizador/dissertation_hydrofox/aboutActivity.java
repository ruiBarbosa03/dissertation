package com.example.utilizador.dissertation_hydrofox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class aboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

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
            Intent about = new Intent(aboutActivity.this, aboutActivity.class);
            startActivity(about);
        }

        if (item.getItemId() == R.id.action_setting){
            Intent setting = new Intent(aboutActivity.this, settingsActivity.class);
            startActivity(setting);
        }

        if (item.getItemId() == R.id.action_graph){
            Intent about = new Intent(aboutActivity.this, graphsActivity.class);
            startActivity(about);
        }

        if (item.getItemId() == R.id.action_stats){
            Intent setting = new Intent(aboutActivity.this, statsActivity.class);
            startActivity(setting);
        }

        if (item.getItemId() == R.id.action_logout){
            SharedPreferencesUtility logout = new SharedPreferencesUtility(this);
            logout.deleteAll();
            Intent about = new Intent(aboutActivity.this, loginActivity.class);
            startActivity(about);
        }

        return super.onOptionsItemSelected(item);
    }


}
