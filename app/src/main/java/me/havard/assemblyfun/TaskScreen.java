package me.havard.assemblyfun;

import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

import me.havard.assemblyfun.data.AFDatabaseInteractionHelper;
import me.havard.assemblyfun.data.Difficulty;
import me.havard.assemblyfun.data.SQLiteCursorLoader;
import me.havard.assemblyfun.data.SharedPreferencesHelper;
import me.havard.assemblyfun.data.SolutionCursorAdapter;
import me.havard.assemblyfun.data.tables.SolutionsTable;
import me.havard.assemblyfun.data.tables.TaskRecordsTable;
import me.havard.assemblyfun.data.tables.TaskinfoTable;
import me.havard.assemblyfun.util.AllDoneCounter;
import me.havard.assemblyfun.util.DialogHelper;
import me.havard.assemblyfun.util.MonthLabels;
import me.havard.assemblyfun.util.RemoveAllTaskData;

public class TaskScreen extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, AllDoneCounter.AllDoneListener {

    public static final String EXTRAS_TASK_ID = "extrasTaskID";
    public static final int RESULT_CODE = 1;
    public static final String RESULT_LONG_REMOVE_LOCAL_TASK_ID = "removeLocalTask";
    public static final String RESULT_LONG_REMOVE_ALL_TASK_DATA_ID = "removeAllTaskData";
    private static final int LOADER_ID_TASK_CURSOR = 0;
    private static final int LOADER_ID_RECORDS_CURSOR = 1;
    private static final int LOADER_ID_SOLUTIONS_CURSOR = 2;

    private long mLocalID;

    private TextView mTaskTitle, mTaskDesc, mTaskDiff, mTaskDate, mTaskAuthor, mTaskRecordsText;
    private RatingBar mRatingBar;
    private LinearLayout mButtonList;
    private RelativeLayout mOnlineRow, mSelfPublishedRow;
    private Button mLocalButton, mOnlineButton, mPublishButton, mFavouriteButton, mAddSolutionButton;
    private ImageView mLocalIcon, mFavouriteIcon, mSolveIcon;

    private ListView mSolutionList;
    private SolutionCursorAdapter mSolutionListAdapter;

