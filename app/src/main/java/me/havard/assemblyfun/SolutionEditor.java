package me.havard.assemblyfun;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import me.havard.assemblyfun.util.DialogHelper;
import me.havard.assemblyfun.util.view.LineNumberText;

public class SolutionEditor extends FragmentActivity implements TabLayout.OnTabSelectedListener, LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener{

    public static final String EXTRAS_SOLUTION_ID = "solutionId";
    public static final String EXTRAS_TASK_ID = "taskId";

    public static final int NUM_PAGES = 2;
    public static final int TASK_PAGE = 0;
    public static final int SOLUTION_PAGE=1;

    private static final int LOADER_ID_TASK_PAGE_TASK_INFO_CURSOR = 0;
    private static final int LOADER_ID_TASK_PAGE_TASK_TEXT_CURSOR = 1;
    private static final int LOADER_ID_TASK_PAGE_RECORDS_CURSOR = 2;
    private static final int LOADER_ID_SOLUTION_TEXT_CURSOR = 3;

    private ViewPager mViewPager;
    private TaskSolutionPagerAdapter mPagerAdapter;

    private long mSolutionId;
    private long mTaskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution_editor);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.solution_editor_tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.solution_editor_view_pager);
        mPagerAdapter = new TaskSolutionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(SOLUTION_PAGE);
        tabLayout.setTabsFromPagerAdapter(mPagerAdapter);
        tabLayout.setOnTabSelectedListener(this);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        Bundle extras = getIntent().getExtras();
        if(extras==null) {
            Log.e("Assembly Fun", "No extras (solution and task id) were passed to the SolutionEditor");
            return;
        }
        mSolutionId = extras.getLong(EXTRAS_SOLUTION_ID, -1);
        mTaskId = extras.getLong(EXTRAS_TASK_ID, -1);
    }

    @Override
    public void onStart() {
        SolutionEditor.this.getLoaderManager().restartLoader(LOADER_ID_SOLUTION_TEXT_CURSOR, null, SolutionEditor.this);
        SolutionEditor.this.getLoaderManager().restartLoader(LOADER_ID_TASK_PAGE_TASK_INFO_CURSOR, null, SolutionEditor.this);
        SolutionEditor.this.getLoaderManager().restartLoader(LOADER_ID_TASK_PAGE_TASK_TEXT_CURSOR, null, SolutionEditor.this);
        SolutionEditor.this.getLoaderManager().restartLoader(LOADER_ID_TASK_PAGE_RECORDS_CURSOR, null, SolutionEditor.this);
        super.onStart();
    }

    public void useTaskPageTaskInfoCursor(Cursor cursor) {
        View v = mPagerAdapter.getTaskFragment().getView();
        if(v==null) {
            Log.e("Assembly Fun", "The EditorTaskFragment had no view when trying to use the database cursor");
            return;
        }
        cursor.moveToFirst();
        if(cursor.isAfterLast()) {
            Log.e("Assembly Fun", "The Task Info Cursor passed to the SolutionEditor.useTaskPageTaskInfoCursor(Cursor) didn't have any items. Nothing to fill fields with!");
            ((TextView)v.findViewById(R.id.solution_editor_task_title)).setText(R.string.label_solution_editor_n_a);
            return;
        }
        ((TextView)v.findViewById(R.id.solution_editor_task_title)).setText(cursor.getString(cursor.getColumnIndex(TaskinfoTable.NAME)));
    }

    public void useTaskPageTaskTextCursor(Cursor cursor) {
        View v = mPagerAdapter.getTaskFragment().getView();
        if(v==null) {
            Log.e("Assembly Fun", "The EditorTaskFragment had no view when trying to use the database cursor");
            return;
        }
        cursor.moveToFirst();
        if(cursor.isAfterLast()) {
            Log.e("Assembly Fun", "The Task Text Cursor passed to the SolutionEditor.useTaskPageTaskTextCursor(Cursor) didn't have any items. Nothing to fill fields with!");
            ((TextView)v.findViewById(R.id.solution_editor_task_desc)).setText(R.string.label_solution_editor_task_not_local);
            v.findViewById(R.id.solution_editor_test_solution_button).setEnabled(false);
            return;
        }
        ((TextView)v.findViewById(R.id.solution_editor_task_desc)).setText(cursor.getString(cursor.getColumnIndex(LocalTaskTable.TASK_TEXT)));
        v.findViewById(R.id.solution_editor_test_solution_button).setEnabled(true);
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
        cursor.moveToFirst();
        if(cursor.isAfterLast()) {
            Log.i("Assembly Fun", "The solution text cursor didn't have any items.");
            return;
        }
        mPagerAdapter.getSolutionFragment().setSolutionText(cursor.getString(cursor.getColumnIndex(SolutionsTable.SOLUTION_TEXT)));
    }

    @Override
    public void onPause()
    {
        super.onPause();
        saveSolutionAndUpdateRecords(SolutionsTable.QUALITY_NEVER_RUN, -1, -1, -1);
    }

    private void saveSolutionAndUpdateRecords(int quality, float speed, int size, float memUse) {
        String solution = mPagerAdapter.getSolutionFragment().getSolutionText();
        if(solution != null)
            new SaveSolutionTextTask(((AssemblyFunApplication)getApplication()).getDatabase(), mSolutionId,
                    solution, quality, speed, size, memUse).execute();
        else
            Log.e("Assembly Fun", "Tried saving the solution text to the database, but failed to find the text field");
    }

    static class SaveSolutionTextTask extends AsyncTask<Void, Void, Void> {

        private SQLiteOpenHelper mDb;
        private long mSolutionId;
        private String mText;
        private int mQuality;
        private float mSpeed, mMemUse;
        private int mSize;

        public SaveSolutionTextTask(SQLiteOpenHelper db, long solution_id, String text, int quality, float speed, int size, float memUse) {
            mDb = db;
            mSolutionId = solution_id;
            mText = text;
            mQuality = quality;
            mSpeed = speed;
            mSize = size;
            mMemUse = memUse;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Cursor c = mDb.getReadableDatabase().rawQuery(QUERY_SOLUTION_TEXT_CURSOR, new String[]{Long.toString(mSolutionId)});
            c.moveToFirst();
            if(c.isAfterLast()) {
                Log.e("Assembly Fun", "No solution row for the id " + mSolutionId + " found. Cursor empty! Not saving anything!");
                return null;
            }
            String prevTask = c.getString(c.getColumnIndex(SolutionsTable.SOLUTION_TEXT));
            c.close();
            if(!mText.equals(prevTask)) {
                //It was modified. Save the text and update variables!
                SQLiteDatabase db = mDb.getWritableDatabase();
                AFDatabaseInteractionHelper.changeSolutionText(db, AFDatabaseInteractionHelper.getClearedContentValuesInstance(), mSolutionId, mText);
                AFDatabaseInteractionHelper.updateSolutionValues(db, AFDatabaseInteractionHelper.getClearedContentValuesInstance(), mSolutionId, -1, mQuality, mSpeed, mSize, mMemUse);
            }
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        mPagerAdapter.getTaskFragment().getTestButton().setEnabled(false);
        new TaskRunnerAsyncTask(AFDatabaseInteractionHelper.getTaskTests(((AssemblyFunApplication) getApplication()).getReadableDatabase(), mTaskId),
                mPagerAdapter.getSolutionFragment().getSolutionText()).execute();
    }

    private class TaskRunnerAsyncTask extends AsyncTask<Object, Object, Object> {
        private SolutionTester mTester;
        private String mTaskTests, mSolutionText;

        public TaskRunnerAsyncTask(String taskTests, String solutionText) {
            this.mTaskTests = taskTests;
            this.mSolutionText = solutionText;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                mTester = new SolutionTester();
                return mTester.runAllTests(mTaskTests, mSolutionText);
            } catch(Exception e) {
                return e;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            if(result instanceof SolutionTester.TestResult) {
                //The tests were successful.
                SolutionTester.TestResult testResult = (SolutionTester.TestResult) result;
                new AlertDialog.Builder(SolutionEditor.this).setTitle(R.string.asm_dialog_title_test_worked).setMessage(
                        String.format(getResources().getString(R.string.asm_dialog_test_worked_details), testResult.speed, testResult.size, testResult.memUsage))
                        .setPositiveButton(R.string.dialog_button_OK, null).setCancelable(true).create().show();
                saveSolutionAndUpdateRecords(SolutionsTable.QUALITY_SOLVED, testResult.speed, testResult.size, testResult.memUsage);
            } else if(result instanceof AssemblyException) {
                AssemblyException e = (AssemblyException) result;
                e.printStackTrace();
                AlertDialog.Builder builder = new AlertDialog.Builder(SolutionEditor.this);
                if(e.getExceptionID()==AssemblyException.TEST_FAILED_PUBLIC) {
                    builder.setTitle(R.string.asm_dialog_title_assembly_test_failed);
                    builder.setMessage(getResources().getString(R.string.asm_wrong_output_public, e.getParams()[0], e.getParams()[1]));
                } else  if(e.getExceptionID()==AssemblyException.TEST_FAILED) {
                    builder.setTitle(R.string.asm_dialog_title_assembly_test_failed);
                    builder.setMessage(R.string.asm_wrong_output);
                } else {
                    builder.setTitle(R.string.asm_dialog_title_assembly_error);
                    builder.setMessage(e.getMessage());
                }
                builder.create();
                builder.setPositiveButton(R.string.dialog_button_OK, null);
                builder.setCancelable(true);
                builder.show();
                saveSolutionAndUpdateRecords(SolutionsTable.QUALITY_FAILS, -1, -1, -1);
            } else if(result instanceof Exception) {
                Log.wtf("Assembly Fun", (Exception)result);
            } else {
                Log.e("Assembly Fun", "Nothing sensible (not even an Exception was given to SolutionEditor.TaskRunnerAsyncTask.onPostExecute()   Thing given: " + result);
            }
            mPagerAdapter.getTaskFragment().getTestButton().setEnabled(true);
        }
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

    private static final String QUERY_TASK_PAGE_INFO_CURSOR =       String.format("SELECT %s FROM %s WHERE %s=? LIMIT 1", TaskinfoTable.NAME, TaskinfoTable.TABLE_NAME, TaskinfoTable._ID_TaskIDs);
    private static final String QUERY_TASK_PAGE_TASK_TEXT_CURSOR =  String.format("SELECT %s FROM %s WHERE %s=? LIMIT 1", LocalTaskTable.TASK_TEXT, LocalTaskTable.TABLE_NAME, LocalTaskTable._ID_TaskIDs);
    private static final String QUERY_TASK_PAGE_RECORDS_CURSOR =    String.format("SELECT *  FROM %s WHERE %s=? LIMIT 1", TaskRecordsTable.TABLE_NAME, TaskRecordsTable._ID_TaskIDs);
    private static final String QUERY_SOLUTION_TEXT_CURSOR =        String.format("SELECT %s FROM %s WHERE %s=? LIMIT 1", SolutionsTable.SOLUTION_TEXT, SolutionsTable.TABLE_NAME, SolutionsTable._ID);

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id== LOADER_ID_TASK_PAGE_TASK_INFO_CURSOR)
            return new SQLiteCursorLoader(this, ((AssemblyFunApplication)getApplication()).getDatabase(), QUERY_TASK_PAGE_INFO_CURSOR,
                    new String[]{Long.toString(mTaskId)}).setId(LOADER_ID_TASK_PAGE_TASK_INFO_CURSOR);
        else if(id== LOADER_ID_TASK_PAGE_TASK_TEXT_CURSOR)
            return new SQLiteCursorLoader(this, ((AssemblyFunApplication)getApplication()).getDatabase(), QUERY_TASK_PAGE_TASK_TEXT_CURSOR,
                    new String[]{Long.toString(mTaskId)}).setId(LOADER_ID_TASK_PAGE_TASK_TEXT_CURSOR);
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
        if(id == LOADER_ID_TASK_PAGE_TASK_INFO_CURSOR)
            useTaskPageTaskInfoCursor(data);
        else if(id == LOADER_ID_TASK_PAGE_TASK_TEXT_CURSOR)
            useTaskPageTaskTextCursor(data);
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
            return mSolutionFragment;
        }

        public EditorTaskFragment getTaskFragment()
        {
            if(mTaskFragment != null)
                return mTaskFragment;

            mTaskFragment = new EditorTaskFragment();
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

        private EditText mEditText;
        private LineNumberText mLineNumberText;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(
                    R.layout.fragment_editor_solution_page, container, false);

            mEditText = (EditText)view.findViewById(R.id.solution_editor_solution_text_field);
            mLineNumberText = (LineNumberText)view.findViewById(R.id.solution_editor_solution_text_line_numbers);
            mLineNumberText.setEditText(mEditText);

            return view;
        }

        public void setSolutionText(String text) {

            if(mEditText==null) {
                Log.e("Assembly Fun", "The EditorTaskFragment had no EditText instance when trying to set the text");
                return;
            }
            mEditText.setText(text);

            if(mLineNumberText==null) {
                Log.e("Assembly Fun", "The EditorTaskFragment had no LineNumberText instance when trying to set the text");
                return;
            }
            mLineNumberText.updateLineNumbers();
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

    public static class EditorTaskFragment extends Fragment implements View.OnClickListener {

        private Button mTestButton;

        public EditorTaskFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(
                    R.layout.fragment_editor_task_page, container, false);

            ((TextView)view.findViewById(R.id.solution_editor_current_solution_fill_label)).setText("");
            mTestButton = (Button)view.findViewById(R.id.solution_editor_test_solution_button);
            mTestButton.setOnClickListener(this);

            return view;
        }

        @Override
        public void onClick(View v) {
            FragmentActivity activity = getActivity();
            if(activity != null && activity instanceof SolutionEditor)
                ((SolutionEditor)activity).onClick(v);
        }

        public Button getTestButton() {
            return mTestButton;
        }
    }
}
