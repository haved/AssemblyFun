package me.havard.assemblyfun;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

import me.havard.assemblyfun.data.AFDatabaseInteractionHelper;
import me.havard.assemblyfun.data.Difficulty;
import me.havard.assemblyfun.data.SQLiteCursorLoader;
import me.havard.assemblyfun.data.SolutionCursorAdapter;
import me.havard.assemblyfun.data.TaskInfoAndRecordsCursorLoader;
import me.havard.assemblyfun.data.tables.SolutionsTable;
import me.havard.assemblyfun.data.tables.TaskRecordsTable;
import me.havard.assemblyfun.data.tables.TaskinfoTable;
import me.havard.assemblyfun.util.MonthLabels;

public class TaskScreen extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    public static final String EXTRAS_TASK_ID = "extrasTaskID";
    private static final int LOADER_ID_TASK_CURSOR = 0;
    private static final int LOADER_ID_SOLUTIONS_CURSOR = 1;

    private long mLocalID;

    private TextView mTaskTitle, mTaskDesc, mTaskDiff, mTaskDate, mTaskAuthor, mTaskRecordsText;
    private LinearLayout mButtonList;
    private RelativeLayout mOnlineRow, mSelfPublishedRow;
    private Button mLocalButton, mOnlineButton, mPublishButton, mFavouriteButton, mAddSolutionButton;
    private ImageView mLocalIcon, mFavouriteIcon, mSolveIcon;

    private ListView mSolutionList;
    private SolutionCursorAdapter mSolutionListAdapter;

    private boolean mLocalFlag, mGlobalFlag, mSolvedFlag, mFavouriteFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_screen);

        mSolutionList = (ListView) findViewById(R.id.task_screen_solution_list);
        mSolutionListAdapter = new SolutionCursorAdapter(this, null);
        mSolutionList.setAdapter(mSolutionListAdapter);

        View headerView = getLayoutInflater().inflate(R.layout.task_screen_header, null);
        mSolutionList.addHeaderView(headerView);

        mTaskTitle = (TextView) headerView.findViewById(R.id.task_screen_task_title);
        mTaskDesc = (TextView) headerView.findViewById(R.id.task_screen_task_desc);
        mTaskDiff = (TextView) headerView.findViewById(R.id.task_screen_task_diff);
        mTaskDate = (TextView) headerView.findViewById(R.id.task_screen_task_date);
        mTaskAuthor = (TextView) headerView.findViewById(R.id.task_screen_task_author);
        mTaskRecordsText = (TextView) headerView.findViewById(R.id.task_screen_task_records);

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
        getLoaderManager().initLoader(LOADER_ID_SOLUTIONS_CURSOR, null, this);
    }

    protected void useTaskInfoAndRecordsCursor(Cursor cursor)
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
        mTaskDate.setText(getResources().getString(MonthLabels.RESOURCE_IDS[cl.get(Calendar.MONTH)]) + " " + cl.get(Calendar.DAY_OF_MONTH) + " " + cl.get(Calendar.YEAR));

        mButtonList.setVisibility(View.VISIBLE);
        int flags = cursor.getInt(cursor.getColumnIndex(TaskinfoTable.FLAGS));

        useLocalGlobalSolvedFlags(TaskinfoTable.hasFlag(flags, TaskinfoTable.FLAG_LOCAL), TaskinfoTable.hasFlag(flags, TaskinfoTable.FLAG_GLOBAL), TaskinfoTable.hasFlag(flags, TaskinfoTable.FLAG_SOLVED));
        mSelfPublishedRow.setVisibility(TaskinfoTable.hasFlag(flags, TaskinfoTable.FLAG_SELF_PUBLISHED) ? View.VISIBLE : View.GONE);
        useFavouriteFlag(TaskinfoTable.hasFlag(flags, TaskinfoTable.FLAG_FAVOURITE));

        String recordText;
        if(cursor.getColumnIndex(TaskRecordsTable.SPEED_REC)>=0)
            recordText = String.format("%s\n" +
                            "%s - '%s': %.2f, %s: %.2f\n" +
                            "%s - '%s': %d, %s: %d\n" +
                            "%s - '%s': %.2f, %s: %.2f", getResources().getString(R.string.label_task_screen_records__),
                    getResources().getString(R.string.label_task_screen_speed),
                    cursor.getString(cursor.getColumnIndex(TaskRecordsTable.SPEED_REC_NAME)), cursor.getFloat(cursor.getColumnIndex(TaskRecordsTable.SPEED_REC)),
                    getResources().getString(R.string.label_task_screen_records_you), cursor.getFloat(cursor.getColumnIndex(TaskRecordsTable.YOUR_SPEED_REC)),

                    getResources().getString(R.string.label_task_screen_size),
                    cursor.getString(cursor.getColumnIndex(TaskRecordsTable.SIZE_REC_NAME)), cursor.getInt(cursor.getColumnIndex(TaskRecordsTable.SIZE_REC)),
                    getResources().getString(R.string.label_task_screen_records_you), cursor.getInt(cursor.getColumnIndex(TaskRecordsTable.YOUR_SIZE_REC)),

                    getResources().getString(R.string.label_task_screen_memuse),
                    cursor.getString(cursor.getColumnIndex(TaskRecordsTable.MEMUSE_REC_NAME)), cursor.getFloat(cursor.getColumnIndex(TaskRecordsTable.MEMUSE_REC)),
                    getResources().getString(R.string.label_task_screen_records_you), cursor.getFloat(cursor.getColumnIndex(TaskRecordsTable.YOUR_MEMUSE_REC)));
        else
            recordText = getResources().getString(R.string.label_task_screen_no_records);

        mTaskRecordsText.setText(recordText);
    }

    protected void useLocalGlobalSolvedFlags(boolean local, boolean global, boolean solved)
    {
        mLocalFlag = local;
        mGlobalFlag = global;
        mSolvedFlag = solved;
        mLocalButton.setText(global ?
                (local ? R.string.label_task_screen_remove_locally : R.string.label_task_screen_store_locally)
                : R.string.label_task_screen_only_locally);
        mLocalButton.setEnabled(global);
        mLocalIcon.setVisibility(local ? View.VISIBLE : View.INVISIBLE);
        mOnlineRow.setVisibility(global ? View.VISIBLE : View.GONE);
        mAddSolutionButton.setText(local ? R.string.label_task_screen_add_solution : R.string.label_task_screen_only_local_tasks_can_be_solved);
        mAddSolutionButton.setEnabled(local);
        mSolveIcon.setVisibility(solved ? View.VISIBLE : View.INVISIBLE);
    }

    protected void useFavouriteFlag(boolean favourite)
    {
        mFavouriteFlag = favourite;
        mFavouriteButton.setText(favourite ? R.string.label_task_screen_un_favourite : R.string.label_task_screen_favourite);
        mFavouriteIcon.setVisibility(favourite ? View.VISIBLE : View.INVISIBLE);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.help_task_screen_title);
            builder.setMessage(R.string.help_task_screen_body);
            builder.create();
            builder.setPositiveButton(R.string.dialog_button_OK, null);
            builder.show();
            return true;
        }
        else if(id== R.id.action_delete_all_task_data)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.dialog_task_screen_are_you_sure);
            builder.setMessage(R.string.dialog_task_screen_delete_task_data_body);
            builder.create();
            builder.setPositiveButton(R.string.dialog_button_OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AFDatabaseInteractionHelper.deleteAllTaskData(((AssemblyFunApplication) getApplication()).getReadableDatabase(), mLocalID); //nTODO: Maybe use a loader?
                }
            });
            builder.setNegativeButton(R.string.dialog_button_Cancel, null);
            builder.setCancelable(true);
            builder.show();
            return true;
        }
        else if(id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle state)
    {
        super.onSaveInstanceState(state);
        state.putLong(EXTRAS_TASK_ID, mLocalID);
    }

    private static final String SOLUTION_LIST_CURSOR_QUERY = String.format("SELECT %s, %s, %s, %s, %s, %s AS _id FROM %s WHERE %s=?",
            SolutionsTable.NAME, SolutionsTable.SOLUTION_QUALITY, SolutionsTable.SPEED, SolutionsTable.SIZE, SolutionsTable.MEMUSE, SolutionsTable._ID,
            SolutionsTable.TABLE_NAME, SolutionsTable._ID_TaskIDs);
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == LOADER_ID_TASK_CURSOR)
            return new TaskInfoAndRecordsCursorLoader(this, ((AssemblyFunApplication)getApplication()).getDatabase(), mLocalID);
        else //if(id == LOADER_ID_SOLUTIONS_CURSOR)
            return new SQLiteCursorLoader(this, ((AssemblyFunApplication)getApplication()).getDatabase(), SOLUTION_LIST_CURSOR_QUERY, new String[]{Long.toString(mLocalID)});
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader instanceof TaskInfoAndRecordsCursorLoader) //it is the loader for the
            useTaskInfoAndRecordsCursor(data);
        else
            mSolutionListAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(!(loader instanceof TaskInfoAndRecordsCursorLoader))
            mSolutionListAdapter.changeCursor(null);
    }

    @Override
    public void onClick(View v)
    {
        if(v==mLocalButton)
        {
            if(mLocalFlag) {
                mLocalButton.setEnabled(false);
                new RemoveTaskLocally(mLocalID).execute();
            }
        }
        else if(v==mFavouriteButton)
        {
            mFavouriteButton.setEnabled(false);
            new ChangeFavouriteStatusTask(mLocalID, !mFavouriteFlag).execute();
        }
    }

    private class RemoveTaskLocally extends AsyncTask<Long, Integer, Void> {

        private long mTaskID;

        public RemoveTaskLocally(long taskID) {
            mTaskID = taskID;
        }

        @Override
        protected Void doInBackground(Long... params) {
            AFDatabaseInteractionHelper.removeLocalTaskFromDB(((AssemblyFunApplication)getApplication()).getWritableDatabase(), AFDatabaseInteractionHelper.getClearedContentValuesInstance(), mTaskID);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mLocalButton.setEnabled(true);
            useLocalGlobalSolvedFlags(false, mGlobalFlag, mSolvedFlag);
        }
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
}
