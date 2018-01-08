package me.edwinvillatoro.gpacalculator.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.edwinvillatoro.gpacalculator.R;
import me.edwinvillatoro.gpacalculator.adapters.CourseRecyclerViewAdapter;
import me.edwinvillatoro.gpacalculator.database.DatabaseOpenHelper;
import me.edwinvillatoro.gpacalculator.model.Course;
import me.edwinvillatoro.gpacalculator.model.Semester;

public class CourseListActivity extends AppCompatActivity implements CourseRecyclerViewAdapter.CourseCallBack {

    private static final String TAG = "CourseListActivity";

    private DatabaseOpenHelper mDatabaseOpenHelper;

    private Toolbar mToolbar;
    private RecyclerView mCourseRecyclerView;
    private CourseRecyclerViewAdapter mCourseRecyclerViewAdapter;
    private TextView mAddCourseLabel, mSemesterGPALabel;
    private AlertDialog mAlertDialog;

    private static final String KEY_SEMESTER_NAME = "semesterName";
    private Semester mSemester;
    private String mSemesterName;
    private List<Course> mCourseList;
    private SharedPreferences mPreferences;
    private String mSharedPrefFile = "me.edwinvillatoro.gpacalculator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        mPreferences = getSharedPreferences(mSharedPrefFile, MODE_PRIVATE);

        mToolbar = (Toolbar) findViewById(R.id.course_list_toolbar);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            mSemesterName = bundle.getString(MainActivity.CLICKED_SEMESTER_NAME);
            SharedPreferences.Editor preferencesEditor = mPreferences.edit();

            preferencesEditor.putString(KEY_SEMESTER_NAME, mSemesterName);
            preferencesEditor.apply();
        }
        
        setUpToolBarAndTitle();

        mDatabaseOpenHelper = new DatabaseOpenHelper(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddCourseActivity();
            }
        });

        mAddCourseLabel = (TextView) findViewById(R.id.text_view_add_course_message);
        mAddCourseLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddCourseActivity();
            }
        });

        mSemesterGPALabel = (TextView) findViewById(R.id.text_view_semester_gpa);

        mCourseRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_course);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mCourseRecyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSemesterName = mPreferences.getString(KEY_SEMESTER_NAME,"");
        setUpToolBarAndTitle();
        refreshView();
    }

    private void setUpToolBarAndTitle() {
        // set activity title to semester name
        mToolbar.setTitle(mSemesterName);
        setSupportActionBar(mToolbar);
    }

    private void getCourses() {
        mSemester = mDatabaseOpenHelper.getCoursesForSemesterFromDatabase(mSemesterName);
        mCourseList = mSemester.getCourseList();

        setSemesterGPALabel();

        mCourseRecyclerViewAdapter = new CourseRecyclerViewAdapter(mCourseList, this);
        mCourseRecyclerView.setAdapter(mCourseRecyclerViewAdapter);
        mCourseRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mCourseRecyclerViewAdapter.setCourseCallBack(this);
    }

    private void setSemesterGPALabel() {
        mSemesterGPALabel.setText(R.string.semester_gpa);
        if (mCourseList.size() != 0) {
            if (mSemester.getCredits() != 0) {
                Double semesterGPA = mSemester.getGpa();

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                int numberOfDecimals = Integer.parseInt(sharedPref.getString
                        (SettingsActivity.KEY_DECIMAL_PLACES, "2"));

                semesterGPA = Math.round(semesterGPA * Math.pow(10, numberOfDecimals)) / Math.pow(10, numberOfDecimals);

                mSemesterGPALabel.setText("SEMESTER GPA: " + semesterGPA);
            }
        }
    }

    private void goToAddCourseActivity() {
        Intent intent = new Intent(this, AddEditCourseActivity.class);
        intent.putExtra(MainActivity.CLICKED_SEMESTER_NAME, mSemesterName);
        intent.putExtra("type", 0);
        startActivity(intent);
    }

    private void refreshView() {
        getCourses();
        if (mCourseList.size() == 0) {
            mAddCourseLabel.setVisibility(View.VISIBLE);
        } else {
            mAddCourseLabel.setVisibility(View.INVISIBLE);
        }
        mCourseRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void deleteCourse(int p) {
        Course course = mCourseList.get(p);
        toastMessage("Deleted " + course.getName() + ".");
        mDatabaseOpenHelper.deleteCourseFromDatabase(course);
        refreshView();
    }

    @Override
    public void OnCourseClick(int p) {
        Course course = mCourseList.get(p);
        Intent intent = new Intent(this, AddEditCourseActivity.class);
        intent.putExtra(MainActivity.CLICKED_SEMESTER_NAME, mSemesterName);
        intent.putExtra("type", 1);
        intent.putExtra("courseName", course.getName());
        intent.putExtra("courseCredits", course.getCredits());
        intent.putExtra("courseGrade", course.getGrade());
        intent.putExtra("coursePredicted", course.getPredicted());
        startActivity(intent);
    }

    @Override
    public void OnCourseLongClick(final int p) {
        mAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Delete Course?")
                .setMessage("Delete " + mCourseList.get(p).getName() + "?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteCourse(p);
                    }})
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCourseRecyclerView.getAdapter().notifyItemChanged(p);
                    }
                }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * customizable toast message
     * @param message message to display
     */
    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
