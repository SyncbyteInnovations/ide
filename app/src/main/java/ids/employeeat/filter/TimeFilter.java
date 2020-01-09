package ids.employeeat.filter;


import android.app.Activity;

import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import helper.logger.LogUtil;
import helper.utils.java.DateUtil;


public class TimeFilter {
    public static void selectTimeRange(final Activity activity, final TimeFilterListener listener)
    {
        showFromTime(activity, listener, true, false);
    }

    public static void selectTime(final Activity activity, final TimeFilterListener listener)
    {
        showFromTime(activity, listener, false, false);
    }

    public static void selectTime(final Activity activity, final TimeFilterListener listener, boolean isToTime)
    {
        showFromTime(activity, listener, false, isToTime);
    }

    private static void showFromTime(final Activity activity, final TimeFilterListener listener, final boolean showToTime, final boolean isToTime)
    {
        Calendar now = Calendar.getInstance();
        TimePickerDialog timeDialog = TimePickerDialog.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second)
                    {
                        try
                        {
                            long fromTime = DateUtil.getTimeInMilli(hourOfDay, minute, 0);
                            if (showToTime)
                            {
                                showToTime(activity, listener, fromTime);
                            }
                            else
                            {
                                if (isToTime)
                                {

                                    listener.timeFilterInfo(0L, fromTime);
                                }
                                else
                                {
                                    listener.timeFilterInfo(fromTime, 0L);
                                }
                            }

                        } catch (Exception oException)
                        {
                            LogUtil.logException(getClass().getName() + "-->showTimerDialog", oException);
                        }
                    }
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        timeDialog.show(activity.getFragmentManager(), "Timepickerdialog");
    }

    private static void showToTime(final Activity activity, final TimeFilterListener listener, final long fromTime)
    {
        Calendar now = Calendar.getInstance();
        TimePickerDialog timeDialog = TimePickerDialog.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second)
                    {
                        try
                        {
                            long toTime = DateUtil.getTimeInMilli(hourOfDay, minute, 0);
                            listener.timeFilterInfo(fromTime, toTime);
                        } catch (Exception e)
                        {
                            LogUtil.logException(getClass().getName() + "-->showTimerDialog", e);
                        }
                    }
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        timeDialog.show(activity.getFragmentManager(), "Timepickerdialog");
    }

    public interface TimeFilterListener {
        void timeFilterInfo(long fromTime, long toTime);
    }
}
