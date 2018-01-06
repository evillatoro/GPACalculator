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

    public static class DatabaseCourse {
        public static final String TABLE_NAME = "courses";
        public static final String COLUMN_COURSE_NAME = "courseName";
        public static final String COLUMN_COURSE_SEMESTER = "courseSemester";
        public static final String COLUMN_COURSE_CREDITS = "courseCredits";
        public static final String COLUMN_COURSE_GRADE = "courseGrade";
        public static final String COLUMN_COURSE_PREDICTED = "coursePredicted";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_COURSE_NAME + " TEXT," +
                COLUMN_COURSE_SEMESTER + " TEXT, " +
                COLUMN_COURSE_CREDITS + " FLOAT, " +
                COLUMN_COURSE_GRADE + " FLOAT, " +
                COLUMN_COURSE_PREDICTED + " INTEGER," +
                "PRIMARY KEY(" + COLUMN_COURSE_NAME + ", " + COLUMN_COURSE_SEMESTER + ")," +
                "FOREIGN KEY(" + COLUMN_COURSE_SEMESTER + ") REFERENCES " +
                DatabaseSemester.TABLE_NAME + "(" + DatabaseSemester.COLUMN_SEMESTER_NAME + ") " + " ON DELETE CASCADE" +")";
    }

}
