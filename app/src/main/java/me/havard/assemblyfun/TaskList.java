package me.havard.assemblyfun;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import me.havard.assemblyfun.me.havard.assemblyfun.data.me.haved.assemblyfun.data.tables.TaskinfoTable;

public class TaskList extends AppCompatActivity {

    public static final String HIDE_UNSOLVED_FIRST_OPTION_ID = "hide_action_unsolved_first";
    public static final String RES_ACTIVITY_TITLE = "activity_title_res";
    public static final String REF_TASKINFO_TABLE_ID_TABLE_NAME = "task_id_table_name";

    private static final String[] FROM_COLUMNS = new String[] {TaskinfoTable.NAME, TaskinfoTable.DESC, TaskinfoTable.DIFFICULTY, TaskinfoTable.AUTHOR};
    private static final int[] TO_TEXT_VIEWS = new int[]{R.id.task_list_item_title, R.id.task_list_item_desc, R.id.task_list_item_difficulty, R.id.task_list_item_author};

    private static final String QUERY_START = "SELECT " + TaskinfoTable.NAME + ", "  + TaskinfoTable.DESC + ", " + TaskinfoTable.DIFFICULTY + ", " + TaskinfoTable.AUTHOR + ", " + TaskinfoTable._ID + " AS _id" +
            " FROM " + TaskinfoTable.TABLE_NAME + ", ";
    private static final String QUERY_MID = " WHERE " +TaskinfoTable.TABLE_NAME+"."+TaskinfoTable._ID + " = ";
    private static final String QUERY_END = "." + TaskinfoTable.REF_ID;

    private boolean mHideUnsolvedFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            Log.e("Assembly Fun", "No extra bundle was supplied to the task list!!!");
            return;
        }

        mHideUnsolvedFirst = extras.getBoolean(HIDE_UNSOLVED_FIRST_OPTION_ID);
        setTitle(extras.getInt(RES_ACTIVITY_TITLE, R.string.title_unset));

        SQLiteDatabase db = ((AssemblyFunApplication) getApplication()).getReadableDatabase();
        String fkDatabaseName = extras.getString(REF_TASKINFO_TABLE_ID_TABLE_NAME);

        Log.d("Assembly Fun", QUERY_START + fkDatabaseName + QUERY_MID + fkDatabaseName + QUERY_END);

        Cursor cursor = db.rawQuery(QUERY_START + fkDatabaseName + QUERY_MID + fkDatabaseName + QUERY_END, null);

        SimpleCursorAdapter listItems = new SimpleCursorAdapter(this, R.layout.task_list_item, cursor, FROM_COLUMNS, TO_TEXT_VIEWS, 0);
        ListView list = (ListView)findViewById(R.id.task_list_view);
        list.setAdapter(listItems);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        if(mHideUnsolvedFirst)
            menu.removeItem(R.id.action_unsolved_first);
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
