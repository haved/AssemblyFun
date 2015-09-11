package me.havard.assemblyfun;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class TaskList extends AppCompatActivity {

    public static final String HIDE_UNSOLEVED_FIRST_OPTION_ID = "hide_action_unsolved_first";
    public static final String RES_ACTIVITY_TITLE = "activity_title_res";

    private boolean mHideUnsolvedFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras != null)
            mHideUnsolvedFirst = extras.getBoolean(HIDE_UNSOLEVED_FIRST_OPTION_ID);

        setContentView(R.layout.activity_task_list);

        if(extras != null & getActionBar()!=null)
            getActionBar().setTitle(extras.getInt(RES_ACTIVITY_TITLE, R.string.title_unset));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        if(mHideUnsolvedFirst)
            menu.removeItem(R.id.action_unsolved_first);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
