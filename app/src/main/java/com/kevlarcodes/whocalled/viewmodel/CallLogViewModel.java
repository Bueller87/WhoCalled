package com.kevlarcodes.whocalled.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.kevlarcodes.whocalled.service.model.CallLogItem;
import com.kevlarcodes.whocalled.service.repository.CallLogRepository;

import java.util.List;

public class CallLogViewModel extends AndroidViewModel {
    private final LiveData<List<CallLogItem>> callListObservable;

    public CallLogViewModel(Application app) {
        super(app);
        callListObservable = CallLogRepository.getInstance(app.getApplicationContext())
                                .getCallLog(CallLogRepository.ALL_CALLS);
    }

    public LiveData<List<CallLogItem>> getCallListObservable() {
        return callListObservable;
    }


}
