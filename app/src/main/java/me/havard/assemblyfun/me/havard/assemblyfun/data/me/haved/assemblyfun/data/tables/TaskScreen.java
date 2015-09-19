package me.havard.assemblyfun.me.havard.assemblyfun.data.me.haved.assemblyfun.data.tables;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import me.havard.assemblyfun.AssemblyFunApplication;
import me.havard.assemblyfun.R;

public class TaskScreen extends AppCompatActivity {

    public static final String EXTRAS_TASK_ID = "ExtrasTaskID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_screen);

        String query = getTaskQuery(getIntent().getExtras().getLong(EXTRAS_TASK_ID));
        Log.d("Assembly Fun", query);
        Cursor c = ((AssemblyFunApplication) getApplication()).getReadableDatabase().rawQuery(query, null);
        c.moveToNext();
        setTitle(c.getString(c.getColumnIndex(TaskinfoTable.NAME)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_screen, menu);
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

    private static final String TASK_QUERY_BEGIN = "SELECT " + TaskinfoTable.NAME + " FROM " + TaskinfoTable.TABLE_NAME + " WHERE " + TaskinfoTable._ID_TaskIDs + " = '";
    private static String getTaskQuery(long taskID)
    {
        return TASK_QUERY_BEGIN + taskID + "'";
    }
}
