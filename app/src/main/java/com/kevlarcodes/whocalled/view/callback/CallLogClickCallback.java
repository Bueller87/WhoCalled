package com.kevlarcodes.whocalled.view.callback;

import com.kevlarcodes.whocalled.service.model.CallLogItem;

public interface CallLogClickCallback {
    void onRowClicked(CallLogItem callLogItem);
    void onMakeCallClicked(CallLogItem callLogItem);
}
