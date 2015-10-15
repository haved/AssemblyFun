package me.havard.assemblyfun;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import me.havard.assemblyfun.assembly.SolutionTester;
import me.havard.assemblyfun.data.AFDatabaseInteractionHelper;
import me.havard.assemblyfun.data.SQLiteCursorLoader;
import me.havard.assemblyfun.data.tables.LocalTaskTable;
import me.havard.assemblyfun.data.tables.SolutionsTable;
import me.havard.assemblyfun.data.tables.TaskRecordsTable;
import me.havard.assemblyfun.data.tables.TaskinfoTable;

public class SolutionEditor extends FragmentActivity implements TabLayout.OnTabSelectedListener, LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener{

    public static final String EXTRAS_SOLUTION_ID = "solutionId";
    public static final String EXTRAS_TASK_ID = "taskId";

    public static final int NUM_PAGES = 2;
    public static final int TASK_PAGE = 0;
    public static final int SOLUTION_PAGE=1;

    private static final int LOADER_ID_TASK_PAGE_CURSOR = 0;
    private static final int LOADER_ID_TASK_PAGE_RECORDS_CURSOR = 1;
    private static final int LOADER_ID_SOLUTION_TEXT_CURSOR = 2;


    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TaskSolutionPagerAdapter mPagerAdapter;

