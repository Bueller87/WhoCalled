package com.kevlarcodes.whocalled.service.receiver;

import java.util.Date;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public abstract class PhonecallReceiver extends BroadcastReceiver {
    private static final String TAG = CallReceiver.class.getSimpleName();
    //The receiver will be recreated whenever android feels like it.
    // We need a static variable to remember data between instantiations
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;  //because the passed incoming is only valid in ringing

    private void setSavedNumber(String newStr) {
        if (newStr != null && newStr.length() > 0) {
            savedNumber = newStr;
        }
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        //TODO: Test Broadcast Receiver with more flavors of Android.
        //Tested with API 19, 24, 27,28
        //Only API 19 receives NEW_OUTGOING_CALL intent
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            try {
                Log.d(TAG, "android.intent.action.NEW_OUTGOING_CALL");
                String number = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
                setSavedNumber(number);
                Log.d(TAG, "onReceive:NEW_OUTGOING_CALL " + savedNumber);
            } catch (Exception e) {
                Log.d(TAG, "onReceive:NEW_OUTGOING_CALL:Exception ");
                e.printStackTrace();
            }
        }
        else{
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            setSavedNumber(number);
            Log.d(TAG,String.format("onReceive:else: stateStr:%s, number:%s", stateStr, number));
            int state = 0;
            if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                state = TelephonyManager.CALL_STATE_IDLE;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                state = TelephonyManager.CALL_STATE_RINGING;
            }


            onCallStateChanged(context, state, savedNumber);
        }
    }

    //Derived classes should override these to respond to specific events of interest
    protected void onIncomingCallStarted(Context ctx, String number, Date start){}
    protected void onOutgoingCallStarted(Context ctx, String number, Date start){}
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end){}
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end){}
    protected void onMissedCall(Context ctx, String number, Date start){}

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    private void onCallStateChanged(Context context, int state, String number) {
        if(lastState == state){
            //No change, debounce extras
            Log.d(TAG, "onCallStateChanged: No change, debounce extras");
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                //savedNumber = number;
                onIncomingCallStarted(context, savedNumber, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false;
                    callStartTime = new Date();
                    onOutgoingCallStarted(context, savedNumber, callStartTime);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    //Ring but no pickup-  a miss

                    onMissedCall(context, savedNumber, callStartTime);
                }
                else if(isIncoming){
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                else{
                    onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                break;
        }
        lastState = state;
    }
}