package me.havard.assemblyfun.data.tables;

/** A class my database tables inherit
 * Created by Havard on 13/09/2015.
 */
public abstract class Table {
    public static final String INT = "INT";
    public static final String REEL = "REEL";
    public static final String TEXT = "TEXT";
    public static final String BLOB = "BLOB";
    public static final String PRIMARY_KEY_AUTOINCREMENT = "INTEGER AUTOINCREMENT NOT NULL PRIMARY KEY ASC";
    public static final String PRIMARY_KEY = "INTEGER NOT NULL PRIMARY KEY ASC";

    public abstract String getCreateString();
    public abstract String getTableName();

    public static String getSQLCreate(String name, String... values)
    {
        StringBuilder out = new StringBuilder("CREATE TABLE ");
        out.append(name);
        out.append("( ");
        int i;
        for(i = 0; i+1 < values.length; i+=2)
        {
            out.append(values[i]);
            out.append(" ");
            out.append(values[i+1]);
            if(i+2<values.length)
                out.append(", ");
        }
        if(i<values.length)
            out.append(values[i]);
        out.append(");");

        return out.toString();
    }
}
