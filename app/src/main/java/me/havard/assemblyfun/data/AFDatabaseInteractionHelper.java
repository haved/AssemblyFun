package me.havard.assemblyfun.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import me.havard.assemblyfun.data.tables.LocalTaskTable;
import me.havard.assemblyfun.data.tables.SolutionsTable;
import me.havard.assemblyfun.data.tables.TaskIDTable;
import me.havard.assemblyfun.data.tables.TaskRecordsTable;
import me.havard.assemblyfun.data.tables.TaskinfoTable;

/** A final class that contains methods used when modifying the assembly fun database.
 * Created by Havard on 26.09.2015.
 */
public final class AFDatabaseInteractionHelper
{
    /** Adds a row to the LocalTaskTable with the supplied _id_TaskIDs, taskText and taskTests.
     * Finds the taskInfo row(shouldn't be plural) with the same _id_TaskIDs and changes the local bit of the flag to 1 and saves it again
     * If there aren't any rows in the taskInfo table with the same _id_TaskIDs an error is logged
     * @param db The database object. Has to be readable and writable!
     * @param values A content values object that has no be cleared before the method is called
     * @param ref_id The _id_TaskIDs of the new row in the localTaskTable and the row(s) in the taskInfoTable to have their flags updated.
     * */
    public static void registerLocalTaskToDB(SQLiteDatabase db, ContentValues values, long ref_id, String taskText, String taskTests)
    {
        LocalTaskTable.addRow(db, values, ref_id, taskText, taskTests);
        values.clear();

        int flags = getFlagsFromTaskWithID(db, ref_id);
        if(flags>=0) {
            values.put(TaskinfoTable.FLAGS, flags | TaskinfoTable.FLAG_LOCAL); //kinda funky!;
            Log.d("Assembly Fun", "A task with the id " + ref_id + " is now registered as local!   OldFlags: " + flags + " NewFlags: " + (flags|TaskinfoTable.FLAG_LOCAL));
            db.update(TaskinfoTable.TABLE_NAME, values, TaskinfoTable._ID_TaskIDs + "=?", new String[]{Long.toString(ref_id)});
        } else {
            Log.e("Assembly Fun", "a local task row was added to localTaskTable, but there was no row in the taskInfoTable with the same _id_TaskIDs! The id in question is " + ref_id);
        }
    }

    /** Removes all rows (hopefully one) from the LocalTaskTable with the supplied _id_TaskIDs.
     * Finds the taskInfo row(shouldn't be plural) with the same _id_TaskIDs and changes the local bit of the flag to 0 and saves it again
     * * If there aren't any rows in the taskInfo table with the same _id_TaskIDs an error is logged
     * @param db The database object. Has to be readable and writable!
     * @param values A content values object that has no be cleared before the method is called
     * @param ref_id The _id_TaskIDs of the row(s) in the localTaskTable and the row(s) in the taskInfoTable to have their flags updated.
     */
    public static void removeLocalTaskFromDB(SQLiteDatabase db, ContentValues values, long ref_id)
    {
        LocalTaskTable.deleteRow(db, ref_id);

        int flags = getFlagsFromTaskWithID(db, ref_id);
        if(flags>=0) {
            values.put(TaskinfoTable.FLAGS, flags & ~TaskinfoTable.FLAG_LOCAL); //super funky!;
            Log.d("Assembly Fun", "A task with the id " + ref_id + " is no longer registered as local!   OldFlags: " + flags + " NewFlags: " + (flags & ~TaskinfoTable.FLAG_LOCAL));
            db.update(TaskinfoTable.TABLE_NAME, values, TaskinfoTable._ID_TaskIDs + "=?", new String[]{Long.toString(ref_id)});
        } else {
            Log.e("Assembly Fun", "a local task row was removed from localTaskTable, but there was no row in the taskInfoTable with the same _id_TaskIDs! The id in question is " + ref_id);
        }
    }

    private static final String[] TASK_TABLES_TO_BE_DELETED_FROM = {TaskinfoTable.TABLE_NAME, LocalTaskTable.TABLE_NAME, TaskRecordsTable.TABLE_NAME, SolutionsTable.TABLE_NAME};
    private static final String TASK_TABLES_WHERE_STATEMENT = TaskIDTable._ID_TaskIDs+"=?";
    private static final String TASK_ID_TABLE_WHERE_STATEMENT = TaskIDTable._ID+"=?";

