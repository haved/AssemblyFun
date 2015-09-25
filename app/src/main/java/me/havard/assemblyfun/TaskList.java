package me.havard.assemblyfun;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import me.havard.assemblyfun.data.SQLiteCursorLoader;
import me.havard.assemblyfun.data.TaskCursorAdapter;
import me.havard.assemblyfun.data.tables.TaskIDTable;
import me.havard.assemblyfun.data.tables.TaskinfoTable;

public class TaskList extends AppCompatActivity implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    public static final String HIDE_UNSOLVED_FILTER_OPTION_ID = "hide_action_unsolved_filter";
    public static final String HIDE_LOCAL_FILTER_OPTION_ID = "hide_action_local_filter";
    public static final String CHECK_COLUMN_NAME = "check_column_name";
    public static final String RES_ACTIVITY_TITLE = "activity_title_res";
    private static final int TASK_CURSOR_LOADER_ID = 0;

    private ListView list;
    private TaskCursorAdapter listAdapter;
    private TextView filterStatusText;
    private ImageButton clearSearchButton;

    private boolean mHideUnsolvedOnly;
    private boolean mHideLocalOnly;
    private String mCheckColumnName;
    private int mTitle;

    private boolean mListPublishedOnly;
    private boolean mListUnsolvedOnly;
    private boolean mListLocalOnly;
    private String mListCurrentSearch;
    private String mCurrentQuery;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_task_list);

        list = (ListView)findViewById(R.id.task_list_view);
        list.setOnItemClickListener(this);
        listAdapter = new TaskCursorAdapter(this, null);
        list.setAdapter(listAdapter);
        filterStatusText = (TextView)findViewById(R.id.task_list_label_serach_status);
        clearSearchButton = (ImageButton)findViewById(R.id.task_list_reset_filter);

        Bundle extras = getIntent().getExtras();
        if(extras==null) {
            filterStatusText.setText("No bundle was passed to the TaskList. Expect some crashes!");
            return;
        }

        mHideUnsolvedOnly = extras.getBoolean(HIDE_UNSOLVED_FILTER_OPTION_ID);
        mHideLocalOnly = extras.getBoolean(HIDE_LOCAL_FILTER_OPTION_ID);
        mCheckColumnName = extras.getString(CHECK_COLUMN_NAME);
        mTitle = extras.getInt(RES_ACTIVITY_TITLE, R.string.title_unset);
        setTitle(mTitle);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        updateTaskList(null, false, false, false);
    }

    protected void updateTaskList(String search, boolean localOnly, boolean solvedOnly, boolean selfPublishedOnly)
    {
        if(search==null || search.equals(""))
            mListCurrentSearch = null;
        else
            mListCurrentSearch = search;
        mListLocalOnly = localOnly;
        mListPublishedOnly = selfPublishedOnly;
        mListUnsolvedOnly = solvedOnly;

        filterStatusText.setText(R.string.label_task_list_loading_items);
        clearSearchButton.setVisibility(View.GONE);

        mCurrentQuery = getQueryText(mCheckColumnName, mListCurrentSearch, mListLocalOnly, mListUnsolvedOnly, mListPublishedOnly);
        Log.d("Assembly Fun", "Made a new query: " + mCurrentQuery);
        getLoaderManager().restartLoader(TASK_CURSOR_LOADER_ID, null, this);
    }

    protected void useCursor(Cursor cursor)
    {
        listAdapter.changeCursor(cursor);
        StringBuilder filterText = new StringBuilder();
        if(mListCurrentSearch!=null) {
            filterText.append("'").append(mListCurrentSearch).append("'");
        }
        if(mListLocalOnly)
            filterText.append(", ").append(getResources().getString(R.string.label_task_list_local_only));
        if(mListUnsolvedOnly)
            filterText.append(", ").append(getResources().getString(R.string.label_task_list_unsolved_only));
        if(mListPublishedOnly)
            filterText.append(", ").append(getResources().getString(R.string.label_task_list_self_published_only));

        filterStatusText.setText(getResources().getString(filterText.length() == 0 ? R.string.label_task_list_no_filter : R.string.label_task_list_filtering_by) + filterText.toString());
        if(mListCurrentSearch!=null)
            clearSearchButton.setVisibility(View.VISIBLE);
        else
            clearSearchButton.setVisibility(View.GONE);
    }

    protected void useNoCursor()
    {
        listAdapter.changeCursor(null);
        filterStatusText.setText("The cursor was removed!");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new SQLiteCursorLoader(this, ((AssemblyFunApplication)getApplication()).getDatabase(), mCurrentQuery);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        useCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        useNoCursor();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent task = new Intent(this, TaskScreen.class);
        task.putExtra(TaskScreen.EXTRAS_TASK_ID, id);
        startActivity(task);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        if(mHideUnsolvedOnly)
            menu.removeItem(R.id.action_task_list_filter_unsolved);
        if(mHideLocalOnly)
            menu.removeItem(R.id.action_task_list_filter_local);
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
            text.setText(mListCurrentSearch);
            dialog.setView(text);

            dialog.setPositiveButton(R.string.dialog_button_OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateTaskList(text.getText().toString().split("'")[0], mListLocalOnly, mListUnsolvedOnly, mListPublishedOnly);
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

        return super.onOptionsItemSelected(item);
    }

    public void onResetSearchButtonPressed(View view)
    {
        updateTaskList(null, mListLocalOnly, mListUnsolvedOnly, mListPublishedOnly);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        getLoaderManager().destroyLoader(TASK_CURSOR_LOADER_ID);
    }

    private static final String QUERY_START = "SELECT " + TaskinfoTable.NAME + ", "  + TaskinfoTable.DESC + ", " + TaskinfoTable.DIFFICULTY + ", " +
            TaskinfoTable.AUTHOR + ", " + TaskinfoTable.LOCAL + ", " + TaskinfoTable.SOLVED + ", " + TaskinfoTable.SELF_PUBLISHED + ", " +  TaskinfoTable.TABLE_NAME + "." + TaskIDTable._ID_TaskIDs + " AS _id" +
            " FROM " + TaskinfoTable.TABLE_NAME + " WHERE ";

    protected static String getQueryText(String checkColumnName, String search, boolean localOnly, boolean unsolvedOnly, boolean self_publishedOnly)
    {
        StringBuilder ands = new StringBuilder();
        if(search!=null) {
            ands.append(" AND ").append(getSearchStatement(search));
        }
        if(localOnly) {
            ands.append(" AND ").append(TaskinfoTable.LOCAL).append(" = '1'");
        }
        if(unsolvedOnly) {
            ands.append(" AND ").append(TaskinfoTable.SOLVED).append(" = '0'");
        }
        if(self_publishedOnly) {
            ands.append(" AND ").append(TaskinfoTable.SELF_PUBLISHED).append(" = '1'");
        }
        return QUERY_START + checkColumnName + " = '1' " + ands.toString();
    }

    public static String getSearchStatement(String search)
    {
        return "(" + TaskinfoTable.NAME + " LIKE '%" + search + "%' OR " + TaskinfoTable.DESC + " LIKE '%" + search + "%' OR " + TaskinfoTable.AUTHOR + " LIKE '%" + search + "%')";
    }
}

