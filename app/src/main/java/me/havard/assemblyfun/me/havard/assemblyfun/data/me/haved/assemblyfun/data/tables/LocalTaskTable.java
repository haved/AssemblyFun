package me.havard.assemblyfun.me.havard.assemblyfun.data.me.haved.assemblyfun.data.tables;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/** A class for the name and column names of the localTasks table.
 * Created by Havard on 13/09/2015.
 */
public class LocalTaskTable extends Table {
    public static final String TABLE_NAME = "localTasks";
    public static final String TASK_TEXT = "TaskText";
    public static final String TASK_TESTS = "TaskTests";
    @Override
    public String getCreateString() {
        return getSQLCreate(TABLE_NAME,
                TaskinfoTable.REF_ID, INT,
                TASK_TEXT, TEXT,
                TASK_TESTS, TEXT,
                TaskinfoTable.FOREIGN_KEY_REF_ID);
    }

    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    public static void populateContentValues(ContentValues values, long ref_id, String taskText, String taskTests)
    {
        values.put(TaskinfoTable.REF_ID, ref_id);
        values.put(TASK_TEXT, taskText);
        values.put(TASK_TESTS, taskTests);
    }

    public static void addLocalTaskToDB(SQLiteDatabase db, ContentValues values, long ref_id, String taskText, String taskTests)
    {
        populateContentValues(values, ref_id, taskText, taskTests);
        db.insert(TABLE_NAME, null, values);
    }

    public static String makeTaskTestString(int[][] inputs, int[][]outputs)
    {
        StringBuilder out = new StringBuilder();
        if(inputs.length != outputs.length)
        {
            throw new RuntimeException("Trying to make a task test string from two sets of inputs and outputs of different sizes");
        }

        for(int i = 0; i < inputs.length & i < outputs.length; i++)
        {
            if(i!=0)
                out.append(";");
            for(int j = 0; j < inputs[i].length; j++) {
                if(j != 0)
                    out.append(",");
                out.append(inputs[i][j]);
            }
            out.append("=");
            for(int j = 0; j < outputs[i].length; j++) {
                if(j != 0)
                    out.append(",");
                out.append(outputs[i][j]);
            }
        }

        return out.toString();
    }
}
