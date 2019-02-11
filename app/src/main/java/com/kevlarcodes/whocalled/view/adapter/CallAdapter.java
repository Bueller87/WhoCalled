package com.kevlarcodes.whocalled.view.adapter;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.kevlarcodes.whocalled.R;
import com.kevlarcodes.whocalled.service.model.CallLogItem;
import com.kevlarcodes.whocalled.service.repository.CallLogRepository;
import com.kevlarcodes.whocalled.view.callback.CallLogClickCallback;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.CallViewHolder> {
    private final List<CallLogItem> mCallList= new ArrayList<>();
    private static final DateFormat sTimeInstance = DateFormat.getTimeInstance(DateFormat.SHORT,
                                        Resources.getSystem().getConfiguration().locale);
    private static final DateFormat sDateInstance = DateFormat.getDateInstance(DateFormat.SHORT,
                                        Resources.getSystem().getConfiguration().locale);
    @Nullable
    private final CallLogClickCallback callClickCallback;

    public CallAdapter(@Nullable CallLogClickCallback callClickCallback) {
        this.callClickCallback = callClickCallback;
    }

    public void setCallList(List<CallLogItem> newList) {
        mCallList.clear();
        mCallList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CallAdapter.CallViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.list_item_call,viewGroup,false );
        return new CallAdapter.CallViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CallAdapter.CallViewHolder holder, int i) {
        holder.bind(mCallList.get(i));
    }

    @Override
    public int getItemCount() {
        return mCallList.size();
    }

    public class CallViewHolder extends RecyclerView.ViewHolder {
       @BindView(R.id.call_contact)
       TextView contactTextView;
       @BindView(R.id.call_date)
       TextView dateTextView;
       @BindView(R.id.call_type)
       ImageView callTypeImageView;
       @BindView(R.id.call_button)
       ImageView callButton;

       final View mItemView;

        public CallViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mItemView = itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callClickCallback != null) {
                        callClickCallback.onRowClicked(mCallList.get(getAdapterPosition()));
                    }
                }
            });

            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callClickCallback != null) {
                        callClickCallback.onMakeCallClicked(mCallList.get(getAdapterPosition()));
                    }
                }
            });
        }

        public void bind(CallLogItem item) {
            contactTextView.setText(item.getDisplayName());
            Date callDate = new Date(item.getDate());
            String dateTimeStr = String.format("%s - %s",
                                    sDateInstance.format(callDate),
                                    sTimeInstance.format(callDate));
            dateTextView.setText(dateTimeStr);

            switch (item.getType()) {
                case CallLogRepository.INCOMING:
                    callTypeImageView.setImageResource(R.drawable.received);
                    callTypeImageView.setContentDescription(mItemView.getResources().
                                                    getString(R.string.incoming_call_cd));
                    break;
                case CallLogRepository.OUTGOING:
                    callTypeImageView.setImageResource(R.drawable.sent);
                    callTypeImageView.setContentDescription(mItemView.getResources().
                                                    getString(R.string.outgoing_call_cd));
                    break;
                case CallLogRepository.MISSED:
                    callTypeImageView.setImageResource(R.drawable.missed);
                    callTypeImageView.setContentDescription(mItemView.getResources().
                                                    getString(R.string.missed_call_cd));
                    break;
                default:
                    callTypeImageView.setImageResource(R.drawable.cancelled);
                    callTypeImageView.setContentDescription(mItemView.getResources()
                                                    .getString(R.string.cancelled_call_cd));
                    break;
            }

        }
    }
}
