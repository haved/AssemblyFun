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

import java.util.ArrayList;

import me.havard.assemblyfun.data.SQLiteCursorLoader;
import me.havard.assemblyfun.data.TaskCursorAdapter;
import me.havard.assemblyfun.data.tables.TaskIDTable;
import me.havard.assemblyfun.data.tables.TaskinfoTable;

public class TaskList extends AppCompatActivity implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    public static final String KEY_HIDE_UNSOLVED_FILTER_OPTION_ID = "hide_action_unsolved_filter";
    public static final String KEY_HIDE_LOCAL_FILTER_OPTION_ID = "hide_action_local_filter";
    public static final String KEY_CHECK_COLUMN_NAME = "check_column_name";
    public static final String KEY_RES_ACTIVITY_TITLE = "activity_title_res";
    private static final int TASK_CURSOR_LOADER_ID = 0;

    private static final String KEY_SAVE_LIST_SEARCH = "key_list_search";
    private static final String KEY_SAVE_LIST_LOCAL_ONLY = "key_list_local_only";
    private static final String KEY_SAVE_LIST_UNSOLVED_ONLY = "key_list_unsolved_only";
    private static final String KEY_SAVE_LIST_SELF_PUBLISHED_ONLY = "key_list_self_published_only";

    private ListView list;
    private TaskCursorAdapter listAdapter;
    private TextView filterStatusText;
    private ImageButton clearSearchButton;

    private boolean mHideUnsolvedOnly;
    private boolean mHideLocalOnly;
    private String mCheckColumnName;
    private int mTitle;

    private boolean mListSelfPublishedOnly;
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

        mHideUnsolvedOnly = extras.getBoolean(KEY_HIDE_UNSOLVED_FILTER_OPTION_ID);
        mHideLocalOnly = extras.getBoolean(KEY_HIDE_LOCAL_FILTER_OPTION_ID);
        mCheckColumnName = extras.getString(KEY_CHECK_COLUMN_NAME);
        mTitle = extras.getInt(KEY_RES_ACTIVITY_TITLE, R.string.title_unset);
        setTitle(mTitle);

        if(savedInstanceBundle != null)
        {
            updateTaskList(savedInstanceBundle.getString(KEY_SAVE_LIST_SEARCH), savedInstanceBundle.getBoolean(KEY_SAVE_LIST_LOCAL_ONLY),
                    savedInstanceBundle.getBoolean(KEY_SAVE_LIST_UNSOLVED_ONLY), savedInstanceBundle.getBoolean(KEY_SAVE_LIST_SELF_PUBLISHED_ONLY));
        }
        else
            updateTaskList(null, false, false, false);
    }

    protected void updateTaskList(String search, boolean localOnly, boolean solvedOnly, boolean selfPublishedOnly)
    {
        if(search==null || search.equals(""))
            mListCurrentSearch = null;
        else
            mListCurrentSearch = search;
        mListLocalOnly = localOnly;
        mListSelfPublishedOnly = selfPublishedOnly;
        mListUnsolvedOnly = solvedOnly;

        filterStatusText.setText(R.string.label_task_list_loading_items);
        clearSearchButton.setVisibility(View.GONE);

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
        if(mListSelfPublishedOnly)
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
        ArrayList<String> whereArgs = new ArrayList<>();

        mCurrentQuery = makeQueryText(whereArgs, mCheckColumnName, mListCurrentSearch, mListLocalOnly, mListUnsolvedOnly, mListSelfPublishedOnly);

        String text = mCurrentQuery;
        int argsIndex = 0;
        for(int i = 0; i < text.length(); i++)
        {
            if(text.charAt(i)=='?')
            {
                text = text.substring(0, i) + whereArgs.get(argsIndex) + text.substring(i+1);
                argsIndex++;
            }
        }

        Log.d("Assembly Fun", "Using query (I think): " + text);

        return new SQLiteCursorLoader(this, ((AssemblyFunApplication)getApplication()).getDatabase(), mCurrentQuery, whereArgs.toArray(new String[whereArgs.size()]));
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
                    updateTaskList(text.getText().toString().split("'")[0], mListLocalOnly, mListUnsolvedOnly, mListSelfPublishedOnly);
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
        updateTaskList(null, mListLocalOnly, mListUnsolvedOnly, mListSelfPublishedOnly);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        getLoaderManager().destroyLoader(TASK_CURSOR_LOADER_ID);
    }

    @Override
    protected void onSaveInstanceState(Bundle saveInstanceState)
    {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putString(KEY_SAVE_LIST_SEARCH, mListCurrentSearch);
        saveInstanceState.putBoolean(KEY_SAVE_LIST_LOCAL_ONLY, mListLocalOnly);
        saveInstanceState.putBoolean(KEY_SAVE_LIST_UNSOLVED_ONLY, mListUnsolvedOnly);
        saveInstanceState.putBoolean(KEY_SAVE_LIST_SELF_PUBLISHED_ONLY, mListSelfPublishedOnly);
    }

    private static final String QUERY_START = "SELECT " + TaskinfoTable.NAME + ", "  + TaskinfoTable.DESC + ", " + TaskinfoTable.DIFFICULTY + ", " +
            TaskinfoTable.AUTHOR + ", " + TaskinfoTable.LOCAL + ", " + TaskinfoTable.SOLVED + ", " + TaskinfoTable.SELF_PUBLISHED + ", " + TaskinfoTable.FAVOURITE + ", "
            +  TaskinfoTable.TABLE_NAME + "." + TaskIDTable._ID_TaskIDs + " AS _id" +
            " FROM " + TaskinfoTable.TABLE_NAME + " WHERE ";

    private static final String SEARCH_STATEMENT = "(" + TaskinfoTable.NAME + " LIKE ? OR " + TaskinfoTable.DESC + " LIKE ? OR " + TaskinfoTable.AUTHOR + " LIKE ?)";
    private static final int SEARCH_STATEMENT_WHERE_ARG_COUNT = 3;

    protected static String makeQueryText(ArrayList<String> whereArgs, String checkColumnName, String search, boolean localOnly, boolean unsolvedOnly, boolean self_publishedOnly)
    {
        whereArgs.add("1"); //For the checkColumn='1'

        StringBuilder ands = new StringBuilder();
        if(search!=null) {
            ands.append(" AND ").append(SEARCH_STATEMENT);
            for(int i = 0; i < SEARCH_STATEMENT_WHERE_ARG_COUNT; i++)
                whereArgs.add("%"+search+"%");
        }
        if(localOnly) {
            ands.append(" AND ").append(TaskinfoTable.LOCAL).append(" = ?");
            whereArgs.add("1");
        }
        if(unsolvedOnly) {
            ands.append(" AND ").append(TaskinfoTable.SOLVED).append(" = ?");
            whereArgs.add("0");
        }
        if(self_publishedOnly) {
            ands.append(" AND ").append(TaskinfoTable.SELF_PUBLISHED).append(" = ?");
            whereArgs.add("1");
        }

        return QUERY_START + checkColumnName + " = ?" + ands.toString();
    }
}
