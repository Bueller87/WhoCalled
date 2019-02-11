package com.kevlarcodes.whocalled.view.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.kevlarcodes.whocalled.R;
import com.kevlarcodes.whocalled.databinding.FragmentCallLogBinding;
import com.kevlarcodes.whocalled.service.model.CallLogItem;
import com.kevlarcodes.whocalled.view.adapter.CallAdapter;
import com.kevlarcodes.whocalled.view.callback.CallLogClickCallback;
import com.kevlarcodes.whocalled.viewmodel.CallLogViewModel;
import java.util.List;

/**
 * Purpose: Fragment to host RecyclerView showing Call Log History on this phone
 */
public class CallLogFragment extends Fragment implements CallLogClickCallback {
    public static final String TAG = CallLogFragment.class.getSimpleName();
    private FragmentCallLogBinding mCallLogBinding;
    private CallAdapter mCallAdapter;
    public CallLogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mCallLogBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_call_log,
                container,
                false);
        mCallAdapter = new CallAdapter(this);

        mCallLogBinding.callList.setAdapter(mCallAdapter);
        mCallLogBinding.setIsLoading(true);
        return mCallLogBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final CallLogViewModel viewModel =
                ViewModelProviders.of(this).get(CallLogViewModel.class);
        observeViewModel(viewModel);
    }

    private void observeViewModel(CallLogViewModel viewModel) {
        // Update the list when the data changes
        viewModel.getCallListObservable().observe(this, new Observer<List<CallLogItem>>() {
            @Override
            public void onChanged(@Nullable List<CallLogItem> callList) {
                if (callList != null) {
                    mCallLogBinding.setIsLoading(false);
                    int position = 0;
                    LinearLayoutManager llm = (LinearLayoutManager) mCallLogBinding.callList.getLayoutManager();
                    if (llm != null){
                        position = llm.findFirstVisibleItemPosition();
                        if (position < 0) position = 0;
                    }
                    mCallAdapter.setCallList(callList);
                    mCallLogBinding.callList.scrollToPosition(position);
                }
            }
        });
    }

    @Override
    public void onRowClicked(CallLogItem callLogItem) {
        showToast("Todo: Call Details View for: " + callLogItem.getDisplayName());
    }

    @Override
    public void onMakeCallClicked(CallLogItem callLogItem) {
        //showToast("onMakeCallClicked: " + callLogItem.getNumber());
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + callLogItem.getNumber()));
        startActivity(intent);
    }

    private void showToast(String msg) {
        Toast toast = Toast.makeText(this.getActivity(),
                msg,
                Toast.LENGTH_SHORT);
        toast.show();
    }
}
