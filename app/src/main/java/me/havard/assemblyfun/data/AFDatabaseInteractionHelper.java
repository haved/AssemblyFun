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
    /** Finds a row in the TaskIDTable with the supplied globalID, or adds one if no row with that globalID already exists (or the globalID == 0, in which case a new row with globalID=NULL is added).
     *
     * @param db A readable and writable SQLiteDatabase object.
     * @param values A ContentValues object that is cleared.
     * @param globalID The globalID that will be checked for in the TaskIDTable, or added if it doesn't exist
     * @return The _id of the row added/found. The row will have the supplied globalID as it's global ID.
     */
    public static long getLocalIDFromGlobalID(SQLiteDatabase db, ContentValues values, long globalID) {
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
     * @param author for the AUTHOR column in the new row. A TYPE_TEXT.
     * @param selfPublished whether or not the task is published on this device. Stored in the FLAGS column in the new row.
     * @param favourite whether or not the task is a favourite. Stored in the FLAGS column in the new row.
     * @param globalID the globalID of the new task. If it already exists in the TaskIDTable that row is used for the _id_TaskIDs foreign key, otherwise a new row in TaskIDTable is added with this as the globalID.
     *                 It it is 0, a new TaskIDTable row is added and used without any globalID.
     * @return the _id_TaskIDs of the new row in the taskInfoTable
     */
    public static long addTaskInfoToTables(SQLiteDatabase db, ContentValues values, String name, String desc, long date, Difficulty diff, float rating, String author,
                                           boolean selfPublished, boolean favourite, long globalID) {
        long localID = -1;

        try {
            db.beginTransaction();
            localID = getLocalIDFromGlobalID(db, values, globalID);
            values.clear();

            TaskinfoTable.addRow(db, values, localID, name, desc, date, diff, rating, author, TaskinfoTable.getFlags(false, false, selfPublished, favourite, globalID != 0));
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return localID;
    }

    private static final String WHERE_INFO_ID_TASK_IDS_EQUAL_TO = TaskinfoTable._ID_TaskIDs + "=?";
    private static final String TASK_INFO_TABLE_FLAGS_QUERY = String.format("SELECT %s FROM %s WHERE %s", TaskinfoTable.FLAGS, TaskinfoTable.TABLE_NAME, WHERE_INFO_ID_TASK_IDS_EQUAL_TO);

    /** Gets the 'flags' column from the first row in the TaskInfoTable WHERE _id_TaskIDs = ref_id
     *
     * @param db A readable SQLiteDatabase object.
     * @param ref_id The _id_TaskIDs to use in the where statement
     * @return The value in the flags column in the first row where _id_TaskIDs = ref_id, of -1 if such a row doesn't exist
     */
    public static int getFlagsFromTaskWithID(SQLiteDatabase db, long ref_id) {
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

    /** A method that will get the flag from a task with a certain _id_TaskIDs. If the favourite part of the
     *
     * @param db A writable and readable database object
     * @param values A clean ContentValues instance
     * @param taskID The _id_TaskIDs of the task to be flag updated
     * @param flag The flag value in question. Has to be a power of two or else strange behavior might occur.
     * @return The new state of the flag in question in the database row. Probably the same as shouldHaveFlag.
     */
    public static boolean changeFlag(SQLiteDatabase db, ContentValues values, long taskID, int flag, boolean shouldHaveFlag) {
        int flags = AFDatabaseInteractionHelper.getFlagsFromTaskWithID(db, taskID);
        if (TaskinfoTable.hasFlag(flags, flag) == shouldHaveFlag) {
            Log.w("Assembly Fun", "Tasked with giving/taking from the task " + taskID + " the flag " + flag + " shouldHaveFlag=" + shouldHaveFlag + ". But the flags are already set up this way");
            return shouldHaveFlag;
        }

        if (shouldHaveFlag)
            flags = TaskinfoTable.addFlag(flags, flag);
        else
            flags = TaskinfoTable.removeFlag(flags, flag);

        values.put(TaskinfoTable.FLAGS, flags);
        db.update(TaskinfoTable.TABLE_NAME, values, WHERE_INFO_ID_TASK_IDS_EQUAL_TO, new String[]{Long.toString(taskID)});
        return shouldHaveFlag;
    }


    /** Adds a row to the LocalTaskTable with the supplied _id_TaskIDs, taskText and taskTests.
     * Finds the taskInfo row(shouldn't be plural) with the same _id_TaskIDs and changes the local bit of the flag to 1 and saves it again
     * If there aren't any rows in the taskInfo table with the same _id_TaskIDs an error is logged
     * @param db The database object. Has to be readable and writable!
     * @param values A content values object that has no be cleared before the method is called
     * @param ref_id The _id_TaskIDs of the new row in the localTaskTable and the row(s) in the taskInfoTable to have their flags updated.
     * */
    public static void registerLocalTaskToDB(SQLiteDatabase db, ContentValues values, long ref_id, String taskText, String taskTests) {
        LocalTaskTable.addRow(db, values, ref_id, taskText, taskTests);
        values.clear();

        int flags = getFlagsFromTaskWithID(db, ref_id);
        if(flags>=0) {
            values.put(TaskinfoTable.FLAGS, TaskinfoTable.addFlag(flags, TaskinfoTable.FLAG_LOCAL)); //Used to be kinda funky!;
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
    public static void removeLocalTaskFromDB(SQLiteDatabase db, ContentValues values, long ref_id) {
        LocalTaskTable.deleteRow(db, ref_id);

        int flags = getFlagsFromTaskWithID(db, ref_id);
        if(flags>=0) {
            values.put(TaskinfoTable.FLAGS, TaskinfoTable.removeFlag(flags, TaskinfoTable.FLAG_LOCAL)); //Used to be super funky
            db.update(TaskinfoTable.TABLE_NAME, values, TaskinfoTable._ID_TaskIDs + "=?", new String[]{Long.toString(ref_id)});
        } else {
            Log.e("Assembly Fun", "a local task row was removed from localTaskTable, but there was no row in the taskInfoTable with the same _id_TaskIDs! The id in question is " + ref_id);
        }
    }

    public static String getTaskTests(SQLiteDatabase db, long task_id) {
        Cursor cursor = makeCursorForField(db, LocalTaskTable.TABLE_NAME, LocalTaskTable.TASK_TESTS, WHERE_INFO_ID_TASK_IDS_EQUAL_TO, new String[]{Long.toString(task_id)}, "LIMIT 1");
        cursor.moveToFirst();
        if(cursor.isAfterLast()) {
            Log.e("Assembly Fun", "Tried to ge the taskTests for the task where task_id=" + task_id + " but the cursor found no rows! Returning an empty string as in \"No tests\"");
            return "";
        }
        String output = cursor.getString(cursor.getColumnIndex(LocalTaskTable.TASK_TESTS)); //Should be 0
        cursor.close();
        return output;
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
    public static long addEmptySolution(SQLiteDatabase db, ContentValues values, long task_id, String name, String solutionText) {
        return SolutionsTable.addRow(db, values, task_id, name, solutionText, SolutionsTable.QUALITY_NEVER_RUN, -1, -1, -1);
    }

    private static final String WHERE_SOLUTION_ID_EQUAL_TO = SolutionsTable._ID+"=?";
    public static void changeSolutionText(SQLiteDatabase db, ContentValues values, long solution_id, String newText) {
        values.put(SolutionsTable.SOLUTION_TEXT, newText);
        db.update(SolutionsTable.TABLE_NAME, values, WHERE_SOLUTION_ID_EQUAL_TO, new String[]{Long.toString(solution_id)});
    }

    public static void changeSolutionTitle(SQLiteDatabase db, ContentValues values, long solution_id, String newTitle) {
        values.put(SolutionsTable.TITLE, newTitle);
        db.update(SolutionsTable.TABLE_NAME, values, WHERE_SOLUTION_ID_EQUAL_TO, new String[]{Long.toString(solution_id)});
    }

    private static final String SELECT_ALL_FROM_SOLUTIONS_TABLE_WHERE = makeCursorTextForField(SolutionsTable.TABLE_NAME, "*", WHERE_SOLUTION_ID_EQUAL_TO, "LIMIT 1");
    public static long copySolution(SQLiteDatabase db, ContentValues values, long solution_id, long task_id, String newTitle) {
        long output = -1;
        try {
            db.beginTransaction();
            Cursor cursor = db.rawQuery(SELECT_ALL_FROM_SOLUTIONS_TABLE_WHERE, new String[]{Long.toString(solution_id)});
            cursor.moveToFirst();
            if (cursor.isAfterLast())
                Log.e("Assembly Fun", "Could not copy a solution that doesn't exist!");
            else {
                values.put(SolutionsTable._ID_TaskIDs, task_id);
                values.put(SolutionsTable.TITLE, newTitle);
                values.put(SolutionsTable.SOLUTION_TEXT, cursor.getString(cursor.getColumnIndex(SolutionsTable.SOLUTION_TEXT)));
                values.put(SolutionsTable.SOLUTION_QUALITY, cursor.getInt(cursor.getColumnIndex(SolutionsTable.SOLUTION_QUALITY)));
                values.put(SolutionsTable.SPEED, cursor.getFloat(cursor.getColumnIndex(SolutionsTable.SPEED)));
                values.put(SolutionsTable.SIZE, cursor.getInt(cursor.getColumnIndex(SolutionsTable.SIZE)));
                values.put(SolutionsTable.MEMUSE, cursor.getFloat(cursor.getColumnIndex(SolutionsTable.MEMUSE)));
                output = db.insert(SolutionsTable.TABLE_NAME, null, values);
                db.setTransactionSuccessful();
            }
        }
        finally {
            db.endTransaction();
        }
        return output;
    }

    public static void deleteSolution(SQLiteDatabase db, long solution_id) {
        try {
            db.beginTransaction();
            db.delete(SolutionsTable.TABLE_NAME, WHERE_SOLUTION_ID_EQUAL_TO, new String[]{Long.toString(solution_id)});
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }

    private static final String SOLUTION_TITLE_QUERY = makeCursorTextForField(SolutionsTable.TABLE_NAME, SolutionsTable.TITLE, WHERE_SOLUTION_ID_EQUAL_TO, "LIMIT 1");
    public static String getSolutionTitle(SQLiteDatabase db, long solution_id) {
        Cursor cursor = db.rawQuery(SOLUTION_TITLE_QUERY, new String[]{Long.toString(solution_id)});
        cursor.moveToFirst();
        if(cursor.isAfterLast()) {
            Log.e("Assembly Fun", "getSolutionTitle could not find a title for the solution with id " + solution_id);
            return null;
        }
        String output = cursor.getString(cursor.getColumnIndex(SolutionsTable.TITLE));
        cursor.close();
        return output;
    }

    private static final String WHERE_RECORDS_ID_TASK_IDS_EQUAL_TO = TaskRecordsTable._ID_TaskIDs+"=?";
    private static final String SELECT_RECORDS_FOR_TASK_ID = "SELECT " + TaskRecordsTable.PERSONAL_SPEED_REC + ", " + TaskRecordsTable.SPEED_REC + ", " +
            TaskRecordsTable.PERSONAL_SIZE_REC + ", " + TaskRecordsTable.SIZE_REC + ", " + TaskRecordsTable.PERSONAL_MEMUSE_REC + ", " + TaskRecordsTable.MEMUSE_REC +
            " FROM " + TaskRecordsTable.TABLE_NAME + " WHERE " + WHERE_RECORDS_ID_TASK_IDS_EQUAL_TO + " LIMIT 1";

    /** Updates the quality, speed, size and memory usage of a row in the SolutionTable. If the quality isn't SolutionsTable.QUALITY_PERFECT the speed, size and memory will be null.
     * If the quality is QUALITY_PERFECT the new records (if any) for speed, size and memory usage will be stored in the TaskRecordsTable. The taskInfoTable row for this task will also get it's flags set to solved if it isn't already.
     * If there are no row's that already contain the task_id as their _id_TaskIDs,
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
    public static void updateSolutionValues(SQLiteDatabase db, ContentValues values, long solution_id, long task_id, int quality, float speed, int size, float memuse) {
        values.put(SolutionsTable.SOLUTION_QUALITY, quality);
        values.put(SolutionsTable.SPEED, quality==SolutionsTable.QUALITY_SOLVED?speed:null);
        values.put(SolutionsTable.SIZE, quality==SolutionsTable.QUALITY_SOLVED?size:null);
        values.put(SolutionsTable.MEMUSE, quality == SolutionsTable.QUALITY_SOLVED ? memuse : null);
        db.update(SolutionsTable.TABLE_NAME, values, WHERE_SOLUTION_ID_EQUAL_TO, new String[]{Long.toString(solution_id)});

        if(quality!=SolutionsTable.QUALITY_SOLVED) //No point in checking any records or flags if the solution isn't a proper one.
            return;

        if(task_id <= 0) //We find the task_id
            task_id = getTaskIdFromSolutionId(db, solution_id);

        int flags = getFlagsFromTaskWithID(db, task_id); //Get the flags we already have
        if(!TaskinfoTable.hasFlag(flags, TaskinfoTable.FLAG_SOLVED)) { //If the task didn't have the FLAGS_SOLVED flag...
            flags = TaskinfoTable.addFlag(flags, TaskinfoTable.FLAG_SOLVED); // flags |= TaskinfoTable.FLAG_SOLVED
            values.clear();
            values.put(TaskinfoTable.FLAGS, flags);
            db.update(TaskinfoTable.TABLE_NAME, values, TaskinfoTable._ID_TaskIDs + "=?", new String[]{Long.toString(task_id)});
            Log.i("Assembly Fun", "Because the TaskinfoTable row for the task " + task_id + " didn't have the solved flag set when a solution for this task got set to QUALITY_PERFECT, the flag was updated!");
        }

        Cursor taskRecords = db.rawQuery(SELECT_RECORDS_FOR_TASK_ID, new String[]{Long.toString(task_id)});
        //noinspection TryFinallyCanBeTryWithResources
        try {
            taskRecords.moveToFirst();
            if (taskRecords.isAfterLast()) { //There is no row in the taskRecordsTable for this task. Let's make one!
                values.clear();
                TaskRecordsTable.addRow(db, values, task_id, speed, "You", speed, size, "You", size, memuse, "You", memuse); //We supply our own scores as global and local high scores.
            }
            else { //There already is a row in the taskRecordTable for this task. Let's check if we've beat any of the previous records.
                values.clear(); //Here we clear the values. This instance will be used for all the new records
                if(speed < taskRecords.getFloat(taskRecords.getColumnIndex(TaskRecordsTable.PERSONAL_SPEED_REC))) {   //New local speed record!
                    values.put(TaskRecordsTable.PERSONAL_SPEED_REC, speed);
                    if(speed < taskRecords.getFloat(taskRecords.getColumnIndex(TaskRecordsTable.SPEED_REC))) {   //New global speed record!
                        values.put(TaskRecordsTable.SPEED_REC, speed);
                        values.put(TaskRecordsTable.SPEED_REC_NAME, "You");
                    }
                }
                if(size < taskRecords.getInt(taskRecords.getColumnIndex(TaskRecordsTable.PERSONAL_SIZE_REC))) {   //New local size record!
                    values.put(TaskRecordsTable.PERSONAL_SIZE_REC, size);
                    if(size < taskRecords.getFloat(taskRecords.getColumnIndex(TaskRecordsTable.SIZE_REC))) {   //New global size record!
                        values.put(TaskRecordsTable.SIZE_REC, size);
                        values.put(TaskRecordsTable.SIZE_REC_NAME, "You");
                    }
                }
                if(memuse < taskRecords.getFloat(taskRecords.getColumnIndex(TaskRecordsTable.PERSONAL_MEMUSE_REC))) {  //New local memory usage record!
                    values.put(TaskRecordsTable.PERSONAL_MEMUSE_REC, memuse);
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

    /** Does as expected
     *
     * @param db a readable SQLiteDatabase object
     * @param solution_id the _id of the solution
     * @return the _id_TaskIDs of the solution (the _id of the task the solution is for)
     */
    public static long getTaskIdFromSolutionId(SQLiteDatabase db, long solution_id) {
        Cursor cursor = makeCursorForField(db, SolutionsTable.TABLE_NAME, SolutionsTable._ID_TaskIDs, SolutionsTable._ID + "=?", new String[]{Long.toString(solution_id)}, "LIMIT 1");
        //noinspection TryFinallyCanBeTryWithResources
        try {
            cursor.moveToFirst();
            if(cursor.isAfterLast()) {
                Log.e("Assembly Fun", "No task_id was supplied when the solution vales were updated for solution with id: " + solution_id +
                        ". Tried looking up the task_id in the SolutionsTable, but the row where _id=solution_id doesn't exist!");
                return -1; //Will still close the cursor!
            }
            return cursor.getLong(cursor.getColumnIndex(SolutionsTable._ID_TaskIDs)); // getColumnIndex should in theory always return 0, but what the heck. One can almost never be too careful!
        } catch(Exception e) {
            Log.wtf("Assembly Fun", e);
        }finally {
            Log.i("Assembly Fun", "Just to be sure... Yes, the cursor was closed!");
            cursor.close();
        }
        return -1;
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
    public static void deleteAllTaskData(SQLiteDatabase db, long ref_id) {
        String[] whereArgs = {Long.toString(ref_id)};
        for(String table:TASK_TABLES_TO_BE_DELETED_FROM)
            db.delete(table, TASK_TABLES_WHERE_STATEMENT, whereArgs);
        db.delete(TaskIDTable.TABLE_NAME, TASK_ID_TABLE_WHERE_STATEMENT, whereArgs);
    }

    /** Returns true if there in the supplied database exists a row in the table 'tableName' where 'columnName'='columnValueWanted'.
     *
     * @param db A readable SQLiteDatabase object.
     * @param tableName The name of the table to scan. "FROM tableName"
     * @param columnName The name of the column
     * @param columnValueWanted The values you want to see if exists in a column.
     * @return Does the wanted value exist in the column in any row of the table in the database?
     */
    public static boolean containsRowWithWhereStatement(SQLiteDatabase db, String tableName, String columnName, String columnValueWanted) {
        Cursor c = db.query(tableName, new String[]{columnName}, columnName+"=?", new String[]{columnValueWanted}, null, null, null, "1");
        boolean output = c.getCount() > 0;
        c.close();
        return output;
    }

    public static Cursor makeCursorForField(SQLiteDatabase db, String tableName, String columns, String whereStatement, String[] whereArgs, String queryExtras) {
        return db.rawQuery(makeCursorTextForField(tableName, columns, whereStatement, queryExtras), whereArgs);
    }

    public static String makeCursorTextForField(String tableName, String columns, String whereStatement, String queryExtras) {
        return String.format("SELECT %s FROM %s WHERE %s %s", columns, tableName, whereStatement, queryExtras);
    }

    private static ContentValues valuesInstance = new ContentValues();
    public static ContentValues getClearedContentValuesInstance() {
        valuesInstance.clear();
        return valuesInstance;
    }

    public static void clearContentValuesInstance()
    {
        valuesInstance.clear();
    }
}
