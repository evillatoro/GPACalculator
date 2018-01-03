package me.edwinvillatoro.gpacalculator.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import me.edwinvillatoro.gpacalculator.R;
import me.edwinvillatoro.gpacalculator.adapters.CourseRecyclerViewAdapter;
import me.edwinvillatoro.gpacalculator.adapters.SemesterRecyclerViewAdapter;
import me.edwinvillatoro.gpacalculator.model.Course;
import me.edwinvillatoro.gpacalculator.model.Semester;

public class CourseListActivity extends AppCompatActivity implements CourseRecyclerViewAdapter.CourseCallBack {

    private RecyclerView mCourseRecyclerView;
    private ArrayList<Course> mCourseList;
    private TextView mAddCourseLabel;
    private CourseRecyclerViewAdapter mCourseRecyclerViewAdapter;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.course_list_toolbar);

        Bundle bundle = getIntent().getExtras();
        // set activity title to semester name
        if (bundle != null) {
            String semesterName = bundle.getString(MainActivity.CLICKED_SEMESTER_NAME);
            toolbar.setTitle(semesterName);
        }
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCourse();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAddCourseLabel = (TextView) findViewById(R.id.text_view_add_course_message);
        mAddCourseLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCourse();
            }
        });

        mCourseRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_course);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mCourseRecyclerView.setLayoutManager(linearLayoutManager);

        getCourses();
    }

    private void getCourses() {
        mCourseList = new ArrayList<>();
        mCourseList.add(new Course("CS 2200"));
        mCourseRecyclerViewAdapter = new CourseRecyclerViewAdapter(mCourseList, this);
        mCourseRecyclerView.setAdapter(mCourseRecyclerViewAdapter);
        mCourseRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mCourseRecyclerViewAdapter.setCourseCallBack(this);
        updateView();
    }

    private void addCourse() {
        final EditText taskEditText = new EditText(this);
        mAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Add a new course")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String courseName = taskEditText.getText().toString();
                        mCourseList.add(new Course(courseName));
                        updateView();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        mAlertDialog.show();
    }

    private void updateView() {
        if (mCourseList.size() == 0) {
            mAddCourseLabel.setVisibility(View.VISIBLE);
        } else {
            mAddCourseLabel.setVisibility(View.INVISIBLE);
        }
        mCourseRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void removeCourse(int p) {
        mCourseList.remove(p);
        updateView();
    }

    @Override
    public void OnCourseClick(int p) {

    }

    @Override
    public void OnCourseLongClick(final int p) {
        mAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Delete Course")
                .setMessage("Delete " + mCourseList.get(p).getName() + "?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        toastMessage("Deleted " + mCourseList.get(p).getName());
                        removeCourse(p);
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