    private boolean mLocalFlag, mGlobalFlag, mSolvedFlag, mFavouriteFlag;
    private boolean mLocalRemovalBuffered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_screen);

        mSolutionList = (ListView) findViewById(R.id.task_screen_solution_list);
        mSolutionListAdapter = new SolutionCursorAdapter(this, null);
        mSolutionList.setAdapter(mSolutionListAdapter);
        mSolutionList.setOnItemClickListener(this);
        mSolutionList.setOnItemLongClickListener(this);

        View headerView = getLayoutInflater().inflate(R.layout.task_screen_header, null);
        mSolutionList.addHeaderView(headerView);

        mTaskTitle = (TextView) headerView.findViewById(R.id.task_screen_task_title);
        mTaskDesc = (TextView) headerView.findViewById(R.id.task_screen_task_desc);
        mTaskDiff = (TextView) headerView.findViewById(R.id.task_screen_task_diff);
        mTaskDate = (TextView) headerView.findViewById(R.id.task_screen_task_date);
        mTaskAuthor = (TextView) headerView.findViewById(R.id.task_screen_task_author);
        mTaskRecordsText = (TextView) headerView.findViewById(R.id.task_screen_task_records);

        mRatingBar = (RatingBar) findViewById(R.id.task_screen_task_rating_bar);

        mButtonList = (LinearLayout)headerView.findViewById(R.id.task_screen_button_list);
        mOnlineRow = (RelativeLayout)headerView.findViewById(R.id.task_screen_online_row);
        mSelfPublishedRow = (RelativeLayout)headerView.findViewById(R.id.task_screen_self_published_row);

        mLocalButton = (Button) headerView.findViewById(R.id.task_screen_local_button);
        mLocalButton.setOnClickListener(this);
        mOnlineButton = (Button) headerView.findViewById(R.id.task_screen_online_button);
        mOnlineButton.setOnClickListener(this);
        mPublishButton = (Button) headerView.findViewById(R.id.task_screen_publish_button);
        mPublishButton.setOnClickListener(this);
        mFavouriteButton = (Button) headerView.findViewById(R.id.task_screen_favourite_button);
        mFavouriteButton.setOnClickListener(this);
        mAddSolutionButton = (Button) headerView.findViewById(R.id.task_screen_add_solution_button);
        mAddSolutionButton.setOnClickListener(this);

        mLocalIcon = (ImageView) headerView.findViewById(R.id.task_screen_local_icon);
        mSolveIcon = (ImageView) headerView.findViewById(R.id.task_screen_solved_icon);
        mFavouriteIcon = (ImageView) headerView.findViewById(R.id.task_screen_favourite_icon);

        mTaskTitle.setText(R.string.label_loading);
        mButtonList.setVisibility(View.GONE);

        Bundle data = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();
        mLocalID = data.getLong(EXTRAS_TASK_ID);
        getLoaderManager().initLoader(LOADER_ID_TASK_CURSOR, null, this);
        getLoaderManager().initLoader(LOADER_ID_RECORDS_CURSOR, null, this);
        getLoaderManager().initLoader(LOADER_ID_SOLUTIONS_CURSOR, null, this);
    }

    protected void useTaskInfoCursor(Cursor cursor)
    {
        cursor.moveToFirst();
        if(cursor.isAfterLast()) {
            mTaskTitle.setText("No task with task id '" + mLocalID + "' was found in the database!");
            return;
        }

        mTaskTitle.setText(cursor.getString(cursor.getColumnIndex(TaskinfoTable.NAME)));
        mTaskDesc.setText(cursor.getString(cursor.getColumnIndex(TaskinfoTable.DESC)));
        mTaskAuthor.setText(cursor.getString(cursor.getColumnIndex(TaskinfoTable.AUTHOR)));
        Difficulty diff = Difficulty.values()[cursor.getInt(cursor.getColumnIndex(TaskinfoTable.DIFFICULTY))];
        mTaskDiff.setText(diff.getLabelId());
        mTaskDiff.setTextColor(ContextCompat.getColor(this, diff.getColorId()));
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(TaskinfoTable.DATE)));
        mTaskDate.setText(String.format("%s %s %s", getResources().getString(MonthLabels.RESOURCE_IDS[cl.get(Calendar.MONTH)]), cl.get(Calendar.DAY_OF_MONTH), cl.get(Calendar.YEAR)));

        mRatingBar.setRating(cursor.getFloat(cursor.getColumnIndex(TaskinfoTable.RATING)));

        mButtonList.setVisibility(View.VISIBLE);
        int flags = cursor.getInt(cursor.getColumnIndex(TaskinfoTable.FLAGS));

        useLocalGlobalSolvedFlags(TaskinfoTable.hasFlag(flags, TaskinfoTable.FLAG_LOCAL),
                TaskinfoTable.hasFlag(flags, TaskinfoTable.FLAG_GLOBAL),
                TaskinfoTable.hasFlag(flags, TaskinfoTable.FLAG_SOLVED),
                false); //We don't pretend to be deleting the task locally
        mSelfPublishedRow.setVisibility(TaskinfoTable.hasFlag(flags, TaskinfoTable.FLAG_SELF_PUBLISHED) ? View.VISIBLE : View.GONE);
        useFavouriteFlag(TaskinfoTable.hasFlag(flags, TaskinfoTable.FLAG_FAVOURITE));
    }

    protected void useRecordsCursor(Cursor cursor)
    {
        String recordText;
        cursor.moveToFirst();
        if(cursor.isAfterLast())
            recordText = getResources().getString(R.string.label_task_screen_no_records);
        else
            recordText = String.format("%s\n" +
                            "%s - '%s': %.2f, %s: %.2f\n" +
                            "%s - '%s': %d, %s: %d\n" +
                            "%s - '%s': %.2f, %s: %.2f", getResources().getString(R.string.label_task_screen_records__),
                    getResources().getString(R.string.label_task_screen_speed),
                    cursor.getString(cursor.getColumnIndex(TaskRecordsTable.SPEED_REC_NAME)), cursor.getFloat(cursor.getColumnIndex(TaskRecordsTable.SPEED_REC)),
                    getResources().getString(R.string.label_task_screen_records_you), cursor.getFloat(cursor.getColumnIndex(TaskRecordsTable.PERSONAL_SPEED_REC)),

                    getResources().getString(R.string.label_task_screen_size),
                    cursor.getString(cursor.getColumnIndex(TaskRecordsTable.SIZE_REC_NAME)), cursor.getInt(cursor.getColumnIndex(TaskRecordsTable.SIZE_REC)),
                    getResources().getString(R.string.label_task_screen_records_you), cursor.getInt(cursor.getColumnIndex(TaskRecordsTable.PERSONAL_SIZE_REC)),

                    getResources().getString(R.string.label_task_screen_memuse),
                    cursor.getString(cursor.getColumnIndex(TaskRecordsTable.MEMUSE_REC_NAME)), cursor.getFloat(cursor.getColumnIndex(TaskRecordsTable.MEMUSE_REC)),
                    getResources().getString(R.string.label_task_screen_records_you), cursor.getFloat(cursor.getColumnIndex(TaskRecordsTable.PERSONAL_MEMUSE_REC)));

        mTaskRecordsText.setText(recordText);
    }

    protected void useLocalGlobalSolvedFlags(boolean local, boolean global, boolean solved, boolean localRemovalBuffered)
    {
        if (localRemovalBuffered&&!local) throw new AssertionError();
        boolean actAsStoredLocally = local & !localRemovalBuffered;
        mLocalFlag = local;
        mGlobalFlag = global;
        mSolvedFlag = solved;
        mLocalRemovalBuffered = localRemovalBuffered;
        mLocalButton.setText(localRemovalBuffered ? R.string.label_task_screen_undo_remove_locally : (global ?
                (local ? R.string.label_task_screen_remove_locally : R.string.label_task_screen_store_locally)
                : R.string.label_task_screen_only_locally));
        mLocalButton.setEnabled(global);
        mLocalIcon.setVisibility(local ? View.VISIBLE : View.INVISIBLE);
        mOnlineRow.setVisibility(global ? View.VISIBLE : View.GONE);
        mAddSolutionButton.setText(actAsStoredLocally ? R.string.label_task_screen_add_solution : R.string.label_task_screen_only_local_tasks_can_be_solved);
        mAddSolutionButton.setEnabled(actAsStoredLocally);
        mSolveIcon.setVisibility(solved ? View.VISIBLE : View.INVISIBLE);
    }

    protected void useFavouriteFlag(boolean favourite) {
        mFavouriteFlag = favourite;
        mFavouriteButton.setText(favourite ? R.string.label_task_screen_un_favourite : R.string.label_task_screen_favourite);
        mFavouriteIcon.setVisibility(favourite ? View.VISIBLE : View.INVISIBLE);
    }

    protected void onSolutionAdded(long solution_id)
    {
        mAddSolutionButton.setEnabled(true);
        reloadSolutionList();
    }

    protected void reloadSolutionList() {
        Loader<?> solutions_loader = getLoaderManager().initLoader(LOADER_ID_SOLUTIONS_CURSOR, null, this);
        solutions_loader.reset();
        solutions_loader.startLoading();
    }

    @Override
    public void onClick(View v)
    {
        if(v==mLocalButton)
        {
            if(mLocalRemovalBuffered)
                useLocalGlobalSolvedFlags(mLocalFlag, mGlobalFlag, mSolvedFlag, false);   //No longer buffer local removal
            else if(mLocalFlag & mGlobalFlag) { //Just to make sure the task exists online before removing it.
                //noinspection ConstantConditions //I like it more when you can see the variables and how they don't change
                useLocalGlobalSolvedFlags(mLocalFlag/*true*/, mGlobalFlag/*true*/, mSolvedFlag, true); //Buffering removing the task locally. NB: mLocalFlag and mGlobalFlag are always true here. (See 'if')

                if(mLocalRemovalBuffered & !mSolvedFlag & !SharedPreferencesHelper.shouldKeepUnlistedTasks(SharedPreferencesHelper.getPreferences(this))) {
                    Snackbar bar = Snackbar.make(findViewById(R.id.task_screen_solution_list), R.string.snack_bar_unlisted_task, Snackbar.LENGTH_LONG);
                    bar.setAction(R.string.dialog_button_Info, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogHelper.makeDialogBuilder(TaskScreen.this, R.string.dialog_unlisted_task_title, R.string.dialog_unlisted_task_body, null,
                                    -1, R.string.dialog_button_OK, true).show();
                        }
                    });
                    bar.show();
                }
            }
        }
        else if(v==mFavouriteButton)
            new ChangeFavouriteStatusTask(mLocalID, !mFavouriteFlag).execute();
        else if(v==mAddSolutionButton){
            if(!mLocalFlag) //Has to be local. The button is in theory not enabled when it's not local, but just to be safe
                return;

            mAddSolutionButton.setEnabled(false);
            DialogHelper.makeInputDialogBuilder(this, R.string.dialog_title_name_new_solution, -1, getResources().getString(R.string.dialog_default_text_new_solution), new DialogHelper.TextDialogListener() {
                @Override
                public void onTextEntered(String text) {
                    new AddNewSolution(mLocalID, text).execute();
                }
            }, R.string.dialog_button_OK, R.string.dialog_button_Cancel).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent solutionEditor = new Intent(this, SolutionEditor.class);
        solutionEditor.putExtra(SolutionEditor.EXTRAS_SOLUTION_ID, id);
        solutionEditor.putExtra(SolutionEditor.EXTRAS_TASK_ID, mLocalID);
        startActivity(solutionEditor);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
        PopupMenu menu = new PopupMenu(this, view);
        menu.inflate(R.menu.menu_task_screen_solution_item);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                if(menuItemId==R.id.action_rename_solution) {
                    DialogHelper.makeInputDialogBuilder(TaskScreen.this, R.string.dialog_title_rename_solution, R.string.dialog_enter_new_name,
                            AFDatabaseInteractionHelper.getSolutionTitle(((AssemblyFunApplication)getApplication()).getReadableDatabase(), id),
                            new DialogHelper.TextDialogListener() {
                                @Override
                                public void onTextEntered(String text) {
                                    new RenameSolutionTask(((AssemblyFunApplication)getApplication()).getDatabase(), id, text).execute();
                                }
                            }, R.string.dialog_button_Rename, R.string.dialog_button_Cancel).show();
                    return true;
                }
                else if(menuItemId==R.id.action_copy_solution) {
                    DialogHelper.makeInputDialogBuilder(TaskScreen.this, R.string.dialog_title_copy_solution, R.string.dialog_enter_new_name,
                            AFDatabaseInteractionHelper.getSolutionTitle(((AssemblyFunApplication)getApplication()).getReadableDatabase(), id),
                            new DialogHelper.TextDialogListener() {
                                @Override
                                public void onTextEntered(String text) {
                                    new CopySolutionTask(((AssemblyFunApplication)getApplication()).getDatabase(), id, mLocalID, text).execute();
                                }
                            }, R.string.dialog_button_Copy, R.string.dialog_button_Cancel).show();
                    return true;
                }
                else if(menuItemId==R.id.action_delete_solution) {
                    DialogHelper.makeDialogBuilder(TaskScreen.this, R.string.dialog_title_delete_solution, R.string.dialog_message_you_sure_delete_solution,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new DeleteSolutionTask(((AssemblyFunApplication)getApplication()).getDatabase(), id).execute();
                                }
                            }, R.string.dialog_button_Delete, R.string.dialog_button_Cancel, true).show();
                    return true;
                }
                return false;
            }
        });
        menu.show();
        return true;
    }

    private class RenameSolutionTask extends AsyncTask<Void, Void, Void> {
        private SQLiteOpenHelper mDb;
        private long mSolutionId;
        private String mNewName;
        public RenameSolutionTask(SQLiteOpenHelper db, long solutionId, String newName) {
            mDb = db;
            mSolutionId = solutionId;
            mNewName = newName;
        }

        @Override
        protected Void doInBackground(Void... params) {
            AFDatabaseInteractionHelper.changeSolutionTitle(mDb.getWritableDatabase(), AFDatabaseInteractionHelper.getClearedContentValuesInstance(), mSolutionId, mNewName);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            reloadSolutionList();
        }
    }

    private class CopySolutionTask extends AsyncTask<Void, Void, Void> {
        private SQLiteOpenHelper mDb;
        private long mSolutionId, mTaskId;
        private String mNewName;
        public CopySolutionTask(SQLiteOpenHelper db, long solutionId, long taskId, String newName) {
            mDb = db;
            mSolutionId = solutionId;
            mTaskId = taskId;
            mNewName = newName;
        }

        @Override
        protected Void doInBackground(Void... params) {
            AFDatabaseInteractionHelper.copySolution(mDb.getWritableDatabase(), AFDatabaseInteractionHelper.getClearedContentValuesInstance(), mSolutionId, mTaskId, mNewName);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            reloadSolutionList();
        }
    }

    private class DeleteSolutionTask extends AsyncTask<Void, Void, Void> {
        private SQLiteOpenHelper mDb;
        private long mSolutionId;
        public DeleteSolutionTask(SQLiteOpenHelper db, long solutionId) {
            mDb = db;
            mSolutionId = solutionId;
        }

        @Override
        protected Void doInBackground(Void... params) {
            AFDatabaseInteractionHelper.deleteSolution(mDb.getWritableDatabase(), mSolutionId);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            reloadSolutionList();
        }
    }

    @Override
    public void finish()
    {
        Intent result = new Intent();
        boolean solvedOrKeepUnlisted = mSolvedFlag | SharedPreferencesHelper.shouldKeepUnlistedTasks(SharedPreferencesHelper.getPreferences(this));
        if((!mLocalFlag | mLocalRemovalBuffered) & !solvedOrKeepUnlisted) {
            Log.i("Assembly Fun", "The task on this task screen is not solved nor local, and shouldKeepUnlistedTasks is false. Prepare for deletion of all task data!");
            result.putExtra(RESULT_LONG_REMOVE_ALL_TASK_DATA_ID, mLocalID);

        }
        else if(mLocalFlag & mLocalRemovalBuffered & solvedOrKeepUnlisted) {
            Log.i("Assembly Fun", "The task on this task screen is buffered to be remove locally, but is solved or 'keep unlisted tasks' is true. Only removing local task");
            result.putExtra(RESULT_LONG_REMOVE_LOCAL_TASK_ID, mLocalID);
        }
        setResult(RESULT_CODE, result);
        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_help)
        {
            DialogHelper.makeDialogBuilder(this, R.string.help_task_screen_title, R.string.help_task_screen_body, null, -1, R.string.dialog_button_OK, true).show();
            return true;
        }
        else if(id== R.id.action_delete_all_task_data)
        {
            DialogHelper.makeDialogBuilder(this, R.string.dialog_task_screen_are_you_sure, R.string.dialog_task_screen_delete_task_data_body, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new RemoveAllTaskData(((AssemblyFunApplication)getApplication()).getDatabase(), mLocalID, new AllDoneCounter(TaskScreen.this)).execute();
                }
            }, R.string.dialog_button_OK, R.string.dialog_button_Cancel, true).show();
            return true;
        }
        else if(id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAllDone(AllDoneCounter counter) {
        finish(); //The remove all task data dialog initiated a task that activated this
    }

    @Override
    public void onSaveInstanceState(Bundle state)
    {
        super.onSaveInstanceState(state);
        state.putLong(EXTRAS_TASK_ID, mLocalID);
    }

    private static final String TASK_INFO_QUERY = String.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s=? LIMIT 1;",
            TaskinfoTable.NAME, TaskinfoTable.DESC, TaskinfoTable.DIFFICULTY, TaskinfoTable.DATE,
            TaskinfoTable.AUTHOR, TaskinfoTable.RATING, TaskinfoTable.FLAGS,
            TaskinfoTable.TABLE_NAME,
            TaskinfoTable._ID_TaskIDs);

    private static final String RECORDS_QUERY = String.format("SELECT %s.* FROM %s WHERE %s=? LIMIT 1;",
            TaskRecordsTable.TABLE_NAME, TaskRecordsTable.TABLE_NAME,
            TaskRecordsTable._ID_TaskIDs);

    private static final String SOLUTION_LIST_CURSOR_QUERY = String.format("SELECT %s, %s, %s, %s, %s, %s AS _id FROM %s WHERE %s=?",
            SolutionsTable.TITLE, SolutionsTable.SOLUTION_QUALITY, SolutionsTable.SPEED, SolutionsTable.SIZE, SolutionsTable.MEMUSE, SolutionsTable._ID,
            SolutionsTable.TABLE_NAME, SolutionsTable._ID_TaskIDs);
    @Override

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == LOADER_ID_TASK_CURSOR)
            return new SQLiteCursorLoader(this, ((AssemblyFunApplication)getApplication()).getDatabase(), TASK_INFO_QUERY, new String[]{Long.toString(mLocalID)}).setId(LOADER_ID_TASK_CURSOR);
        else if(id== LOADER_ID_RECORDS_CURSOR)
            return new SQLiteCursorLoader(this, ((AssemblyFunApplication)getApplication()).getDatabase(), RECORDS_QUERY, new String[]{Long.toString(mLocalID)}).setId(LOADER_ID_RECORDS_CURSOR);
        else //if(id == LOADER_ID_SOLUTIONS_CURSOR)
            return new SQLiteCursorLoader(this, ((AssemblyFunApplication)getApplication()).getDatabase(), SOLUTION_LIST_CURSOR_QUERY, new String[]{Long.toString(mLocalID)}).setId(LOADER_ID_SOLUTIONS_CURSOR);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId()==LOADER_ID_TASK_CURSOR)
            useTaskInfoCursor(data);
        else if(loader.getId()== LOADER_ID_RECORDS_CURSOR)
            useRecordsCursor(data);
        else if(loader.getId() == LOADER_ID_SOLUTIONS_CURSOR)
            mSolutionListAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId()==LOADER_ID_SOLUTIONS_CURSOR)
            mSolutionListAdapter.changeCursor(null);
    }

    private class ChangeFavouriteStatusTask extends AsyncTask<Long, Integer, Boolean> {

        private long mTaskID;
        private boolean mMakeFavourite;

        public ChangeFavouriteStatusTask(long taskID, boolean makeFavourite) {
            mTaskID = taskID;
            mMakeFavourite = makeFavourite;
        }

        @Override
        protected Boolean doInBackground(Long... params) {

            Boolean outcome = AFDatabaseInteractionHelper.changeFlag(((AssemblyFunApplication) getApplication()).getWritableDatabase(), AFDatabaseInteractionHelper.getClearedContentValuesInstance(),
                    mTaskID, TaskinfoTable.FLAG_FAVOURITE, mMakeFavourite);
            AFDatabaseInteractionHelper.clearContentValuesInstance();
            return outcome;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mFavouriteButton.setEnabled(true);
            useFavouriteFlag(result);
        }
    }

    private class AddNewSolution extends AsyncTask<Void, Void, Long> {

        private long mTaskID;
        private String mSolutionName;

        public AddNewSolution(long taskID, String solutionName) {
            mTaskID = taskID;
            mSolutionName = solutionName;
        }

        @Override
        protected Long doInBackground(Void... params) {
            return AFDatabaseInteractionHelper.addEmptySolution(((AssemblyFunApplication)getApplication()).getWritableDatabase(),
                    AFDatabaseInteractionHelper.getClearedContentValuesInstance(), mTaskID, mSolutionName, "");
        }

        @Override
        protected void onPostExecute(Long solutionId) {
            onSolutionAdded(solutionId);
        }
    }
}
