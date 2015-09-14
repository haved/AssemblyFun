package me.havard.assemblyfun;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import me.havard.assemblyfun.me.havard.assemblyfun.data.me.haved.assemblyfun.data.tables.TaskinfoTable;

public class TaskList extends AppCompatActivity {

    public static final String HIDE_UNSOLVED_FIRST_OPTION_ID = "hide_action_unsolved_first";
    public static final String RES_ACTIVITY_TITLE = "activity_title_res";
    public static final String REF_TASKINFO_TABLE_ID_TABLE_NAME = "task_id_table_name";

    private static final String[] FROM_COLUMNS = new String[] {TaskinfoTable.NAME, TaskinfoTable.DESC, TaskinfoTable.DIFFICULTY, TaskinfoTable.AUTHOR};
    private static final int[] TO_TEXT_VIEWS = new int[]{R.id.task_list_item_title, R.id.task_list_item_desc, R.id.task_list_item_difficulty, R.id.task_list_item_author};

    private boolean mHideUnsolvedFirst;
    private String fkDatabaseName;
    private int title;
    private ListView list;
    private SimpleCursorAdapter listItems;

    private String currentSearch;

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
        title = extras.getInt(RES_ACTIVITY_TITLE, R.string.title_unset);

        fkDatabaseName = extras.getString(REF_TASKINFO_TABLE_ID_TABLE_NAME);

        list = (ListView)findViewById(R.id.task_list_view);
        makeNewListAdapter(null);
    }

    private void makeNewListAdapter(String search) {
        SQLiteDatabase db = ((AssemblyFunApplication) getApplication()).getReadableDatabase();
        String query = getQueryText(fkDatabaseName, search == null ? null : ("(" + TaskinfoTable.NAME + " LIKE '%" + search + "%' OR " + TaskinfoTable.DESC + " LIKE '%" + search + "%')"));
        Log.d("Assembly Fun", query);
        Cursor cursor = db.rawQuery(query, null);
        listItems = new SimpleCursorAdapter(this, R.layout.task_list_item, cursor, FROM_COLUMNS, TO_TEXT_VIEWS, 0);
        list.setAdapter(listItems);

        currentSearch = search;

        if(currentSearch != null)
            setTitle("Search for: '"+currentSearch+"'");
        else
            setTitle(title);
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

        if(id == R.id.action_search) {
            if (currentSearch != null) {
                makeNewListAdapter(null);
                return true;
            }
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Enter search query:");
            final EditText text = new EditText(this);
            text.setInputType(InputType.TYPE_CLASS_TEXT);
            dialog.setView(text);

            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    makeNewListAdapter(text.getText().toString());
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            dialog.show();

            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final String QUERY_START = "SELECT " + TaskinfoTable.NAME + ", "  + TaskinfoTable.DESC + ", " + TaskinfoTable.DIFFICULTY + ", " + TaskinfoTable.AUTHOR + ", " + TaskinfoTable._ID + " AS _id" +
            " FROM " + TaskinfoTable.TABLE_NAME + ", ";
    private static final String QUERY_MID = " WHERE " +TaskinfoTable.TABLE_NAME+"."+TaskinfoTable._ID + " = ";
    private static final String QUERY_END = "." + TaskinfoTable.REF_ID;

    private static String getQueryText(String databaseName, String extraWhere)
    {
        return QUERY_START + databaseName + QUERY_MID + databaseName + QUERY_END + (extraWhere==null ? "" : (" AND " + extraWhere));
    }
}
