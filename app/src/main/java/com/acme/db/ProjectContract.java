package com.acme.db;

import android.provider.BaseColumns;

public class ProjectContract {



    /**
     * Contains the name of the table to create that contans the row counters.
     */
    public static final String KEY_TABLE_NAME = "key_flag";
    public static final String FILE_TABLE_NAME = "file_pointer";

    /**
     * Contains the SQL query to use to create the table containing the projects.
     */
    public static final String SQL_CREATE_KEY_TABLE = "CREATE TABLE " + KEY_TABLE_NAME +
            " ("+ ProjectEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ProjectEntry.COLUMN_KEY_NAME + " TEXT,"
            + ProjectEntry.COLUMN_KEY_VALUE + " TEXT);";

    public static final String SQL_CREATE_FILE_TABLE = "CREATE TABLE " + FILE_TABLE_NAME +
            " ("+ ProjectEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ProjectEntry.COLUMN_FILE_NAME + " TEXT,"
            + ProjectEntry.COLUMN_FILE_STATUS + " TEXT);";

    /**
     * This class represents the rows for an entry in the project table. The
     * primary key is the _id column from the BaseColumn class.
     */
    public static abstract class ProjectEntry implements BaseColumns {
        // Name of the project as shown in the application.
        public static final String COLUMN_KEY_NAME = "name";
        public static final String COLUMN_KEY_VALUE = "value";

        public static final String COLUMN_FILE_NAME = "name";
        public static final String COLUMN_FILE_STATUS = "status";
    }

}
