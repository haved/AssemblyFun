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
import me.havard.assemblyfun.util.AllDoneCounter;
import me.havard.assemblyfun.util.DialogHelper;
import me.havard.assemblyfun.util.RemoveAllTaskData;
import me.havard.assemblyfun.util.RemoveTaskLocally;

public class TaskList extends AppCompatActivity implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>, AllDoneCounter.AllDoneListener {
    public static final String KEY_HIDE_UNSOLVED_FILTER_OPTION_ID = "hide_action_unsolved_filter";
    public static final String KEY_HIDE_LOCAL_FILTER_OPTION_ID = "hide_action_local_filter";
    public static final String KEY_CHECK_FLAG_BIT = "check_flag_bit";
    public static final String KEY_RES_ACTIVITY_TITLE = "activity_title_res";
    private static final int TASK_CURSOR_LOADER_ID = 0;
    private static final int REQUEST_CODE_TASK_SCREEN = 1;

    private static final String KEY_SAVE_LIST_SEARCH = "key_list_search";
    private static final String KEY_SAVE_LIST_LOCAL_ONLY = "key_list_local_only";
    private static final String KEY_SAVE_LIST_UNSOLVED_ONLY = "key_list_unsolved_only";
    private static final String KEY_SAVE_LIST_SELF_PUBLISHED_ONLY = "key_list_self_published_only";
    private static final String KEY_SAVE_LIST_FAVOURITES_ONLY = "key_list_favourites_only";
    private static final String KEY_SAVE_LIST_ORDER_BY_ID = "key_list_order_by_id";

    private ListView mListView;
    private TaskCursorAdapter mListAdapter;
    private TextView mListAdapterStatusText;
    private ImageButton mClearLawAndOrderButton;

    private boolean mHideUnsolvedOnly;
    private boolean mHideLocalOnly;
    private int mCheckFlagBit;
    private int mTitle;

    private boolean mListFilterLocal;
    private boolean mListFilterUnsolved;
    private boolean mListFilterSelfPublished;
    private boolean mListFilterFavourites;
    private String mListFilterText;
    private OrderBy mListOrderBy;
    private String mCurrentQuery;

