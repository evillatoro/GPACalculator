package me.edwinvillatoro.gpacalculator.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.edwinvillatoro.gpacalculator.R;
import me.edwinvillatoro.gpacalculator.adapters.CourseRecyclerViewAdapter;
import me.edwinvillatoro.gpacalculator.database.DatabaseOpenHelper;
import me.edwinvillatoro.gpacalculator.model.Course;

public class CourseListActivity extends AppCompatActivity implements CourseRecyclerViewAdapter.CourseCallBack {

    private static final String TAG = "CourseListActivity";

    private DatabaseOpenHelper mDatabaseOpenHelper;
    private RecyclerView mCourseRecyclerView;
    private List<Course> mCourseList;
    private TextView mAddCourseLabel, mSemesterGPALabel;
    private CourseRecyclerViewAdapter mCourseRecyclerViewAdapter;
    private AlertDialog mAlertDialog;
    private Toolbar toolbar;
    private String semesterName;
    static final String KEY_SEMESTER_NAME = "semesterName";

    private SharedPreferences mPreferences;
    private String mSharedPrefFile = "me.edwinvillatoro.gpacalculator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        mPreferences = getSharedPreferences(mSharedPrefFile, MODE_PRIVATE);

        toolbar = (Toolbar) findViewById(R.id.course_list_toolbar);
        Bundle bundle = getIntent().getExtras();
        // set activity title to semester name
        if (bundle != null) {
            semesterName = bundle.getString(MainActivity.CLICKED_SEMESTER_NAME);
            SharedPreferences.Editor preferencesEditor = mPreferences.edit();

            preferencesEditor.putString(KEY_SEMESTER_NAME, semesterName);
            preferencesEditor.apply();
        }
        setUpToolBar();

        mDatabaseOpenHelper = new DatabaseOpenHelper(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCourse();
            }
        });

        mAddCourseLabel = (TextView) findViewById(R.id.text_view_add_course_message);
        mAddCourseLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCourse();
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
        semesterName = mPreferences.getString(KEY_SEMESTER_NAME,"");
        setUpToolBar();
        getCourses();
    }

    private void setUpToolBar() {
        toolbar.setTitle(semesterName);
        setSupportActionBar(toolbar);
    }

    private void getCourses() {
        mCourseList = mDatabaseOpenHelper.getCoursesForSemesterFromDatabase(semesterName);

        mSemesterGPALabel.setText(R.string.semester_gpa);

        mCourseRecyclerViewAdapter = new CourseRecyclerViewAdapter(mCourseList, this);
        mCourseRecyclerView.setAdapter(mCourseRecyclerViewAdapter);
        mCourseRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mCourseRecyclerViewAdapter.setCourseCallBack(this);
        refreshView();
    }

    private void addCourse() {
        Intent intent = new Intent(this, AddEditCourseActivity.class);
        intent.putExtra(MainActivity.CLICKED_SEMESTER_NAME, semesterName);
        intent.putExtra("type", 0);
        startActivity(intent);
    }

    private void refreshView() {
        if (mCourseList.size() == 0) {
            mAddCourseLabel.setVisibility(View.VISIBLE);
        } else {
            mAddCourseLabel.setVisibility(View.INVISIBLE);
        }
        mCourseRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void deleteCourse(int p) {
        Course course = mCourseList.get(p);
        toastMessage("Deleted " + course.getName());
        mCourseList.remove(p);
        mDatabaseOpenHelper.deleteCourseFromDatabase(course);
        refreshView();
    }

    @Override
    public void OnCourseClick(int p) {
        Course course = mCourseList.get(p);
        Intent intent = new Intent(this, AddEditCourseActivity.class);
        intent.putExtra(MainActivity.CLICKED_SEMESTER_NAME, semesterName);
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
                .setTitle("Delete Course")
                .setMessage("Delete " + mCourseList.get(p).getName() + "?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteCourse(p);
                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCourseRecyclerView.getAdapter().notifyItemChanged(p);
                    }
                }).show();
    }

    /**
     * customizable toast message
     * @param message message to display
     */
    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
