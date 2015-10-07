package me.havard.assemblyfun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class AssemblyFunMenu extends AppCompatActivity implements View.OnClickListener {

    private Button mSolveButton, mMakeButton, mSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assembly_fun_menu);

        mSolveButton        = (Button) findViewById(R.id.main_menu_solve_tasks);
        mSolveButton        .setOnClickListener(this);
        mMakeButton         = (Button) findViewById(R.id.main_menu_make_tasks);
        mMakeButton         .setOnClickListener(this);
        mSettingsButton     = (Button) findViewById(R.id.main_menu_settings_button);
        mSettingsButton     .setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==mSolveButton) {
            Intent intent = new Intent(this, SolveOptions.class);
            startActivity(intent);
        }
        else if(v==mSettingsButton) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_assembly_fun_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
