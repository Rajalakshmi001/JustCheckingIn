package edu.rosehulman.scottae.justcheckingin.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import edu.rosehulman.scottae.justcheckingin.R;
import edu.rosehulman.scottae.justcheckingin.models.CheckIn;

public class CheckInListAdapter extends RecyclerView.Adapter<CheckInListAdapter.ViewHolder> {

    private ArrayList<CheckIn> mCheckIns;

    public CheckInListAdapter(Context context) {
        mCheckIns = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            mCheckIns.add(new CheckIn("test", new Date()));
        }
    }

    @NonNull
    @Override
    public CheckInListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_in_row_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckInListAdapter.ViewHolder holder, int position) {
        final CheckIn checkin = mCheckIns.get(position);
        holder.mCommentView.setText(checkin.getComment());
        holder.mDateView.setText(checkin.getDate().toString());
    }

    @Override
    public int getItemCount() {
        return mCheckIns.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mCommentView;
        TextView mDateView;

        public ViewHolder(View itemView) {
            super(itemView);
            mCommentView = itemView.findViewById(R.id.check_in_comment);
            mDateView = itemView.findViewById(R.id.check_in_date_text);
        }
    }
}
