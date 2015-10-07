package me.havard.assemblyfun;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import me.havard.assemblyfun.data.SharedPreferencesHelper;
import me.havard.assemblyfun.data.tables.TaskinfoTable;

public class SolveOptions extends AppCompatActivity implements View.OnClickListener{

    private Button mOnlineTasks, mLocalTasks, mSolvedTasks, mAllTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solve_options);

        mOnlineTasks = (Button) findViewById(R.id.solve_options_online_tasks);
        mOnlineTasks.setOnClickListener(this);
        mLocalTasks = (Button) findViewById(R.id.solve_options_local_tasks);
        mLocalTasks.setOnClickListener(this);
        mSolvedTasks = (Button) findViewById(R.id.solve_options_solved_tasks);
        mSolvedTasks.setOnClickListener(this);
        mAllTasks = (Button) findViewById(R.id.solve_options_all_tasks);
        mAllTasks.setOnClickListener(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mAllTasks.setVisibility(SharedPreferencesHelper.shouldKeepUnlistedTasks(SharedPreferencesHelper.getPreferences(this)) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        if(v == mOnlineTasks)
            onSolveOnlinePressed();
        else if(v == mLocalTasks)
            onSolveLocalPressed();
        else if(v == mSolvedTasks)
            onSolveSolvedPressed();
        else if(v == mAllTasks)
            onSolveAllTasksPressed();
    }

    private void onSolveOnlinePressed()
    {

    }

    private void onSolveLocalPressed()
    {
        Intent intent = new Intent(this, TaskList.class);
        intent.putExtra(TaskList.KEY_RES_ACTIVITY_TITLE, R.string.button_solve_option_local);
        intent.putExtra(TaskList.KEY_CHECK_FLAG_BIT, TaskinfoTable.FLAG_LOCAL);
        intent.putExtra(TaskList.KEY_HIDE_LOCAL_FILTER_OPTION_ID, true);
        startActivity(intent);
    }

    private void onSolveSolvedPressed()
    {
        Intent intent = new Intent(this, TaskList.class);
        intent.putExtra(TaskList.KEY_RES_ACTIVITY_TITLE, R.string.button_solve_option_solved);
        intent.putExtra(TaskList.KEY_CHECK_FLAG_BIT, TaskinfoTable.FLAG_SOLVED);
        intent.putExtra(TaskList.KEY_HIDE_UNSOLVED_FILTER_OPTION_ID, true);
        startActivity(intent);
    }

    private void onSolveAllTasksPressed()
    {
        Intent intent = new Intent(this, TaskList.class);
        intent.putExtra(TaskList.KEY_RES_ACTIVITY_TITLE, R.string.button_solve_option_all_tasks);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_solve_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.help_solve_options_title);
            dialog.setMessage(R.string.help_solve_options_body);
            dialog.create();
            dialog.setPositiveButton(R.string.dialog_button_OK, null);
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