    /** Removes all task data associated with a _id_TaskIDs
     * All rows where _id_TaskIDs (or just _id in the case of the TaskIDTable) = ref_id will be deleted from the following tables (in order):
     * *TaskInfoTable
     * *LocalTaskTable
     * *TaskRecordsTable
     * *SolutionsTable
     * *TaskIDTable
     * @param db The database object. Has to be writable.
     * @param ref_id the _id or _id_TaskIDs of the rows that will be deleted.
     */
    public static void deleteAllTaskData(SQLiteDatabase db, long ref_id)
    {
        String[] whereArgs = {Long.toString(ref_id)};
        for(String table:TASK_TABLES_TO_BE_DELETED_FROM)
            db.delete(table, TASK_TABLES_WHERE_STATEMENT, whereArgs);
        db.delete(TaskIDTable.TABLE_NAME, TASK_ID_TABLE_WHERE_STATEMENT, whereArgs);
    }

    /** Adds a new row to the solution table were the quality is failed and the records are all -1
     *
     * @param db a writable SQLiteDatabase object
     * @param values a ContentValues object that is cleared
     * @param task_id the _id_TaskIDs for the new solution. The task the solution is solving.
     * @param name the name of the solution
     * @param solutionText the solution text.
     * @return the _id of the new row in the SolutionTable
     */
    public static long addEmptySolution(SQLiteDatabase db, ContentValues values, long task_id, String name, String solutionText)
    {
        return SolutionsTable.addRow(db, values, task_id, name, solutionText, SolutionsTable.QUALITY_FAIL, -1, -1, -1);
    }

    private static final String WHERE_SOLUTION_ID_EQUAL_TO = SolutionsTable._ID+"=?";
    private static final String WHERE_RECORDS_ID_TASK_IDS_EQUAL_TO = TaskRecordsTable._ID_TaskIDs+"=?";
    private static final String SELECT_RECORDS_FOR_TASK_ID = "SELECT " + TaskRecordsTable.YOUR_SPEED_REC + ", " + TaskRecordsTable.SPEED_REC + ", " +
            TaskRecordsTable.YOUR_SIZE_REC + ", " + TaskRecordsTable.SIZE_REC + ", " + TaskRecordsTable.YOUR_MEMUSE_REC + ", " + TaskRecordsTable.MEMUSE_REC +
            " FROM " + TaskRecordsTable.TABLE_NAME + " WHERE " + WHERE_RECORDS_ID_TASK_IDS_EQUAL_TO + " LIMIT 1";

    /** Updates the quality, speed, size and memory usage of a row in the SolutionTable. If the quality isn't SolutionsTable.QUALITY_PERFECT the speed, size and memory will be null.
     * IF the quality is QUALITY_PERFECT the new speed, size and memory usage will be stored in the TaskRecordsTable. If there are no row's that already contain the task_id as their _id_TaskIDs,
     * a new one will be added were the new speed, size and memory usage are set as both local and global records, and the global record holders is set to "You"
     *
     * @param db A readable and writable SQLiteDatabase object.
     * @param values A ContentValues object that is cleared
     * @param solution_id the _id for the solution you want to update the values for
     * @param task_id the _id of the task that the solution is for. This can be -1, in which case this method will look up the _id_TaskIDs in the row in the SolutionsTable where _id = solution_id
     * @param quality the new quality of the solution. Either SolutionTable.QUALITY_FAIL or SolutionTable.QUALITY_PERFECT
     * @param speed the new (average) speed of the task
     * @param size the new size of the task
     * @param memuse the new (average) memory usage of the task.
     */
    public static void updateSolutionValues(SQLiteDatabase db, ContentValues values, long solution_id, long task_id, int quality, float speed, int size, float memuse)
    {
        values.put(SolutionsTable.SOLUTION_QUALITY, quality);
        values.put(SolutionsTable.SPEED, quality==SolutionsTable.QUALITY_PERFECT?speed:null);
        values.put(SolutionsTable.SIZE, quality==SolutionsTable.QUALITY_PERFECT?size:null);
        values.put(SolutionsTable.MEMUSE, quality == SolutionsTable.QUALITY_PERFECT ? memuse : null);
        db.update(SolutionsTable.TABLE_NAME, values, WHERE_SOLUTION_ID_EQUAL_TO, new String[]{Long.toString(solution_id)});

        if(quality!=SolutionsTable.QUALITY_PERFECT)
            return;
        Cursor taskRecords = db.rawQuery(SELECT_RECORDS_FOR_TASK_ID, new String[]{Long.toString(task_id)});
        //noinspection TryFinallyCanBeTryWithResources
        try {
            taskRecords.moveToFirst();
            if (taskRecords.isAfterLast()) {
                values.clear();
                TaskRecordsTable.addRow(db, values, task_id, speed, "You", speed, size, "You", size, memuse, "You", memuse);
            }
            else { //There already is a row in the taskRecordTable for this task. Let's check if we've beat any of the previous records.
                values.clear();
                if(speed < taskRecords.getFloat(taskRecords.getColumnIndex(TaskRecordsTable.YOUR_SPEED_REC))) {   //New local speed record!
                    values.put(TaskRecordsTable.YOUR_SPEED_REC, speed);
                    if(speed < taskRecords.getFloat(taskRecords.getColumnIndex(TaskRecordsTable.SPEED_REC))) {   //New global speed record!
                        values.put(TaskRecordsTable.SPEED_REC, speed);
                        values.put(TaskRecordsTable.SPEED_REC_NAME, "You");
                    }
                }
                if(size < taskRecords.getInt(taskRecords.getColumnIndex(TaskRecordsTable.YOUR_SIZE_REC))) {   //New local size record!
                    values.put(TaskRecordsTable.YOUR_SIZE_REC, size);
                    if(size < taskRecords.getFloat(taskRecords.getColumnIndex(TaskRecordsTable.SIZE_REC))) {   //New global size record!
                        values.put(TaskRecordsTable.SIZE_REC, size);
                        values.put(TaskRecordsTable.SIZE_REC_NAME, "You");
                    }
                }
                if(memuse < taskRecords.getFloat(taskRecords.getColumnIndex(TaskRecordsTable.YOUR_MEMUSE_REC))) {  //New local memory usage record!
                    values.put(TaskRecordsTable.YOUR_MEMUSE_REC, memuse);
                    if(memuse < taskRecords.getFloat(taskRecords.getColumnIndex(TaskRecordsTable.MEMUSE_REC))) {  //New global memory usage record!
                        values.put(TaskRecordsTable.MEMUSE_REC, memuse);
                        values.put(TaskRecordsTable.MEMUSE_REC_NAME, "You");
                    }
                }
                if(values.size()>0) //New records, baby!
                    db.update(TaskRecordsTable.TABLE_NAME, values, WHERE_RECORDS_ID_TASK_IDS_EQUAL_TO, new String[]{Float.toString(task_id)});
            }
        } finally {
            taskRecords.close();
        }
    }

