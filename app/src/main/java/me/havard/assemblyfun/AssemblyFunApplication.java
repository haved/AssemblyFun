package me.havard.assemblyfun;

import android.app.Application;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;

import me.havard.assemblyfun.data.TaskDatabaseOpenHelper;

public class AssemblyFunApplication extends Application {

    private TaskDatabaseOpenHelper db;

    public TaskDatabaseOpenHelper getDatabase()
    {
        return db;
    }

    public SQLiteDatabase getReadableDatabase()
    {
        return db.getReadableDatabase();
    }

    public SQLiteDatabase getWritableDatabase()
    {
        return db.getWritableDatabase();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(db!=null)
            db.close();
        db=new TaskDatabaseOpenHelper(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if(db!=null)
            db.close();
        db=null;
    }

}