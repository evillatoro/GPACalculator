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
import me.edwinvillatoro.gpacalculator.model.Semester;

/**
 * Created by Edwin Villatoro on 1/2/2018.
 */
public class SemesterRecyclerViewAdapter extends RecyclerView.Adapter<SemesterRecyclerViewAdapter.SemesterViewHolder>{

    private List<Semester> listData;
    private LayoutInflater inflater;
    private Context context;

    private SemesterCallBack semesterCallBack;

    public void setSemesterCallBack(final SemesterCallBack semesterCallBack) {
        this.semesterCallBack = semesterCallBack;
    }

    public SemesterRecyclerViewAdapter(List<Semester> listData, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.listData = listData;
        this.context = context;
    }

    @Override
    public SemesterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_semester, parent, false);
        return new SemesterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SemesterViewHolder holder, int position) {
        Semester semester = listData.get(position);
        holder.semesterName.setText(semester.getName());
        holder.semesterCredits.setText(semester.getCredits() + " Credits");
        holder.semesterGPA.setText(semester.getGpa() + "");
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public interface SemesterCallBack {
        void OnSemesterClick(int p);
        void OnSemesterLongClick(int p);
    }

    class SemesterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView semesterName;
        private TextView semesterGPA;
        private TextView semesterCredits;
        private View container;

        public SemesterViewHolder(View view) {
            super(view);
            semesterName = view.findViewById(R.id.text_view_semester_name);
            semesterGPA = view.findViewById(R.id.text_view_semester_gpa);
            semesterCredits = view.findViewById(R.id.text_view_semester_credits);

            container = view.findViewById(R.id.semester_container);
            container.setOnClickListener(this);
            container.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            toastMessage("Click");
            semesterCallBack.OnSemesterClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            toastMessage("Long Click");
            semesterCallBack.OnSemesterLongClick(getAdapterPosition());
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