    /* Adds (or updates if there's already a row with the same ref_id) a row to the taskRecordsTable with all the values passed. If a value is -1 or a string is null the field will not be added/updated.
     * This method is damaging to the database because the fields should never be null!
     * @param db A readable and writable SQLiteDatabase object.
     * @param values A ContentValues object. Has to be cleared. Will be used in the method.
     * @param ref_id The _id_TaskIDs of the row in the taskRecordsTable to wither be added or updated with the new values.
     * @param speed_rec The global record for speed. The average of the run time for all tests. Lower is better.
     * @param speed_rec_name The name of the record setter in the speed category.
     * @param your_speed_rec The local speed record, made on the device.
     * @param size_rec The global size record. Smaller is better
     * @param size_rec_name The record holder globally for the size record.
     * @param your_size_rec The owner of the devices size record.
     * @param memuse_rec The global record for the least amount of memory used on average in all the public tests. Can be 0.
     * @param memuse_rec_name The holder for the global record of least amount of memory used
     * @param your_memuse_rec The device owners record for least amount of memory used.

    public static void addOrUpdateRecords(SQLiteDatabase db, ContentValues values, long ref_id, float speed_rec, String speed_rec_name, float your_speed_rec,
                                          int size_rec, String size_rec_name, int your_size_rec, float memuse_rec, String memuse_rec_name, float your_memuse_rec)
    {
        if(containsRowWithWhereStatement(db, TaskRecordsTable.TABLE_NAME, TaskRecordsTable._ID_TaskIDs, Long.toString(ref_id)))
            TaskRecordsTable.updateRow(db, values, ref_id, speed_rec, speed_rec_name, your_speed_rec, size_rec, size_rec_name, your_size_rec, memuse_rec, memuse_rec_name, your_memuse_rec);
        else
            TaskRecordsTable.addRow(db, values, ref_id, speed_rec, speed_rec_name, your_speed_rec, size_rec, size_rec_name, your_size_rec, memuse_rec, memuse_rec_name, your_memuse_rec);
    }*/