    /**
     * Used to make sure all tasks are finished before loading the task screen.
     */
    private AllDoneCounter mAllDoneCounter;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_task_list);

        mListView = (ListView)findViewById(R.id.task_list_view);
        mListView.setOnItemClickListener(this);
        mListAdapter = new TaskCursorAdapter(this, null);
        mListView.setAdapter(mListAdapter);
        mListAdapterStatusText = (TextView)findViewById(R.id.task_list_label_serach_status);
        mClearLawAndOrderButton = (ImageButton)findViewById(R.id.task_list_reset_filter);

        Bundle extras = getIntent().getExtras();
        if(extras==null) {
            mListAdapterStatusText.setText("No bundle was passed to the TaskList. Expect some crashes!");
            return;
        }

        mHideUnsolvedOnly = extras.getBoolean(KEY_HIDE_UNSOLVED_FILTER_OPTION_ID, false);
        mHideLocalOnly = extras.getBoolean(KEY_HIDE_LOCAL_FILTER_OPTION_ID, false);
        mCheckFlagBit = extras.getInt(KEY_CHECK_FLAG_BIT, -1);
        mTitle = extras.getInt(KEY_RES_ACTIVITY_TITLE, R.string.title_unset);
        setTitle(mTitle);

        mAllDoneCounter = new AllDoneCounter(this);

        if(savedInstanceBundle != null)
        {
            updateTaskListQuery(savedInstanceBundle.getString(KEY_SAVE_LIST_SEARCH), savedInstanceBundle.getBoolean(KEY_SAVE_LIST_LOCAL_ONLY),
                    savedInstanceBundle.getBoolean(KEY_SAVE_LIST_UNSOLVED_ONLY), savedInstanceBundle.getBoolean(KEY_SAVE_LIST_SELF_PUBLISHED_ONLY),
                    savedInstanceBundle.getBoolean(KEY_SAVE_LIST_FAVOURITES_ONLY),
                    savedInstanceBundle.containsKey(KEY_SAVE_LIST_ORDER_BY_ID) ? OrderBy.values()[savedInstanceBundle.getInt(KEY_SAVE_LIST_ORDER_BY_ID)] : null,
                    false);
        }
    }

    private boolean mWaitForCounter;

    @Override
    protected void onStart() {
        Log.d("Assembly Fun", "onStart()!");
        if(mWaitForCounter) {
            mListAdapterStatusText.setText(R.string.label_task_list_loading_items);
            mClearLawAndOrderButton.setVisibility(View.GONE);
            mWaitForCounter = false;
        } else
            mAllDoneCounter.checkIfAllDone();
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent)
    {
        if(requestCode != REQUEST_CODE_TASK_SCREEN | resultCode != TaskScreen.RESULT_CODE) {
            Log.e("Assembly Fun", "The Activity result received by the TaskList didn't have the right requestCode and resultCode");
            return;
        }

        Bundle extras = resultIntent.getExtras();
        if(extras==null)
            return;
        long removeTaskLocallyID = extras.getLong(TaskScreen.RESULT_LONG_REMOVE_LOCAL_TASK_ID, -1);
        long removeAllTaskDataTaskID = extras.getLong(TaskScreen.RESULT_LONG_REMOVE_ALL_TASK_DATA_ID, -1);
        if(removeAllTaskDataTaskID != -1){
            new RemoveAllTaskData(((AssemblyFunApplication)getApplication()).getDatabase(), removeAllTaskDataTaskID, mAllDoneCounter).execute();
        } else if(removeTaskLocallyID != -1){
            new RemoveTaskLocally(((AssemblyFunApplication)getApplication()).getDatabase(), removeTaskLocallyID, mAllDoneCounter).execute();
        }

        if(!mAllDoneCounter.isEmpty())
            mWaitForCounter = true;
    }

    protected void updateTaskListQuery(String search, boolean localOnly, boolean solvedOnly, boolean selfPublishedOnly, boolean favouritesOnly, OrderBy orderBy, boolean loadNewList) {
        if(search==null || search.equals(""))
            mListFilterText = null;
        else
            mListFilterText = search;
        mListFilterLocal = localOnly;
        mListFilterSelfPublished = selfPublishedOnly;
        mListFilterUnsolved = solvedOnly;
        mListFilterFavourites = favouritesOnly;
        mListOrderBy = orderBy;

        if(loadNewList)
            reloadTaskList();
    }

    protected void reloadTaskList(boolean restartLoader) {
        mListAdapterStatusText.setText(R.string.label_task_list_loading_items);
        mClearLawAndOrderButton.setVisibility(View.GONE);
        if(restartLoader)
            getLoaderManager().restartLoader(TASK_CURSOR_LOADER_ID, null, this);
        else
            getLoaderManager().initLoader(TASK_CURSOR_LOADER_ID, null, this);
        invalidateOptionsMenu();
    }

    protected void reloadTaskList()
    {
        reloadTaskList(true);
    }

    protected void useCursor(Cursor cursor) {
        mListAdapter.changeCursor(cursor);
        StringBuilder filterText = new StringBuilder();
        String orderedText = "";
        if(mListFilterText != null)
            filterText.append("'").append(mListFilterText).append("'");
        if(mListFilterLocal)
            filterText.append(", ").append(getResources().getString(R.string.label_task_list_filter_local));
        if(mListFilterUnsolved)
            filterText.append(", ").append(getResources().getString(R.string.label_task_list_filter_unsolved));
        if(mListFilterSelfPublished)
            filterText.append(", ").append(getResources().getString(R.string.label_task_list_filter_self_published));
        if(mListFilterFavourites)
            filterText.append(", ").append(getResources().getString(R.string.label_task_list_filter_favourites));

        if(mListOrderBy!=null)
            orderedText = ". " + getResources().getString(R.string.label_task_list_sorted_by) + getResources().getString(mListOrderBy.getLabelId());

        int count = mListAdapter.getCount();

        String countText = " - " + count + " " + getResources().getString(count == 1 ? R.string.label_task_list_item_shown : R.string.label_task_list_items_shown);

        if(filterText.length()>1/*For a reason*/){
            if(filterText.substring(0, 2).equals(", "))
                filterText.replace(0, 2, "");
            mListAdapterStatusText.setText(getResources().getString(R.string.label_task_list_filtering_by) + filterText.toString() + orderedText + countText);
        }
        else
            mListAdapterStatusText.setText(getResources().getString(R.string.label_task_list_no_filter) + filterText.toString() + orderedText + countText);

        if(filterText.length()+orderedText.length()>0)
            mClearLawAndOrderButton.setVisibility(View.VISIBLE);
        else
            mClearLawAndOrderButton.setVisibility(View.GONE);
    }

    protected void useNoCursor() {
        mListAdapter.changeCursor(null);
        mListAdapterStatusText.setText("The cursor was removed!");
    }

    @Override
    public void onAllDone(AllDoneCounter counter) {
        reloadTaskList(false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        ArrayList<String> whereArgs = new ArrayList<>();

        mCurrentQuery = makeQueryText(whereArgs, mCheckFlagBit, mListFilterText, mListFilterLocal, mListFilterUnsolved, mListFilterSelfPublished, mListFilterFavourites, mListOrderBy);
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
        startActivityForResult(task, REQUEST_CODE_TASK_SCREEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        if(mHideUnsolvedOnly)
            menu.removeItem(R.id.action_task_list_filter_unsolved);
        if(mHideLocalOnly)
            menu.removeItem(R.id.action_task_list_filter_local);

        if(mListFilterLocal)
            menu.findItem(R.id.action_task_list_filter_local).setChecked(true);
        if(mListFilterUnsolved)
            menu.findItem(R.id.action_task_list_filter_unsolved).setChecked(true);
        if(mListFilterSelfPublished)
            menu.findItem(R.id.action_task_list_filter_self_published).setChecked(true);
        if(mListFilterFavourites)
            menu.findItem(R.id.action_task_list_filter_favourites).setChecked(true);

        if(mListOrderBy!=null) {
            MenuItem item = menu.findItem(mListOrderBy.getActionId());
            item.setCheckable(true);
            item.setChecked(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        boolean change = false;

        OrderBy newOrder=null;
        for(OrderBy order:OrderBy.values())
            if(id==order.getActionId()){
                newOrder = order;
                break;
            }

        if(id==R.id.action_task_list_filter_local) {
            mListFilterLocal = !mListFilterLocal;
            change = true;
        }
        else if(id==R.id.action_task_list_filter_unsolved) {
            mListFilterUnsolved = !mListFilterUnsolved;
            change = true;
        }
        else if(id==R.id.action_task_list_filter_self_published) {
            mListFilterSelfPublished = !mListFilterSelfPublished;
            change = true;
        }
        else if(id==R.id.action_task_list_filter_favourites) {
            mListFilterFavourites = !mListFilterFavourites;
            change = true;
        }

        if(newOrder!=null)
        {
            if(newOrder==mListOrderBy)
                mListOrderBy = null;
            else
                mListOrderBy = newOrder;
            change = true;
        }

        if(change){
            reloadTaskList();
            return true;
        }

        if(id == R.id.action_search) {
            DialogHelper.makeInputDialogBuilder(this, R.string.dialog_title_search_task_list, -1, mListFilterText, new DialogHelper.TextDialogListener() {
                @Override
                public void onTextEntered(String text) {
                    updateTaskListQuery(text.split("'")[0], mListFilterLocal, mListFilterUnsolved, mListFilterSelfPublished, mListFilterFavourites, mListOrderBy, true);
                }
            }, R.string.dialog_button_OK, R.string.dialog_button_Cancel).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onResetSearchButtonPressed(View view) {
        updateTaskListQuery(null, false, false, false, false, null, true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getLoaderManager().destroyLoader(TASK_CURSOR_LOADER_ID);
    }

    @Override
    protected void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putString(KEY_SAVE_LIST_SEARCH, mListFilterText);
        saveInstanceState.putBoolean(KEY_SAVE_LIST_LOCAL_ONLY, mListFilterLocal);
        saveInstanceState.putBoolean(KEY_SAVE_LIST_UNSOLVED_ONLY, mListFilterUnsolved);
        saveInstanceState.putBoolean(KEY_SAVE_LIST_SELF_PUBLISHED_ONLY, mListFilterSelfPublished);
        saveInstanceState.putBoolean(KEY_SAVE_LIST_FAVOURITES_ONLY, mListFilterFavourites);
        if(mListOrderBy!=null)
            saveInstanceState.putInt(KEY_SAVE_LIST_ORDER_BY_ID, mListOrderBy.ordinal());
    }

    private static final String QUERY_START = "SELECT " + TaskinfoTable.NAME + ", "  + TaskinfoTable.DESC + ", " + TaskinfoTable.DIFFICULTY + ", " +
            TaskinfoTable.AUTHOR + ", " + TaskinfoTable.FLAGS + ", "
            +  TaskinfoTable.TABLE_NAME + "." + TaskIDTable._ID_TaskIDs + " AS _id" +
            " FROM " + TaskinfoTable.TABLE_NAME + " WHERE (" + TaskinfoTable.FLAGS + "&?)!=0 ";

    private static final String SEARCH_STATEMENT = "(" + TaskinfoTable.NAME + " LIKE ? OR " + TaskinfoTable.DESC + " LIKE ? OR " + TaskinfoTable.AUTHOR + " LIKE ?)";
    private static final int SEARCH_STATEMENT_WHERE_ARG_COUNT = 3;

    private static final String AND_FLAGS_ND = " AND (" + TaskinfoTable.FLAGS + "&";

    protected static String makeQueryText(ArrayList<String> whereArgs, int mCheckFlagBit, String search,
                                          boolean localOnly, boolean unsolvedOnly, boolean self_publishedOnly, boolean favouritesOnly, OrderBy orderBy) {
        whereArgs.add(Integer.toString(mCheckFlagBit));

        StringBuilder lawAndOrderStatement = new StringBuilder();
        if(search!=null) {
            lawAndOrderStatement.append(" AND ").append(SEARCH_STATEMENT);
            for(int i = 0; i < SEARCH_STATEMENT_WHERE_ARG_COUNT; i++)
                whereArgs.add("%"+search+"%");
        }
        if(localOnly)
            lawAndOrderStatement.append(AND_FLAGS_ND).append(TaskinfoTable.FLAG_LOCAL).append(")=1");
        if(unsolvedOnly)
            lawAndOrderStatement.append(AND_FLAGS_ND).append(TaskinfoTable.FLAG_SOLVED).append(")=0");
        if(self_publishedOnly)
            lawAndOrderStatement.append(AND_FLAGS_ND).append(TaskinfoTable.FLAG_SELF_PUBLISHED).append(")=1");
        if(favouritesOnly)
            lawAndOrderStatement.append(AND_FLAGS_ND).append(TaskinfoTable.FLAG_FAVOURITE).append(")=1");
        if(orderBy!=null)
            lawAndOrderStatement.append(" ORDER BY ").append(orderBy.getOrderByStatement());

        Log.d("Assembly Fun", QUERY_START + lawAndOrderStatement.toString());

        return QUERY_START + lawAndOrderStatement.toString();
    }

    public enum OrderBy {
        NAME(TaskinfoTable.NAME, true, R.string.action_sort_by_name, R.id.action_task_list_sort_by_name),
        DATE(TaskinfoTable.DATE, false, R.string.action_sort_by_date, R.id.action_task_list_sort_by_date),
        AUTHOR(TaskinfoTable.AUTHOR, true, R.string.action_sort_by_author, R.id.action_task_list_sort_by_author),
        RATING(TaskinfoTable.RATING, false, R.string.action_sort_by_rating, R.id.action_task_list_sort_by_rating),
        DIFFICULTY(TaskinfoTable.DIFFICULTY, true, R.string.action_sort_by_difficulty, R.id.action_task_list_sort_by_difficulty);

        private String orderByStatement;
        private int label_string_id;
        private int action_id;

        OrderBy(String columnName, boolean ascending, int label_string_id, int action_id){
            this.orderByStatement = columnName + (ascending? " ASC": " DESC");
            this.label_string_id = label_string_id;
            this.action_id = action_id;
        }

        //* The order by SQL statement of this sorting, without the "ORDER BY "*/
        public String getOrderByStatement()
        {
            return orderByStatement;
        }

        public int getLabelId()
        {
            return label_string_id;
        }

        public int getActionId()
        {
            return action_id;
        }
    }
}
