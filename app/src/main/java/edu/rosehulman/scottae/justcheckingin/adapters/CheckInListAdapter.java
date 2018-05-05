package edu.rosehulman.scottae.justcheckingin.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import edu.rosehulman.scottae.justcheckingin.R;
import edu.rosehulman.scottae.justcheckingin.models.CheckIn;

public class CheckInListAdapter extends RecyclerView.Adapter<CheckInListAdapter.ViewHolder> {

    private ArrayList<CheckIn> mCheckIns;
    private Context mContext;

    public CheckInListAdapter(Context context) {
        mContext = context;
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
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final CharSequence[] items = mContext.getResources().getStringArray(R.array.event_context_menu);

                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    // TODO: replace with actual actions
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            switch (item) {
                                case 0:
                                    Toast.makeText(builder.getContext(), "edit", Toast.LENGTH_SHORT).show();
                                    return;
                                case 1:
                                    Toast.makeText(builder.getContext(), "delete", Toast.LENGTH_SHORT).show();
                                    return;
                                default:
                                    Toast.makeText(builder.getContext(), "cancelled", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.show();
                    return true;
                }
            });
        }
    }
}