    /** Adds a row to the taskInfoTable with the supplied values (except for the globalID).
     * The globalID is used to look up in the TaskIDTable. If it already exists there, the _id (localID) from that row in the TaskIDTable is used as the _id_TaskIDs for the new row in the taskInfoTable.
     * Otherwise, if the globalID is new or 0, a new row is added to the TaskIDTable with the supplied globalID (NULL if globalID==0). The _id of this new row is used as the _id_TaskIDs for the new taskInfoTable row.
     *
     * @param db A readable and writable SQLIteDatabase object
     * @param values A ContentValues object that is cleared.
     * @param name The name for the NAME column in the new row
     * @param desc for the DESC column in the new row.
     * @param date for the DATA column in the new row. Milliseconds since Jan 1. 1970
     * @param diff for the DIFFICULTY column in the new row. An integer corresponing to one of the difficulties in the Difficulty enum.
     * @param rating for the RATING column in the new row. A float.
     * @param author for the AUTHOR column in the new row. A TEXT.
     * @param solved whether or not the task is solved. Stored in the FLAGS column in the new row.
     * @param selfPublished whether or not the task is published on this device. Stored in the FLAGS column in the new row.
     * @param favourite whether or not the task is a favourite. Stored in the FLAGS column in the new row.
     * @param globalID the globalID of the new task. If it already exists in the TaskIDTable that row is used for the _id_TaskIDs foreign key, otherwise a new row in TaskIDTable is added with this as the globalID.
     *                 It it is 0, a new TaskIDTable row is added and used without any globalID.
     * @return the _id_TaskIDs of the new row in the taskInfoTable
     */
    public static long addTaskInfoToTables(SQLiteDatabase db, ContentValues values, String name, String desc, long date, Difficulty diff, float rating, String author,
                                           boolean solved, boolean selfPublished, boolean favourite, long globalID)
    {
        long localID=getLocalIDFromGlobalID(db, values, globalID);
        values.clear();

        TaskinfoTable.addRow(db, values, localID, name, desc, date, diff, rating, author, TaskinfoTable.getFlags(false, solved, selfPublished, favourite, globalID != 0));
        return localID;
    }

    /** Finds a row in the TaskIDTable with the supplied globalID, or adds one if no row with that globalID already exists (or the globalID == 0, in which case a new row with globalID=NULL is added).
     *
     * @param db A readable and writable SQLiteDatabase object.
     * @param values A ContentValues object that is cleared.
     * @param globalID The globalID that will be checked for in the TaskIDTable, or added if it doesn't exist
     * @return The _id of the row added/found. The row will have the supplied globalID as it's global ID.
     */
    public static long getLocalIDFromGlobalID(SQLiteDatabase db, ContentValues values, long globalID)
    {
        if(globalID==0)
            return TaskIDTable.addRow(db, values);
        else{
            Cursor c = db.query(TaskIDTable.TABLE_NAME, new String[]{TaskIDTable._ID, TaskIDTable.GLOBAL_TASK_ID}, TaskIDTable.GLOBAL_TASK_ID+"=?", new String[]{Long.toString(globalID)}, null, null, null, "1");
            long output;
            if(c.isAfterLast())
                output = TaskIDTable.addRow(db, values, globalID);
            else {
                c.moveToFirst();
                output = c.getLong(c.getColumnIndex(TaskIDTable._ID));
            }

            c.close();

            return output;
        }
    }

    /** Returns true if there in the supplied database exists a row in the table 'tableName' where 'columnName'='columnValueWanted'.
     *
     * @param db A readable SQLiteDatabase object.
     * @param tableName The name of the table to scan. "FROM tableName"
     * @param columnName The name of the column
     * @param columnValueWanted The values you want to see if exists in a column.
     * @return Does the wanted value exist in the column in any row of the table in the database?
     */
    public static boolean containsRowWithWhereStatement(SQLiteDatabase db, String tableName, String columnName, String columnValueWanted)
    {
        Cursor c = db.query(tableName, new String[]{columnName}, columnName+"=?", new String[]{columnValueWanted}, null, null, null, "1");
        boolean output = c.getCount() > 0;
        c.close();
        return output;
    }

    private static final String TASK_INFO_TABLE_FLAGS_QUERY = "SELECT " + TaskinfoTable.FLAGS + " FROM " + TaskinfoTable.TABLE_NAME + " WHERE " + TaskinfoTable._ID_TaskIDs + "=?";

    /** Gets the 'flags' column from the first row in the TaskInfoTable WHERE _id_TaskIDs = ref_id
     *
     * @param db A readable SQLiteDatabase object.
     * @param ref_id The _id_TaskIDs to use in the where statement
     * @return The value in the flags column in the first row where _id_TaskIDs = ref_id, of -1 if such a row doesn't exist
     */
    public static int getFlagsFromTaskWithID(SQLiteDatabase db, long ref_id)
    {
        int output=-1;
        Cursor cursor = db.rawQuery(TASK_INFO_TABLE_FLAGS_QUERY, new String[]{Long.toString(ref_id)});
        //noinspection TryFinallyCanBeTryWithResources
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                output = cursor.getInt(cursor.getColumnIndex(TaskinfoTable.FLAGS));
            }
        }
        finally {
            cursor.close();
        }
        return output;
    }
}
