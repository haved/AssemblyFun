package me.havard.assemblyfun;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import me.havard.assemblyfun.data.SharedPreferencesHelper;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    private Switch mAlwaysKeepTasks;
    private Button mDeleteUnusedRows, mVacuumDatabase, mDeleteUnlisted, mDeleteSolvedOnlyTasks;

    private boolean mKeepUnlistedTasksUsedToBe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAlwaysKeepTasks        = (Switch) findViewById(R.id.settings_keep_tasks_switch);
        mAlwaysKeepTasks        .setOnClickListener(this);
        mDeleteUnusedRows       = (Button) findViewById(R.id.settings_delete_unused_rows);
        mDeleteUnusedRows       .setOnClickListener(this);
        mVacuumDatabase         = (Button) findViewById(R.id.settings_vacuum_database);
        mVacuumDatabase         .setOnClickListener(this);
        mDeleteUnlisted         = (Button) findViewById(R.id.settings_delete_unlisted_tasks);
        mDeleteUnlisted         .setOnClickListener(this);
        mDeleteSolvedOnlyTasks  = (Button) findViewById(R.id.settings_delete_solved_only);
        mDeleteSolvedOnlyTasks  .setOnClickListener(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mKeepUnlistedTasksUsedToBe = SharedPreferencesHelper.alwaysKeepTasks(SharedPreferencesHelper.getPreferences(this));
        mAlwaysKeepTasks.setChecked(mKeepUnlistedTasksUsedToBe);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(mAlwaysKeepTasks.isChecked()!= mKeepUnlistedTasksUsedToBe) {
            SharedPreferences.Editor editor = SharedPreferencesHelper.getPreferences(this).edit();
            SharedPreferencesHelper.setShouldAlwaysKeepTasks(editor, !mKeepUnlistedTasksUsedToBe); //We know the boolean is the opposite of isChecked()
            editor.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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

    @Override
    public void onClick(View v) {

    }
}
