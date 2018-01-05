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
import me.edwinvillatoro.gpacalculator.model.Grade;

/**
 * Created by Edwin Villatoro on 1/4/2018.
 */
public class GradeScaleRecyclerViewAdapter extends RecyclerView.Adapter<GradeScaleRecyclerViewAdapter.GradeViewHolder> {

    private List<Grade> listData;
    private LayoutInflater inflater;
    private Context context;

    private GradeCallBack gradeCallBack;

    public void setGradeCallBack(GradeCallBack gradeCallBack) {
        this.gradeCallBack = gradeCallBack;
    }

    public GradeScaleRecyclerViewAdapter(List<Grade> listData, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.listData = listData;
        this.context = context;
    }

    @Override
    public GradeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_letter_grade, parent, false);
        return new GradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GradeViewHolder holder, int position) {
        Grade grade = listData.get(position);
        holder.gradeLetter.setText(grade.getLetter());
        holder.gradePoints.setText(grade.getPoints() + "");
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public interface GradeCallBack {
        void OnGradeClick(int p);
    }

    class GradeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView gradeLetter;
        private TextView gradePoints;
        private View container;

        public GradeViewHolder(View itemView) {
            super(itemView);
            gradeLetter = itemView.findViewById(R.id.text_view_grade_letter);
            gradePoints = itemView.findViewById(R.id.text_view_grade_points);

            container = itemView.findViewById(R.id.grade_container);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            toastMessage("Click");
            gradeCallBack.OnGradeClick(getAdapterPosition());
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
