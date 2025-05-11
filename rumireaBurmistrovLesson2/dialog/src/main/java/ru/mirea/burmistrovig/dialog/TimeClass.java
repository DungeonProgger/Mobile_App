package ru.mirea.burmistrovig.dialog;

import android.app.TimePickerDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import java.util.Calendar;

public class TimeClass  extends TimePickerDialog {
    private final Calendar initialTime;
    private final OnTimeSetListener externalListener;

    public TimeClass(Context context, Calendar initialTime, OnTimeSetListener listener) {
        super(context, (TimePicker, hourOfDay, minute) -> {
                    if (listener != null) {
                        listener.onTimeSet(TimePicker, hourOfDay, minute);
                        initialTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        initialTime.set(Calendar.MINUTE, minute);
                    }
                },
                initialTime.get(Calendar.HOUR_OF_DAY),
                initialTime.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(context));

        this.initialTime = initialTime;
        this.externalListener = listener;
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
        super.onTimeChanged(timePicker, hourOfDay, minute);
    }
}
