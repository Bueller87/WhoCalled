package com.kevlarcodes.whocalled.service.model;


import android.content.res.Resources;
import android.telephony.PhoneNumberUtils;

import com.kevlarcodes.whocalled.R;

import java.util.Locale;

public class CallLogItem {
    private String number;
    private long date;
    private int duration;
    private int type;
    private String name;
    private boolean isVM;

    public CallLogItem() {  }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) { this.number = number; }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public boolean isVoiceMail() {
        return isVM;
    }

    public void setVoiceMail(boolean VM) {
        isVM = VM;
    }


    public String getDisplayName(){
         if (getName() != null) {
            return getName();
        } else {
            return getFormatedNumber();
        }
    }

    public String getFormatedNumber() {
        String str = "";
        if (getNumber() != null) {
            str = PhoneNumberUtils.formatNumber(getNumber());
        }
        return str;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),"name:%s, number:%s, duration:%d, date:%d, type:%d",
                        getDisplayName(),
                        getNumber(),
                        getDuration(),
                        getDate(),
                        getType());
    }


}
