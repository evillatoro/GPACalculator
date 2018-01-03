package me.edwinvillatoro.gpacalculator.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.edwinvillatoro.gpacalculator.R;
import me.edwinvillatoro.gpacalculator.model.Course;

/**
 * Created by Edwin Villatoro on 1/3/2018.
 */
public class CourseRecyclerViewAdapter extends RecyclerView.Adapter<CourseRecyclerViewAdapter.CourseCustomHolder> {

    private List<Course> listData;
    private LayoutInflater inflater;
    private Context context;

    private CourseCallBack courseCallBack;

    public void setCourseCallBack(final CourseCallBack courseCallBack) {
        this.courseCallBack = courseCallBack;
    }

    public CourseRecyclerViewAdapter(List<Course> listData, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.listData = listData;
        this.context = context;
    }

    @Override
    public CourseCustomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_course, parent, false);
        return new CourseCustomHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseCustomHolder holder, int position) {
        Course course = listData.get(position);
        holder.courseName.setText(course.getName());
        holder.courseCredits.setText(course.getCredits() + " Credits");
        holder.courseGrade.setText(course.getGrade() + "");
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public interface CourseCallBack {
        void OnCourseClick(int p);
        void OnCourseLongClick (int p);
    }

    class CourseCustomHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView courseName;
        private TextView courseCredits;
        private TextView courseGrade;
        private View container;

        public CourseCustomHolder(View view) {
            super(view);
            courseName = view.findViewById(R.id.text_view_course_name);
            courseCredits = view.findViewById(R.id.text_view_course_credits);
            courseGrade = view.findViewById(R.id.text_view_course_grade);

            container = view.findViewById(R.id.courseContainer);
            container.setOnClickListener(this);
            container.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            toastMessage("Click");
            courseCallBack.OnCourseClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            toastMessage("Long Click");
            courseCallBack.OnCourseLongClick(getAdapterPosition());
            return true;
        }
    }

    /**
     * customizable toast message
     * @param message message to display
     */
    private void toastMessage(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
