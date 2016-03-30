package com.mk.familyweighttracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.activeandroid.ActiveAndroid;
import com.mk.familyweighttracker.Activities.UsersListActivity;

public class HomeActivity extends AppCompatActivity {

    private static final String DATABASE_NAME = "MkWeighTracker.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //    https://github.com/pardom/ActiveAndroid/wiki/Getting-started
        //ActiveAndroid.dispose();
        //getApplicationContext().deleteDatabase(DATABASE_NAME);
        ActiveAndroid.initialize(getApplicationContext());
        //Configuration.Builder configurationBuilder = new Configuration.Builder(this);
        //configurationBuilder.addModelClass(UserModel.class);
        //ActiveAndroid.initialize(configurationBuilder.create());

        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_home);
        setSupportActionBar(toolbar);

        Button startButton = ((Button) findViewById(R.id.button_home_start));
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UsersListActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        ActiveAndroid.dispose();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
