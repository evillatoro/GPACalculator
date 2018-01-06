package me.edwinvillatoro.gpacalculator.database;

/**
 * Created by Edwin Villatoro on 1/5/2018.
 */
public class DatabaseContract {

    private DatabaseContract() {

    }

    public static class DatabaseSemester {
        public static final String TABLE_NAME = "semesters";
        public static final String COLUMN_SEMESTER_NAME = "semesterName";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_SEMESTER_NAME + " TEXT PRIMARY KEY" + ")";
    }

}
