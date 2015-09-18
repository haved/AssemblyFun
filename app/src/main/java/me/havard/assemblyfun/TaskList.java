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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import me.havard.assemblyfun.me.havard.assemblyfun.data.TaskCursorAdapter;
import me.havard.assemblyfun.me.havard.assemblyfun.data.me.haved.assemblyfun.data.tables.TaskinfoTable;

public class TaskList extends AppCompatActivity {

    public static final String HIDE_UNSOLVED_FIRST_OPTION_ID = "hide_action_unsolved_first";
    public static final String RES_ACTIVITY_TITLE = "activity_title_res";
    public static final String REF_TASKINFO_TABLE_ID_TABLE_NAME = "task_id_table_name";

    private boolean mHideUnsolvedFirst;
    protected String fkDatabaseName;
    protected int title;
    protected ListView list;
    protected TextView search_status;
    protected ImageButton reset_filter;
    protected TaskCursorAdapter listItems;

    protected String currentSearch;

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
        fkDatabaseName = extras.getString(REF_TASKINFO_TABLE_ID_TABLE_NAME);
        list = (ListView)findViewById(R.id.task_list_view);
        search_status = (TextView)findViewById(R.id.task_list_label_serach_status);
        reset_filter = (ImageButton)findViewById(R.id.task_list_reset_filter);
        title = extras.getInt(RES_ACTIVITY_TITLE, R.string.title_unset);
        setTitle(title);

        updateList(null);
    }

    private void updateList(String search) {
        if(search.equals(""))
            search=null;
        currentSearch = search;
        SQLiteDatabase db = ((AssemblyFunApplication) getApplication()).getReadableDatabase();
        String query = getQueryText(fkDatabaseName, currentSearch == null ? null : ("(" + TaskinfoTable.NAME + " LIKE '%" + currentSearch + "%' OR " + TaskinfoTable.DESC + " LIKE '%" + currentSearch + "%')"));
        Log.d("Assembly Fun", query);
        Cursor cursor = db.rawQuery(query, null);
        listItems = new TaskCursorAdapter(this, cursor);
        list.setAdapter(listItems);

        String baseLabel;
        if(currentSearch == null) {
            baseLabel = getResources().getString(R.string.label_task_list_no_filter);
            reset_filter.setVisibility(View.GONE);
        } else {
            baseLabel = "Searching for: '" + currentSearch + "'";
            reset_filter.setVisibility(View.VISIBLE);
        }
        int count = listItems.getCount();
        search_status.setText(baseLabel + " - " + count + " " + getResources().getString(count == 1 ? R.string.label_task_list_item_shown : R.string.label_task_list_items_shown));
    }

    public void onResetFilterButtonPressed(View view)
    {
        updateList(null);
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.dialog_title_search_task_list);
            final EditText text = new EditText(this);
            text.setInputType(InputType.TYPE_CLASS_TEXT);
            text.setText(currentSearch);
            dialog.setView(text);

            dialog.setPositiveButton(R.string.dialog_button_OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateList(text.getText().toString().split("'")[0]);
                }
            });
            dialog.setNegativeButton(R.string.dialog_button_Cancel, new DialogInterface.OnClickListener() {
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

    protected static String getQueryText(String databaseName, String extraWhere)
    {
        return QUERY_START + databaseName + QUERY_MID + databaseName + QUERY_END + (extraWhere==null ? "" : (" AND " + extraWhere));
    }
}
