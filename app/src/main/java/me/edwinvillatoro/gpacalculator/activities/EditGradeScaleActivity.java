package me.edwinvillatoro.gpacalculator.activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import me.edwinvillatoro.gpacalculator.R;
import me.edwinvillatoro.gpacalculator.adapters.GradeScaleRecyclerViewAdapter;
import me.edwinvillatoro.gpacalculator.model.Grade;
import me.edwinvillatoro.gpacalculator.model.Semester;

public class EditGradeScaleActivity extends AppCompatActivity implements GradeScaleRecyclerViewAdapter.GradeCallBack{

    private RecyclerView mGradeScaleRecyclerView;
    private ArrayList<Grade> mGradeList;
    private GradeScaleRecyclerViewAdapter mGradeScaleRecyclerViewAdapter;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_grade_scale);

        mGradeScaleRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_grade_scale);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mGradeScaleRecyclerView.setLayoutManager(linearLayoutManager);

        getGradePreferences();
        getGrades();
    }

    private void getGradePreferences() {
        //TODO: get user grade preferences
    }

    private void getGrades() {
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
        mGradeScaleRecyclerViewAdapter = new GradeScaleRecyclerViewAdapter(mGradeList, this);
        mGradeScaleRecyclerView.setAdapter(mGradeScaleRecyclerViewAdapter);
        mGradeScaleRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mGradeScaleRecyclerViewAdapter.setGradeCallBack(this);
    }

    @Override
    public void OnGradeClick(int p) {
        final Grade gradeClicked = mGradeList.get(p);

        final EditText taskEditText = new EditText(this);
        taskEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        taskEditText.setText(gradeClicked.getPoints() + "");
        taskEditText.setSelectAllOnFocus(true);
        mAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Edit " + gradeClicked.getLetter() + "'s Point Value")
                .setView(taskEditText)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Double newPointValue = Double.parseDouble(taskEditText.getText().toString());
                        gradeClicked.setPoints(newPointValue);
                        updateView();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        mAlertDialog.show();
        mAlertDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void updateView() {
        mGradeScaleRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * customizable toast message
     * @param message message to display
     */
    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
