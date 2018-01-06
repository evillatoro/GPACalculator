package me.edwinvillatoro.gpacalculator.activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import me.edwinvillatoro.gpacalculator.R;
import me.edwinvillatoro.gpacalculator.database.DatabaseOpenHelper;
import me.edwinvillatoro.gpacalculator.model.Course;
import me.edwinvillatoro.gpacalculator.model.Grade;

public class AddEditCourseActivity extends AppCompatActivity {

    private DatabaseOpenHelper mDatabaseOpenHelper;
    private ArrayList<Grade> mGradeList;
    private Spinner mGradeSpinner;
    private SharedPreferences mPreferences;
    private EditText mCourseNameEditText;
    private EditText mCourseCreditsEditText;
    private String semester;
    private RadioGroup mRadioGroup;
    private String mSharedPrefFile = "me.edwinvillatoro.gpacalculator";
    private Bundle mBundle;
    private ArrayAdapter<String> mAdapter;
    private int mType;
    private String mOldCourseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_course);

        mDatabaseOpenHelper = new DatabaseOpenHelper(this);

        mPreferences = getSharedPreferences(mSharedPrefFile, MODE_PRIVATE);

        mCourseNameEditText = (EditText) findViewById(R.id.edit_text_course_name);
        mCourseCreditsEditText = (EditText) findViewById(R.id.edit_text_course_credits);

        mGradeSpinner = (Spinner) findViewById(R.id.grade_spinner);

        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group_predicted);

        /**
         * Set pointer to end of text in edittext when user clicks Next on KeyBoard.
         */
        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ((EditText) view).setSelection(((EditText) view).getText().length());
                }
            }
        };

        mCourseNameEditText.setOnFocusChangeListener(onFocusChangeListener);
        mCourseCreditsEditText.setOnFocusChangeListener(onFocusChangeListener);

        getGradePreferences();

        Toolbar toolbar = (Toolbar) findViewById(R.id.add_edit_course_toolbar);
        mBundle = getIntent().getExtras();
        // set activity title to course name
        if (mBundle != null) {
            semester = mBundle.getString(MainActivity.CLICKED_SEMESTER_NAME);
            mType = mBundle.getInt("type");
            if (mType == 1) {
                toolbar.setTitle("Edit Course");
                addCourseDetails();
            } else {
                toolbar.setTitle("Add Course");
                showKeyboardOnStartUp();
            }
        }

        //TODO: change activity title to course name if there is one
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void addCourseDetails() {
        String courseName = mBundle.getString("courseName");
        Double courseGrade = mBundle.getDouble("courseGrade");
        Double courseCredits = mBundle.getDouble("courseCredits");
        int coursePredicted = mBundle.getInt("coursePredicted");

        //TODO: find way to set the course grade on the spinner

        int index = 0;
        for (int i = 0; i < mGradeList.size(); i++) {
            if (mGradeList.get(i).getPoints() == courseGrade) {
                index = i + 1;
            }
        }
        toastMessage("course grade "+ courseGrade);

        mOldCourseName = courseName;
        //toastMessage("position " + index);
        mGradeSpinner.setSelection(index);

        mCourseNameEditText.setText(courseName);
        mCourseCreditsEditText.setText(courseCredits + "");
        if (coursePredicted == 0) {
            ((RadioButton)mRadioGroup.getChildAt(2)).setChecked(true);
        } else {
            ((RadioButton)mRadioGroup.getChildAt(1)).setChecked(true);
        }
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

        // Create an ArrayAdapter using the string array and a default mGradeSpinner layout
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array);
        // Specify the layout to use when the list of choices appears
        mAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        // Apply the adapter to the mGradeSpinner
        mGradeSpinner.setAdapter(mAdapter);
    }

    private void addCourse() {
        String name = mCourseNameEditText.getText().toString();
        String semester = this.semester;
        try {
            if (name.equals("")) {
                throw new IllegalArgumentException();
            }
            double credits = Double.parseDouble(mCourseCreditsEditText.getText().toString());
            double grade = getGrade();
            int predicted = getPredicted();

            Course course = new Course(name, semester, credits, grade, predicted);
            if (mDatabaseOpenHelper.addCourseToDatabase(course)) {
                toastMessage(name + " added.");
                finish();
            } else {
                toastMessage("Course already exist in this semester.");
            }
        } catch (NumberFormatException e) {
            toastMessage("Please enter all fields.");
        } catch (IllegalArgumentException e) {
            toastMessage("Please enter a name.");
        }
    }

    private void editCourse() {
        String name = mCourseNameEditText.getText().toString();
        String semester = this.semester;
        try {
            if (name.equals("")) {
                throw new IllegalArgumentException();
            }
            double credits = Double.parseDouble(mCourseCreditsEditText.getText().toString());
            double grade = getGrade();
            int predicted = getPredicted();

            Course course = new Course(name, semester, credits, grade, predicted);
            if (mDatabaseOpenHelper.updateCourseInDatabase(course, mOldCourseName)) {
                toastMessage(name + " edit.");
                finish();
            } else {
                toastMessage("Course already exist in this semester.");
            }
        } catch (NumberFormatException e) {
            toastMessage("Please enter all fields.");
        } catch (IllegalArgumentException e) {
            toastMessage("Please enter a name.");
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    private double getGrade() {
        int position = mGradeSpinner.getSelectedItemPosition();
        if (position - 1 >= 0) {
            return mGradeList.get(position - 1).getPoints();
        } else {
            return -1;
        }
    }

    private int getPredicted() {
        int checkedRadioButtonId = mRadioGroup.getCheckedRadioButtonId();

        if (checkedRadioButtonId == R.id.yesRadioButton) {
            return 1;
        } else {
            return 0;
        }
    }

    private void showKeyboardOnStartUp() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            if (mType == 1) {
                editCourse();
            } else {
                addCourse();
            }
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
