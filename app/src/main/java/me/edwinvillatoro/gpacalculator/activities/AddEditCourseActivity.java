package me.edwinvillatoro.gpacalculator.activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import me.edwinvillatoro.gpacalculator.R;
import me.edwinvillatoro.gpacalculator.model.Grade;

public class AddEditCourseActivity extends AppCompatActivity {

    private ArrayList<Grade> mGradeList;
    private Spinner spinner;
    private SharedPreferences mPreferences;
    private String mSharedPrefFile = "me.edwinvillatoro.gpacalculator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_course);

        Toolbar toolbar = (Toolbar) findViewById(R.id.add_edit_course_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mPreferences = getSharedPreferences(mSharedPrefFile, MODE_PRIVATE);

        spinner = (Spinner) findViewById(R.id.grade_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toastMessage("SELECTED: " + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getGradePreferences();
    }

    private void getGradePreferences() {
        mGradeList = new ArrayList<>();
        mGradeList.add(new Grade("A+", 4.33));
        mGradeList.add(new Grade("A", 4.00));
        mGradeList.add(new Grade("A-", 3.67));
        mGradeList.add(new Grade("B+", 3.33));
        mGradeList.add(new Grade("B", 3.00));
        mGradeList.add(new Grade("B-", 2.67));
        mGradeList.add(new Grade("C+", 2.33));
        mGradeList.add(new Grade("C", 2.00));
        mGradeList.add(new Grade("C-", 1.67));
        mGradeList.add(new Grade("D+", 1.33));
        mGradeList.add(new Grade("D", 1.00));
        mGradeList.add(new Grade("D-", 0.00));
        mGradeList.add(new Grade("F", 0.00));

        ArrayList<String> array = new ArrayList<>();
        array.add("In Progress");

        for (Grade grade: mGradeList) {
            String KEY = grade.getLetter();

            Double storedPointValue = Double.longBitsToDouble(mPreferences.getLong(KEY, Double.doubleToRawLongBits(grade.getPoints())));
            grade.setPoints(storedPointValue);

            String insert = String.format("%-10s %s", grade.getLetter(), grade.getPoints());
            array.add(insert);
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    private void addCourse() {
        //TODO: add course using toolbar
    }

    /**
     * customizable toast message
     * @param message message to display
     */
    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
