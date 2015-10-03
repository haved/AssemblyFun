package me.havard.assemblyfun;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import me.havard.assemblyfun.data.Difficulty;
import me.havard.assemblyfun.data.SQLiteCursorLoader;
import me.havard.assemblyfun.data.tables.TaskinfoTable;

public class TaskScreen extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRAS_TASK_ID = "extrasTaskID";
    private static final int LOADER_ID_TASK_CURSOR = 0;

    private static final String TASK_QUERY = "SELECT * FROM " + TaskinfoTable.TABLE_NAME + " WHERE " + TaskinfoTable._ID_TaskIDs + " = ?";

    private long mLocalID;
    private String[] mSelectArgs;

    private TextView mTaskTitle, mTaskDesc, mTaskDiff, mTaskDate, mTaskAuthor;
    private Button mLocalButton, mSolveButton, mPublishButton, mFavouriteButton;
    private ImageView mLocalIcon, mSolveIcon, mPublishIcon, mFavouriteIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_screen);

        mTaskTitle = (TextView) findViewById(R.id.task_screen_task_title);
        mTaskDesc = (TextView) findViewById(R.id.task_screen_task_desc);
        mTaskDiff = (TextView) findViewById(R.id.task_screen_task_diff);
        mTaskDate = (TextView) findViewById(R.id.task_screen_task_date);
        mTaskAuthor = (TextView) findViewById(R.id.task_screen_task_author);

        mLocalIcon = (ImageView) findViewById(R.id.task_screen_local_icon);
        mSolveIcon = (ImageView) findViewById(R.id.task_screen_solve_icon);
        mPublishIcon = (ImageView) findViewById(R.id.task_screen_publish_icon);
        mFavouriteIcon = (ImageView) findViewById(R.id.task_screen_favourite_icon);

        mTaskTitle.setText(R.string.label_loading);

        mLocalID = getIntent().getExtras().getLong(EXTRAS_TASK_ID);
        mSelectArgs = new String[]{Long.toString(mLocalID)};
        getLoaderManager().initLoader(LOADER_ID_TASK_CURSOR, null, this);
    }

    protected void useCursor(Cursor cursor)
    {
        cursor.moveToFirst();
        if(cursor.isAfterLast()) {
            mTaskTitle.setText("No task with task id '" + mLocalID + "' was not found in the database!");
            return;
        }

        mTaskTitle.setText(cursor.getString(cursor.getColumnIndex(TaskinfoTable.NAME)));
        mTaskDesc.setText(cursor.getString(cursor.getColumnIndex(TaskinfoTable.DESC)));
        mTaskDate.setText(cursor.getString(cursor.getColumnIndex(TaskinfoTable.DATE)));
        mTaskAuthor.setText(cursor.getString(cursor.getColumnIndex(TaskinfoTable.AUTHOR)));

        Difficulty diff = Difficulty.values()[cursor.getInt(cursor.getColumnIndex(TaskinfoTable.DIFFICULTY))];
        mTaskDiff.setText(diff.getLabelId());
        mTaskDiff.setTextColor(ContextCompat.getColor(this, diff.getColorId()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new SQLiteCursorLoader(this, ((AssemblyFunApplication)getApplication()).getDatabase(), TASK_QUERY, mSelectArgs);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        useCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Well. No reason to do anything really
    }
}
