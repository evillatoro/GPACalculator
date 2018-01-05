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
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import me.edwinvillatoro.gpacalculator.R;
import me.edwinvillatoro.gpacalculator.adapters.SemesterRecyclerViewAdapter;
import me.edwinvillatoro.gpacalculator.model.Semester;

public class MainActivity extends AppCompatActivity implements SemesterRecyclerViewAdapter.SemesterCallBack {

    public static final String CLICKED_SEMESTER_NAME ="semesterName";
    private RecyclerView mSemesterRecyclerView;
    private ArrayList<Semester> mSemesterList;
    private TextView mAddSemesterLabel;
    private SemesterRecyclerViewAdapter mSemesterRecyclerViewAdapter;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSemester();
            }
        });

        mAddSemesterLabel = (TextView) findViewById(R.id.text_view_add_semester_message);
        mAddSemesterLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSemester();
            }
        });

        mSemesterRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_semester);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mSemesterRecyclerView.setLayoutManager(linearLayoutManager);

        getUserPreferences();

        getSemesters();
        toastMessage("OnCreate Called");
}

    private void getUserPreferences() {
        // sets all the defaults
        //PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String decimalPlacesPref = sharedPref.getString
                (SettingsActivity.KEY_DECIMAL_PLACES, "2");

        toastMessage("Current Decimals: " + decimalPlacesPref);
    }

    private void getSemesters() {
        mSemesterList = new ArrayList<>();
        mSemesterList.add(new Semester("Spring 2018"));
        mSemesterRecyclerViewAdapter = new SemesterRecyclerViewAdapter(mSemesterList, this);
        mSemesterRecyclerView.setAdapter(mSemesterRecyclerViewAdapter);
        mSemesterRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mSemesterRecyclerViewAdapter.setSemesterCallBack(this);
        updateView();
    }

    private void addSemester() {
        final EditText taskEditText = new EditText(this);
        mAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Add a new semester")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String semesterName = taskEditText.getText().toString();
                        mSemesterList.add(new Semester(semesterName));

                        //TODO: Decide whether to go directly to CourseList or stay
                        //updateView();
                        goToCourseListIntent(semesterName);

                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        mAlertDialog.show();
        mAlertDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void updateView() {
        if (mSemesterList.size() == 0) {
            mAddSemesterLabel.setVisibility(View.VISIBLE);
        } else {
            mAddSemesterLabel.setVisibility(View.INVISIBLE);
        }
        mSemesterRecyclerViewAdapter.notifyDataSetChanged();
        toastMessage("Refreshed Semesters");
    }

    private void removeSemester(int p) {
        mSemesterList.remove(p);
        updateView();
    }

    @Override
    public void OnSemesterClick(int p) {
        Semester clickedSemester = mSemesterList.get(p);
        goToCourseListIntent(clickedSemester.getName());
    }

    @Override
    public void OnSemesterLongClick(final int p) {
        mAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Delete Semester")
                .setMessage("Delete " + mSemesterList.get(p).getName() + "?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        toastMessage("Deleted " + mSemesterList.get(p).getName());
                        removeSemester(p);
                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSemesterRecyclerView.getAdapter().notifyItemChanged(p);
                    }
                }).show();
    }

    private void goToCourseListIntent(String semesterName) {
        Intent intent = new Intent(this, CourseListActivity.class);
        intent.putExtra(CLICKED_SEMESTER_NAME, semesterName);
        startActivity(intent);
    }

    /**
     * customizable toast message
     * @param message message to display
     */
    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        getSemesters();
    }
}
