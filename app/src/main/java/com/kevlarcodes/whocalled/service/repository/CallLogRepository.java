package com.kevlarcodes.whocalled.service.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.kevlarcodes.whocalled.R;
import com.kevlarcodes.whocalled.service.model.CallLogItem;
import java.util.ArrayList;
import java.util.List;

public class CallLogRepository {
    //singleton instance of repository
    private static CallLogRepository callLogRepository;
    private static String mVoiceMailNumber;
    //App context used to query Content Provider
    private final Context mAppContext;
    private static final String TAG = "CallLogRepository";
    private final MutableLiveData<List<CallLogItem>> mMutableLiveCallList = new MutableLiveData<>();
    //Requirement:
    //1. Up to 50 call log item should be displayed
    private static final int MAX_LOG_SIZE = 50;
    //Call item types
    public static final int INCOMING = CallLog.Calls.INCOMING_TYPE;
    public static final int OUTGOING = CallLog.Calls.OUTGOING_TYPE;
    public static final int MISSED = CallLog.Calls.MISSED_TYPE;
    //filter call list constants
    public static final int INCOMING_CALLS = 672;
    public static final int OUTGOING_CALLS = 609;
    public static final int MISSED_CALLS = 874;
    public static final int ALL_CALLS = 814;

    public static final int READ_LOGS = 725;

    private CallLogRepository(final Context context) {
        //ensure app context, no strong refs to activity contexts
        mAppContext = context.getApplicationContext();
        mVoiceMailNumber = getVoiceMailNumber(context);
    }

    public synchronized static CallLogRepository getInstance(Context context) {
        //singleton pattern
        if (callLogRepository == null) {
            callLogRepository = new CallLogRepository(context);
        }
        return callLogRepository;
    }

    private String getVoiceMailNumber(Context context) {
        String telNumberStr = "";
        try {
            TelephonyManager tel = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            telNumberStr = tel.getVoiceMailNumber();
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException:getVoiceMailNumber: " + e.getMessage() );
        } catch (Exception e) {
            Log.e(TAG, "Exception:getVoiceMailNumber: " + e.getMessage() );
        }

        return telNumberStr;
    }

    private String lookupDisplayName(final String phoneNumber) {
        String contactName = null;
        try {
            ContentResolver cr = mAppContext.getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(phoneNumber));
            String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME};
            //TODO: call query off the main thread
            Cursor cursor = cr.query(uri,
                    projection,
                    null,
                    projection,
                    null);
            cursor.moveToFirst();
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "lookupDisplayName Exception: " + e.getMessage());
        }

        return contactName;
    }

    public void addCallToLog(CallLogItem logItem) {
        List<CallLogItem> list = mMutableLiveCallList.getValue();
        if (list != null) {
            if (logItem.getName() == null) {
                Log.d(TAG, "addCallToLog: lookup name");
                logItem.setName(lookupDisplayName(logItem.getNumber()));
            }
            list.add(0,logItem );
        }
        //update mutable live data
        mMutableLiveCallList.setValue(list);
    }

    public LiveData<List<CallLogItem>> getCallLog(int callType) {
        List<CallLogItem> logList = new ArrayList<>();
        String selection;

        switch (callType) {
            case INCOMING_CALLS:
                selection = CallLog.Calls.TYPE + " = " + CallLog.Calls.INCOMING_TYPE;
                break;
            case OUTGOING_CALLS:
                selection = CallLog.Calls.TYPE + " = " + CallLog.Calls.OUTGOING_TYPE;
                break;
            case MISSED_CALLS:
                selection = CallLog.Calls.TYPE + " = " + CallLog.Calls.MISSED_TYPE;
                break;
            default:  //ALL_CALLS
                selection = null;
        }

        try {
            //TODO: narrow down projection
            //TODO: eliminate MAX_LOG_SIZE and call query off the main thread (AsyncTask is simple enough)
            final Cursor cursor = mAppContext.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                    null,
                    selection,
                    null,
                    CallLog.Calls.DATE + " DESC");
            int numberCol = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int typeCol = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int dateCol = cursor.getColumnIndex(CallLog.Calls.DATE);
            int durationCol = cursor.getColumnIndex(CallLog.Calls.DURATION);
            int cachedNameCol = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);

            while (cursor.moveToNext() && logList.size() < MAX_LOG_SIZE) {
                CallLogItem logItem = buildCallLogItem(
                        cursor.getString(cachedNameCol),
                        cursor.getString(numberCol),
                        cursor.getInt(typeCol),
                        cursor.getInt(durationCol),
                        cursor.getLong(dateCol)
                );
                logList.add(logItem);
            }
            cursor.close();
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException:getCallLog: " + e.getMessage() );
        } catch (Exception e) {
            Log.e(TAG, "Exception:getCallLog: " + e.getMessage() );
        }
        mMutableLiveCallList.setValue(logList);
        return mMutableLiveCallList;
    }

    public CallLogItem buildCallLogItem(String name, String number, int type, int duration, long date) {
        CallLogItem logItem = new CallLogItem();
        logItem.setNumber(number);
        if (mVoiceMailNumber.equals(number)) {
            logItem.setVoiceMail(true);
            logItem.setName(mAppContext.getString(R.string.voice_mail_text));
        } else {
            logItem.setVoiceMail(false);
            logItem.setName(name);
        }
        logItem.setType(type);
        logItem.setDuration(duration);
        logItem.setDate(date);
        return logItem;
    }



}
