package me.havard.assemblyfun;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import me.havard.assemblyfun.data.tables.LocalTaskTable;
import me.havard.assemblyfun.data.tables.SelfPublishedTable;
import me.havard.assemblyfun.data.tables.SolvedTasksTable;

public class SolveOptions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solve_options);
    }

    public void onSolveOnlinePressed(View source)
    {

    }

    public void onSolveLocalPressed(View source)
    {
        Intent localTaskListActivity = new Intent(this, TaskList.class);
        localTaskListActivity.putExtra(TaskList.RES_ACTIVITY_TITLE, R.string.solve_option_local);
        localTaskListActivity.putExtra(TaskList.REF_TASKINFO_TABLE_ID_TABLE_NAME, LocalTaskTable.TABLE_NAME);
        startActivity(localTaskListActivity);
    }

    public void onSolveSolvedPressed(View source)
    {
        Intent solvedTaskListActivity = new Intent(this, TaskList.class);
        solvedTaskListActivity.putExtra(TaskList.RES_ACTIVITY_TITLE, R.string.solve_option_solved);
        solvedTaskListActivity.putExtra(TaskList.REF_TASKINFO_TABLE_ID_TABLE_NAME, SolvedTasksTable.TABLE_NAME);
        solvedTaskListActivity.putExtra(TaskList.HIDE_UNSOLVED_FIRST_OPTION_ID, true);
        startActivity(solvedTaskListActivity);
    }

    public void onSolvePublishedPressed(View source)
    {
        Intent solvedTaskListActivity = new Intent(this, TaskList.class);
        solvedTaskListActivity.putExtra(TaskList.RES_ACTIVITY_TITLE, R.string.solve_option_published);
        solvedTaskListActivity.putExtra(TaskList.REF_TASKINFO_TABLE_ID_TABLE_NAME, SelfPublishedTable.TABLE_NAME);
        startActivity(solvedTaskListActivity);
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
            TextView body = new TextView(this);
            body.setText(R.string.help_solve_options_body);
            dialog.setView(body);
            dialog.create();
            dialog.setPositiveButton(R.string.dialog_button_OK, null);
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
