package ru.mirea.burmistrovig.dialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.sql.Date;
import java.util.Calendar;

public class DateClass extends DatePickerDialog {
    private final Calendar initialDate;

    public DateClass(Context context, Calendar initialDate, OnDateSetListener listener) {
        super(context, (datePicker, year, month, day) -> {
                    if (listener != null) {
                        listener.onDateSet(datePicker, year, month, day);
                        initialDate.set(Calendar.YEAR, year);
                        initialDate.set(Calendar.MONTH, month);
                        initialDate.set(Calendar.DAY_OF_MONTH, day);
                    }
                },
                initialDate.get(Calendar.YEAR),
                initialDate.get(Calendar.MONTH),
                initialDate.get(Calendar.DAY_OF_MONTH));

        this.initialDate = initialDate;
    }
}
