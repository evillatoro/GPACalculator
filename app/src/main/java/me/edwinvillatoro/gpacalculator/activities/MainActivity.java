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
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.edwinvillatoro.gpacalculator.R;
import me.edwinvillatoro.gpacalculator.adapters.SemesterRecyclerViewAdapter;
import me.edwinvillatoro.gpacalculator.database.DatabaseOpenHelper;
import me.edwinvillatoro.gpacalculator.model.Semester;

public class MainActivity extends AppCompatActivity implements SemesterRecyclerViewAdapter.SemesterCallBack {

    private static final String TAG = "MainActivity";

    public static final String CLICKED_SEMESTER_NAME ="semesterName";

    private DatabaseOpenHelper mDatabaseOpenHelper;
    private RecyclerView mSemesterRecyclerView;
    private List<Semester> mSemesterList;
    private TextView mAddSemesterLabel, mCumulativeGPALabel;
    private SemesterRecyclerViewAdapter mSemesterRecyclerViewAdapter;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);

        mDatabaseOpenHelper = new DatabaseOpenHelper(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddSemesterDialog();
            }
        });

        mAddSemesterLabel = (TextView) findViewById(R.id.text_view_add_semester_message);
        mAddSemesterLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddSemesterDialog();
            }
        });

        mCumulativeGPALabel = (TextView) findViewById(R.id.text_view_cumulative_gpa);

        mSemesterRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_semester);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mSemesterRecyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserPreferences();
        refreshView();
    }

    private void getUserPreferences() {
        // sets all the defaults
        //PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String decimalPlacesPref = sharedPref.getString
                (SettingsActivity.KEY_DECIMAL_PLACES, "2");
    }

    private void getAllSemesters() {
        Log.d(TAG, "getAllSemestersFromDatabase: getting semesters");
        mSemesterList = mDatabaseOpenHelper.getAllSemestersFromDatabase();

        setCumulativeGPALabel();

        mSemesterRecyclerViewAdapter = new SemesterRecyclerViewAdapter(mSemesterList, this);
        mSemesterRecyclerView.setAdapter(mSemesterRecyclerViewAdapter);
        mSemesterRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mSemesterRecyclerViewAdapter.setSemesterCallBack(this);
    }

    private void showAddSemesterDialog() {
        final EditText taskEditText = new EditText(this);
        taskEditText.setHint("Semester Name");
        mAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Add a New Semester")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String semesterName = taskEditText.getText().toString();
                        addSemester(semesterName);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        mAlertDialog.show();
        mAlertDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void showDeleteSemesterDialog(final int p) {
        mAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Delete Semester?")
                .setMessage("Delete " + mSemesterList.get(p).getName() + "?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteSemester(p);
                    }})
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSemesterRecyclerView.getAdapter().notifyItemChanged(p);
                    }
                }).show();
    }

    private void addSemester(String semesterName) {
        if (mDatabaseOpenHelper.addSemesterToDatabase(semesterName)) {
            goToCourseListIntent(semesterName);
        } else {
            toastMessage(semesterName + " semester already exists.");
        }
    }

    private void setCumulativeGPALabel() {
        mCumulativeGPALabel.setText(R.string.cumulative_gpa);
        if (mSemesterList.size() != 0) {
            double cumulativeGPA;
            double cumulativeSemesterCredits = 0.0;
            double cumulativeQualityPoints = 0.0;
            for (Semester semester : mSemesterList) {
                cumulativeQualityPoints += semester.getQualityPoints();
                cumulativeSemesterCredits += semester.getCredits();
            }

            if (cumulativeSemesterCredits != 0) {
                cumulativeGPA = cumulativeQualityPoints / cumulativeSemesterCredits;
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                int numberOfDecimals = Integer.parseInt(sharedPref.getString
                        (SettingsActivity.KEY_DECIMAL_PLACES, "2"));

                cumulativeGPA = Math.round(cumulativeGPA * Math.pow(10, numberOfDecimals)) / Math.pow(10, numberOfDecimals);

                mCumulativeGPALabel.setText("CUMULATIVE GPA: " + cumulativeGPA);
            }
        }
    }

    private void refreshView() {
        getAllSemesters();
        if (mSemesterList.size() == 0) {
            mAddSemesterLabel.setVisibility(View.VISIBLE);
        } else {
            mAddSemesterLabel.setVisibility(View.INVISIBLE);
        }
        mSemesterRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnSemesterClick(int p) {
        Semester clickedSemester = mSemesterList.get(p);
        goToCourseListIntent(clickedSemester.getName());
    }

    @Override
    public void OnSemesterLongClick(final int p) {
        showDeleteSemesterDialog(p);
    }

    private void deleteSemester(int p) {
        String semesterNameToDelete = mSemesterList.get(p).getName();
        toastMessage("Deleted " + semesterNameToDelete);
        mSemesterList.remove(p);
        mDatabaseOpenHelper.deleteSemesterFromDatabase(semesterNameToDelete);
        refreshView();
    }

    private void goToCourseListIntent(String semesterName) {
        Intent intent = new Intent(this, CourseListActivity.class);
        intent.putExtra(CLICKED_SEMESTER_NAME, semesterName);
        startActivity(intent);
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
