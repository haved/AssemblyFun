package me.havard.assemblyfun;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TaskList extends AppCompatActivity {

    public static final String HIDE_UNSOLVED_FIRST_OPTION_ID = "hide_action_unsolved_first";
    public static final String RES_ACTIVITY_TITLE = "activity_title_res";
    public static final String TASK_ID_TABLE_NAME = "task_id_table_name";

    private boolean mHideUnsolvedFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras != null)
            mHideUnsolvedFirst = extras.getBoolean(HIDE_UNSOLVED_FIRST_OPTION_ID);

        setContentView(R.layout.activity_task_list);

        ArrayAdapter<String> listItems = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{extras.getString(TASK_ID_TABLE_NAME), "Pelle", "Parafin", "Bolgeband"});
        ListView list = (ListView)findViewById(R.id.task_list_view);
        list.setAdapter(listItems);

        if(extras != null)
            setTitle(extras.getInt(RES_ACTIVITY_TITLE, R.string.title_unset));
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
