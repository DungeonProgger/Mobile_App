package ru.mirea.burmistrovig.dialog;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressClass extends ProgressDialog {

    public ProgressClass(Context context) {
        super(context);
        configureDialog();
    }

    public ProgressClass(Context context, int theme) {
        super(context, theme);
        configureDialog();
    }

    private void configureDialog() {
        setTitle("ProgressDialog");
        setMessage("Loading...");
        setCancelable(false);

    }


    public void showWithSafetyCheck() {
        if (!isShowing()) {
            show();
        }
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            super.dismiss();
        }
    }
}
