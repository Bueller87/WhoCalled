package com.kevlarcodes.whocalled.view.adapter;
import android.databinding.BindingAdapter;
import android.view.View;

public class LoadingBindingAdapter {
    @BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}