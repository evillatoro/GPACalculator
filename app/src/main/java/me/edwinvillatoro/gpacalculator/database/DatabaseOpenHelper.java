package me.edwinvillatoro.gpacalculator.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import me.edwinvillatoro.gpacalculator.database.DatabaseContract.DatabaseSemester;
import me.edwinvillatoro.gpacalculator.database.DatabaseContract.DatabaseCourse;
import me.edwinvillatoro.gpacalculator.model.Course;
import me.edwinvillatoro.gpacalculator.model.Semester;

/**
 * Created by Edwin Villatoro on 1/5/2018.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseOpenHelper";

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "gpa_calculator_database";

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseSemester.CREATE_TABLE);
        db.execSQL(DatabaseCourse.CREATE_TABLE);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseSemester.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseCourse.TABLE_NAME);
        onCreate(db);
    }

    public boolean addSemesterToDatabase(String semesterName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseSemester.COLUMN_SEMESTER_NAME, semesterName);

        long result = db.insert(DatabaseSemester.TABLE_NAME, null, contentValues);

        db.close();
        // if result equals -1, semester was not inserted
        return result != -1;
    }

    public boolean addCourseToDatabase(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseCourse.COLUMN_COURSE_NAME, course.getName());
        contentValues.put(DatabaseCourse.COLUMN_COURSE_SEMESTER, course.getSemester());
        contentValues.put(DatabaseCourse.COLUMN_COURSE_CREDITS, course.getCredits());
        contentValues.put(DatabaseCourse.COLUMN_COURSE_GRADE, course.getGrade());
        contentValues.put(DatabaseCourse.COLUMN_COURSE_PREDICTED, course.getPredicted());

        long result = db.insert(DatabaseCourse.TABLE_NAME, null, contentValues);

        db.close();
        // if result equals -1, course was not inserted
        return result != -1;
    }

    public void deleteSemesterFromDatabase(String semesterName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection =
                DatabaseSemester.COLUMN_SEMESTER_NAME + " = ?";
        String[] selectionArgs = { semesterName};
        db.delete(DatabaseSemester.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    public void deleteCourseFromDatabase(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection =
                DatabaseCourse.COLUMN_COURSE_SEMESTER + " = ?"+
                        "AND " + DatabaseCourse.COLUMN_COURSE_NAME + " = ?";

        String[] selectionArgs = { course.getSemester(), course.getName()};

        db.delete(DatabaseCourse.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    public List<Semester> getAllSemestersFromDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM " + DatabaseSemester.TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        List<Semester> semesterList = new ArrayList<>();
        while (data.moveToNext()) {
            Semester semester = new Semester(data.getString(0));
            semesterList.add(0, semester);
        }

        data.close();
        db.close();
        return semesterList;

    }

    public List<Course> getCoursesForSemesterFromDatabase(String semesterName) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM " + DatabaseCourse.TABLE_NAME + " WHERE "
                + DatabaseCourse.COLUMN_COURSE_SEMESTER + "= \"" + semesterName + "\"";
        List<Course> courseList = new ArrayList<>();
        Cursor data = db.rawQuery(query, null);
        while (data.moveToNext()) {
            Course course = new Course(data.getString(0),
                    data.getString(1),
                    data.getDouble(2),
                    data.getDouble(3),
                    data.getInt(4));
            courseList.add(course);
        }

        data.close();
        db.close();
        return courseList;
    }

    public boolean updateCourseInDatabase(Course course, String oldCourseName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseCourse.COLUMN_COURSE_NAME, course.getName());
        contentValues.put(DatabaseCourse.COLUMN_COURSE_SEMESTER, course.getSemester());
        contentValues.put(DatabaseCourse.COLUMN_COURSE_CREDITS, course.getCredits());
        contentValues.put(DatabaseCourse.COLUMN_COURSE_GRADE, course.getGrade());
        contentValues.put(DatabaseCourse.COLUMN_COURSE_PREDICTED, course.getPredicted());

        String selection =
                DatabaseCourse.COLUMN_COURSE_NAME + " = ?" +
                        "AND " + DatabaseCourse.COLUMN_COURSE_SEMESTER + " = ?";

        String[] selectionArgs = { oldCourseName, course.getSemester()};

        try {
            long result = db.update(DatabaseCourse.TABLE_NAME,
                    contentValues, selection, selectionArgs);
            return true;
        } catch (SQLiteConstraintException e) {
            return false;
        }
    }
}
