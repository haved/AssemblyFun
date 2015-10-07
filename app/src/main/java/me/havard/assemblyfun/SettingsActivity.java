package me.havard.assemblyfun;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import me.havard.assemblyfun.data.SharedPreferencesHelper;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    private Switch mAlwaysKeepTasks;
    private Button mDeleteUnusedRowsButton, mVacuumDatabaseButton, mDeleteUnlistedButton, mDeleteSolvedOnlyTasksButton;

    private boolean mKeepUnlistedTasksUsedToBe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAlwaysKeepTasks        = (Switch) findViewById(R.id.settings_keep_tasks_switch);
        mAlwaysKeepTasks        .setOnClickListener(this);
        mDeleteUnusedRowsButton = (Button) findViewById(R.id.settings_delete_unused_rows);
        mDeleteUnusedRowsButton.setOnClickListener(this);
        mVacuumDatabaseButton = (Button) findViewById(R.id.settings_vacuum_database);
        mVacuumDatabaseButton.setOnClickListener(this);
        mDeleteUnlistedButton = (Button) findViewById(R.id.settings_delete_unlisted_tasks);
        mDeleteUnlistedButton.setOnClickListener(this);
        mDeleteSolvedOnlyTasksButton = (Button) findViewById(R.id.settings_delete_solved_only);
        mDeleteSolvedOnlyTasksButton.setOnClickListener(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mKeepUnlistedTasksUsedToBe = SharedPreferencesHelper.shouldKeepUnlistedTasks(SharedPreferencesHelper.getPreferences(this));
        mAlwaysKeepTasks.setChecked(mKeepUnlistedTasksUsedToBe);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(mAlwaysKeepTasks.isChecked()!= mKeepUnlistedTasksUsedToBe) {
            SharedPreferences.Editor editor = SharedPreferencesHelper.getPreferences(this).edit();
            SharedPreferencesHelper.setKeepUnlistedTasks(editor, !mKeepUnlistedTasksUsedToBe); //We know the boolean is the opposite of isChecked()
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
        if(v== mVacuumDatabaseButton) {
            mVacuumDatabaseButton.setEnabled(false);
            new VacuumTask(((AssemblyFunApplication)getApplication()).getWritableDatabase()).execute();
        }
    }

    private class VacuumTask extends AsyncTask<Void, Void, Void>{

        private SQLiteDatabase mDB;

        public VacuumTask(SQLiteDatabase db) {
            mDB = db;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i("Assembly Fun", "Vacuuming database!");
            mDB.execSQL("VACUUM");
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            mVacuumDatabaseButton.setEnabled(true);
        }
    }
}
