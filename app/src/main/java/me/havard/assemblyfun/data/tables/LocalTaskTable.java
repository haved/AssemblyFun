package me.havard.assemblyfun.data.tables;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/** A class for the name and column names of the localTasks table.
 * Created by Havard on 13/09/2015.
 */
public class LocalTaskTable extends Table {
    public static final String TABLE_NAME = "localTasks";
    public static final String _ID_TaskIDs = TaskIDTable._ID_TaskIDs;
    public static final String TASK_TEXT = "TaskText";
    public static final String TASK_TESTS = "TaskTests";
    @Override
    public String getCreateString() {
        return getSQLCreate(TABLE_NAME,
                _ID_TaskIDs, PRIMARY_KEY,
                TASK_TEXT, TEXT,
                TASK_TESTS, TEXT,
                TaskIDTable.FOREIGN_KEY_ID_TaskIDs);
    }

    @Override
    public String getTableName()
    {
        return TABLE_NAME;
    }

    public static void addRow(SQLiteDatabase db, ContentValues values, long ref_id, String taskText, String taskTests)
    {
        values.put(_ID_TaskIDs, ref_id);
        values.put(TASK_TEXT, taskText);
        values.put(TASK_TESTS, taskTests);
        db.insert(TABLE_NAME, null, values);
    }

    public static void deleteRow(SQLiteDatabase db, long ref_id)
    {
        db.delete(TABLE_NAME, _ID_TaskIDs+"=?", new String[]{Long.toString(ref_id)});
    }

    //Format: r0,r1=r0,r1,r2;r0=r0,r1;r0,r1=r0;
    //A test: 5,4,6=2,5,6
    //Example 4,5=3,5,4;3=5,10;1,2=3;
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