/*public class TaskList extends AppCompatActivity implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String HIDE_UNSOLVED_FIRST_OPTION_ID = "hide_action_unsolved_first";
    public static final String RES_ACTIVITY_TITLE = "activity_title_res";
    public static final String REF_TASKINFO_TABLE_ID_TABLE_NAME = "task_id_table_name";
    private static final int TASK_CURSOR_LOADER_ID = 0;

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

        Log.e("Assembly Fun", "getIntent().getExtras()!=null: " + (getIntent().getExtras() != null));
        Log.e("Assembly Fun", "savedInstanceState!=null: " + (savedInstanceState != null));

        list = (ListView)findViewById(R.id.task_list_view);
        list.setOnItemClickListener(this);
        listItems = new TaskCursorAdapter(this, null);
        list.setAdapter(listItems);
        search_status = (TextView)findViewById(R.id.task_list_label_serach_status);
        reset_filter = (ImageButton)findViewById(R.id.task_list_reset_filter);

        Bundle extras = getIntent().getExtras();
        if(extras==null) {
            search_status.setText("No bundle was passed to the TaskList. Expect some crashes!");
            return;
        }

        mHideUnsolvedFirst = extras.getBoolean(HIDE_UNSOLVED_FIRST_OPTION_ID);
        fkDatabaseName = extras.getString(REF_TASKINFO_TABLE_ID_TABLE_NAME);
        title = extras.getInt(RES_ACTIVITY_TITLE, R.string.title_unset);
        setTitle(title);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        updateList(null);
    }

    private void updateList(String search) {
        if (search != null && search.equals(""))
            search = null;
        currentSearch = search;
        search_status.setText(getResources().getString(R.string.label_task_list_loading_items));
        reset_filter.setVisibility(View.GONE);
        getLoaderManager().restartLoader(TASK_CURSOR_LOADER_ID, null, this);
    }

    private void useCursor(Cursor cursor) {
        listItems.changeCursor(cursor);

        String baseLabel;
        if (currentSearch == null) {
            baseLabel = getResources().getString(R.string.label_task_list_no_filter);
            reset_filter.setVisibility(View.GONE);
        } else {
            baseLabel = getResources().getString(R.string.label_task_list_searching_for)+"'" + currentSearch + "'";
            reset_filter.setVisibility(View.VISIBLE);
        }
        int count = listItems.getCount();
        search_status.setText(baseLabel + " - " + count + " " + getResources().getString(count == 1 ? R.string.label_task_list_item_shown : R.string.label_task_list_items_shown));
    }

    private void useNoCursor() {
        listItems.changeCursor(null);
    }

    public void onResetFilterButtonPressed(View view) {
        updateList(null);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        getLoaderManager().destroyLoader(TASK_CURSOR_LOADER_ID);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new SQLiteCursorLoader(this, ((AssemblyFunApplication)getApplication()).getDatabase(), getQueryText(fkDatabaseName, getSearchStatement(currentSearch)));
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        useCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        useNoCursor();
    }

    @Override
    public void onItemClick (AdapterView<?> parent, View view, int position, long id)
    {
        Intent task = new Intent(this, TaskScreen.class);
        task.putExtra(TaskScreen.EXTRAS_TASK_ID, id);
        startActivity(task);
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

        return super.onOptionsItemSelected(item);
    }

    private static final String QUERY_START = "SELECT " + TaskinfoTable.NAME + ", "  + TaskinfoTable.DESC + ", " + TaskinfoTable.DIFFICULTY + ", " +
            TaskinfoTable.AUTHOR + ", " + TaskinfoTable.TABLE_NAME + "." + TaskIDTable._ID_TaskIDs + " AS _id" +
            " FROM " + TaskinfoTable.TABLE_NAME + ", ";
    private static final String QUERY_MID = " WHERE " + TaskinfoTable.TABLE_NAME + "."+TaskIDTable._ID_TaskIDs + " = ";
    private static final String QUERY_END = "." + TaskIDTable._ID_TaskIDs;

    protected static String getQueryText(String databaseName, String extraWhere)
    {
        return QUERY_START + databaseName + QUERY_MID + databaseName + QUERY_END + (extraWhere==null ? "" : (" AND " + extraWhere));
    }

    private static String getSearchStatement(String search)
    {
        return search == null ? null : ("(" + TaskinfoTable.NAME + " LIKE '%" + search + "%' OR " + TaskinfoTable.DESC + " LIKE '%" + search + "%')");
    }

    /*@Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d("Assembly Fun", "TaskListActivity Instance saved!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        savedInstanceState.putBoolean(HIDE_UNSOLVED_FIRST_OPTION_ID, mHideUnsolvedFirst);
        savedInstanceState.putString(REF_TASKINFO_TABLE_ID_TABLE_NAME, fkDatabaseName);
        savedInstanceState.putInt(RES_ACTIVITY_TITLE, title);
    }*/
/*}*/
