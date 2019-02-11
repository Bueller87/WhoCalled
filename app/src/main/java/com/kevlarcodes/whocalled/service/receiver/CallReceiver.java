package com.kevlarcodes.whocalled.service.receiver;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.util.Log;

import com.kevlarcodes.whocalled.service.model.CallLogItem;
import com.kevlarcodes.whocalled.service.repository.CallLogRepository;
import com.kevlarcodes.whocalled.viewmodel.CallLogViewModel;

import java.util.Date;

public class CallReceiver extends PhonecallReceiver {
    private static final String TAG = CallReceiver.class.getSimpleName();

    private void addNewCallToRepo(Context context, String number,
                                       int type, int duration, long date) {
        Log.d(TAG, "addNewCallToRepo: " + number);
        CallLogRepository callRepo = CallLogRepository.getInstance(context);
        CallLogItem newCall = callRepo.buildCallLogItem(null, number, type,
                                                                duration, date);
        callRepo.addCallToLog(newCall);
    }
    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        Log.d(TAG, "onIncomingCallStarted: ");
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Log.d(TAG, "onOutgoingCallStarted: " + number);
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d(TAG, "Override::onIncomingCallEnded: " + number);
        addNewCallToRepo(ctx, number,CallLogRepository.INCOMING,
                (int) (end.getTime() - start.getTime()),
                start.getTime());
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d(TAG, "Override::onOutgoingCallEnded: " + number );
        addNewCallToRepo(ctx, number,CallLogRepository.OUTGOING,
                (int) (end.getTime() - start.getTime()),
                start.getTime());
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Log.d(TAG, "Override::onMissedCall: " + number);
        addNewCallToRepo(ctx, number,CallLogRepository.MISSED,0, start.getTime());
    }

}