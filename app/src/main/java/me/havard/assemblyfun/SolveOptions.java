package me.havard.assemblyfun;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import me.havard.assemblyfun.data.tables.TaskinfoTable;

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
        localTaskListActivity.putExtra(TaskList.CHECK_COLUMN_NAME, TaskinfoTable.LOCAL);
        localTaskListActivity.putExtra(TaskList.HIDE_LOCAL_FILTER_OPTION_ID, true);
        startActivity(localTaskListActivity);
    }

    public void onSolveSolvedPressed(View source)
    {
        Intent solvedTaskListActivity = new Intent(this, TaskList.class);
        solvedTaskListActivity.putExtra(TaskList.RES_ACTIVITY_TITLE, R.string.solve_option_solved);
        solvedTaskListActivity.putExtra(TaskList.CHECK_COLUMN_NAME, TaskinfoTable.SOLVED);
        solvedTaskListActivity.putExtra(TaskList.HIDE_UNSOLVED_FILTER_OPTION_ID, true);
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