    private long mSolutionId;
    private long mTaskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution_editor);

        mTabLayout = (TabLayout) findViewById(R.id.solution_editor_tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.solution_editor_view_pager);
        mPagerAdapter = new TaskSolutionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(SOLUTION_PAGE);
        mTabLayout.setTabsFromPagerAdapter(mPagerAdapter);
        mTabLayout.setOnTabSelectedListener(this);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        Bundle extras = getIntent().getExtras();
        if(extras==null) {
            Log.e("Assembly Fun", "No extras (solution and task id) were passed to the SolutionEditor");
            return;
        }
        mSolutionId = extras.getLong(EXTRAS_SOLUTION_ID, -1);
        mTaskId = extras.getLong(EXTRAS_TASK_ID, -1);
    }

    public void useTaskPageCursor(Cursor cursor) {
        View v = mPagerAdapter.getTaskFragment().getView();
        if(v==null) {
            Log.e("Assembly Fun", "The EditorTaskFragment had no view when trying to use the database cursor");
            return;
        }
        cursor.moveToFirst();
        if(cursor.isAfterLast()) {
            Log.e("Assembly Fun", "The Task Cursor passed to the EditorTaskFragment didn't have any items. Nothing to fill fields with!");
            ((TextView)v.findViewById(R.id.solution_editor_task_title)).setText(R.string.label_solution_editor_n_a);
            ((TextView)v.findViewById(R.id.solution_editor_task_desc)).setText(R.string.label_solution_editor_n_a);
            return;
        }
        ((TextView)v.findViewById(R.id.solution_editor_task_title)).setText(cursor.getString(cursor.getColumnIndex(TaskinfoTable.NAME)));
        ((TextView)v.findViewById(R.id.solution_editor_task_desc)).setText(cursor.getString(cursor.getColumnIndex(LocalTaskTable.TASK_TEXT)));
    }

    public void useTaskRecordsCursor(Cursor cursor) {
        View v = mPagerAdapter.getTaskFragment().getView();
        if(v==null) {
            Log.e("Assembly Fun", "The EditorTaskFragment had no view when trying to use the database cursor");
            return;
        }
        cursor.moveToFirst();
        if(cursor.isAfterLast()) {
            Log.i("Assembly Fun", "The Records Cursor passed to the EditorTaskFragment didn't have any items. Saying 'none' for records");
            ((TextView)v.findViewById(R.id.solution_editor_world_records_fill_label)).setText(R.string.label_solution_editor_None);
            ((TextView)v.findViewById(R.id.solution_editor_personal_records_fill_label)).setText(R.string.label_solution_editor_None);
            return;
        }
        String worldRecords = getResources().getString(R.string.label_solution_editor_world_records_fill,
                cursor.getFloat(cursor.getColumnIndex(TaskRecordsTable.SPEED_REC)), cursor.getString(cursor.getColumnIndex(TaskRecordsTable.SPEED_REC_NAME)),
                cursor.getInt(cursor.getColumnIndex(TaskRecordsTable.SIZE_REC)), cursor.getString(cursor.getColumnIndex(TaskRecordsTable.SIZE_REC_NAME)),
                cursor.getFloat(cursor.getColumnIndex(TaskRecordsTable.MEMUSE_REC)), cursor.getString(cursor.getColumnIndex(TaskRecordsTable.MEMUSE_REC_NAME)));

        String personalRecords = getResources().getString(R.string.label_solution_editor_personal_records_fill,
                cursor.getFloat(cursor.getColumnIndex(TaskRecordsTable.PERSONAL_SPEED_REC)),
                cursor.getInt(cursor.getColumnIndex(TaskRecordsTable.PERSONAL_SIZE_REC)),
                cursor.getFloat(cursor.getColumnIndex(TaskRecordsTable.PERSONAL_MEMUSE_REC)));

        ((TextView)v.findViewById(R.id.solution_editor_world_records_fill_label)).setText(worldRecords);
        ((TextView)v.findViewById(R.id.solution_editor_personal_records_fill_label)).setText(personalRecords);
    }

    public void useSolutionTextCursor(Cursor cursor) {
        View v = mPagerAdapter.getSolutionFragment().getView();
        if(v==null) {
            Log.e("Assembly Fun", "The EditorTaskFragment had no view when trying to use the database cursor");
            return;
        }
        cursor.moveToFirst();
        if(cursor.isAfterLast()) {
            Log.i("Assembly Fun", "The solution text cursor didn't have any items.");
            return;
        }
        ((EditText)v.findViewById(R.id.solution_editor_solution_text_field)).setText(cursor.getString(cursor.getColumnIndex(SolutionsTable.SOLUTION_TEXT)));
    }

    @Override
    public void onPause()
    {
        super.onPause();
        String solution = mPagerAdapter.getSolutionFragment().getSolutionText();
        if(solution != null)
            new SaveSolutionTextTask(((AssemblyFunApplication)getApplication()).getDatabase(), mSolutionId,
                solution).execute();
        else
            Log.e("Assembly Fun", "Tried saving the solution text to the database, but failed to find the text field");
    }

    static class SaveSolutionTextTask extends AsyncTask<Void, Void, Void> {

        private SQLiteOpenHelper mDb;
        private long mSolutionId;
        private String mText;

        public SaveSolutionTextTask(SQLiteOpenHelper db, long solution_id, String text) {
            mDb = db;
            mSolutionId = solution_id;
            mText = text;
        }

        @Override
        protected Void doInBackground(Void... params) {
            AFDatabaseInteractionHelper.changeSolutionText(mDb.getWritableDatabase(), AFDatabaseInteractionHelper.getClearedContentValuesInstance(), mSolutionId, mText);
            return null;
        }
    }

    private SolutionTester mTester;
    @Override
    public void onClick(View v) {
        if(mTester == null)
            mTester = new SolutionTester();

        mTester.runAllTests(AFDatabaseInteractionHelper.getTaskTests(((AssemblyFunApplication)getApplication()).getReadableDatabase(), mTaskId), mPagerAdapter.getSolutionFragment().getSolutionText());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem m) {

        int id = m.getItemId();

        if(id==android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(m);
    }

    @Override
    public void onBackPressed()
    {
        if(mViewPager.getCurrentItem() != SOLUTION_PAGE)
            mViewPager.setCurrentItem(SOLUTION_PAGE, true);
        else
            super.onBackPressed();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition(), true);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private static final String QUERY_TASK_PAGE_CURSOR = String.format("SELECT %s, %s FROM %s, %s WHERE %s.%s=? AND %s.%s=? LIMIT 1", TaskinfoTable.NAME, LocalTaskTable.TASK_TEXT,
            TaskinfoTable.TABLE_NAME, LocalTaskTable.TABLE_NAME,      TaskinfoTable.TABLE_NAME,TaskinfoTable._ID_TaskIDs,        LocalTaskTable.TABLE_NAME,LocalTaskTable._ID_TaskIDs);

    private static final String QUERY_TASK_PAGE_RECORDS_CURSOR = String.format("SELECT * FROM %s WHERE %s=? LIMIT 1", TaskRecordsTable.TABLE_NAME, TaskRecordsTable._ID_TaskIDs);
    private static final String QUERY_SOLUTION_TEXT_CURSOR = String.format("SELECT %s FROM %s WHERE %s=? LIMIT 1", SolutionsTable.SOLUTION_TEXT, SolutionsTable.TABLE_NAME, SolutionsTable._ID);

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id==LOADER_ID_TASK_PAGE_CURSOR)
            return new SQLiteCursorLoader(this, ((AssemblyFunApplication)getApplication()).getDatabase(), QUERY_TASK_PAGE_CURSOR,
                    new String[]{Long.toString(mTaskId), Long.toString(mTaskId)}).setId(LOADER_ID_TASK_PAGE_CURSOR);
        else if(id==LOADER_ID_TASK_PAGE_RECORDS_CURSOR)
            return new SQLiteCursorLoader(this, ((AssemblyFunApplication)getApplication()).getDatabase(),
                    QUERY_TASK_PAGE_RECORDS_CURSOR, new String[]{Long.toString(mTaskId)}).setId(LOADER_ID_TASK_PAGE_RECORDS_CURSOR);
        else if(id==LOADER_ID_SOLUTION_TEXT_CURSOR)
            return new SQLiteCursorLoader(this, ((AssemblyFunApplication)getApplication()).getDatabase(),
                    QUERY_SOLUTION_TEXT_CURSOR, new String[]{Long.toString(mSolutionId)}).setId(LOADER_ID_SOLUTION_TEXT_CURSOR);
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        if(id == LOADER_ID_TASK_PAGE_CURSOR)
            useTaskPageCursor(data);
        else if(id == LOADER_ID_TASK_PAGE_RECORDS_CURSOR)
            useTaskRecordsCursor(data);
        else if(id == LOADER_ID_SOLUTION_TEXT_CURSOR)
            useSolutionTextCursor(data);
        else
            Log.w("Assembly Fun", "onLoadFinished in the SolutionEditor returned a loader with unknown id: " + id);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    class TaskSolutionPagerAdapter extends FragmentPagerAdapter {

        EditorSolutionFragment mSolutionFragment;
        EditorTaskFragment mTaskFragment;

        public TaskSolutionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public EditorSolutionFragment getSolutionFragment()
        {
            if(mSolutionFragment != null)
                return mSolutionFragment;

            mSolutionFragment = new EditorSolutionFragment();
            SolutionEditor.this.getLoaderManager().initLoader(LOADER_ID_SOLUTION_TEXT_CURSOR, null, SolutionEditor.this);
            return mSolutionFragment;
        }

        public EditorTaskFragment getTaskFragment()
        {
            if(mTaskFragment != null)
                return mTaskFragment;

            mTaskFragment = new EditorTaskFragment();
            mTaskFragment.getTestSolutionButton().setOnClickListener(SolutionEditor.this);
            SolutionEditor.this.getLoaderManager().initLoader(LOADER_ID_TASK_PAGE_CURSOR, null, SolutionEditor.this);
            SolutionEditor.this.getLoaderManager().initLoader(LOADER_ID_TASK_PAGE_RECORDS_CURSOR, null, SolutionEditor.this);
            return mTaskFragment;
        }

        @Override
        public Fragment getItem(int position) {
            if(position == SOLUTION_PAGE)
                return getSolutionFragment();
            else if(position == TASK_PAGE)
                return getTaskFragment();

            Log.e("Assembly Fun", "The TaskSolutionAdapter doesn't have a page with position " + position);
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public String getPageTitle(int position)
        {
            if(position>=0&position<2)
                return getResources().getString(position==0?R.string.title_solution_editor_task_tab:R.string.title_solution_editor_solve_tab);
            return "unknown title";
        }
    }

    public static class EditorSolutionFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(
                    R.layout.fragment_editor_solution_page, container, false);
        }

        public String getSolutionText() {
            View v = getView();
            if(v==null)
                return null;
            EditText text = (EditText)v.findViewById(R.id.solution_editor_solution_text_field);
            if(text == null)
                return null;
            return text.getText().toString();
        }
    }

    public static class EditorTaskFragment extends Fragment {

        private Button mTaskSolutionButton;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(
                    R.layout.fragment_editor_task_page, container, false);

            ((TextView)view.findViewById(R.id.solution_editor_current_solution_fill_label)).setText("");
            mTaskSolutionButton = (Button) view.findViewById(R.id.solution_editor_test_solution_button);

            return view;
        }

        public Button getTestSolutionButton() {
            return mTaskSolutionButton;
        }
    }
}
