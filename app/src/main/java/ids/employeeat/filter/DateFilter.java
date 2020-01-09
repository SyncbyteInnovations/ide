package ids.employeeat.filter;

import android.app.Activity;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import helper.logger.LogUtil;
import helper.utils.java.DateUtil;
import ids.employeeat.R;


public class DateFilter {
    public static void selectYMDRange(final Activity activity, final DateFilterListener listener)
    {
        showFromDate(activity, listener, true, false, false);
    }

    public static void selectYMD(final Activity activity, final DateFilterListener listener)
    {
        showFromDate(activity, listener, false, false, false);
    }

    public static void selectYMD(final Activity activity, final DateFilterListener listener, boolean isToDate)
    {
        showFromDate(activity, listener, false, isToDate, false);
    }

    public static void selectUTCYMD(final Activity activity, final DateFilterListener listener, boolean isToDate)
    {
        showFromDate(activity, listener, false, isToDate, true);
    }

    private static void showFromDate(final Activity activity, final DateFilterListener listener, final boolean showToDate, final boolean isToDate, final boolean isUTC)
    {
        try
        {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dateDialog = DatePickerDialog.newInstance(
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
                        {
                            if (isUTC)
                            {
                                long fromDate = DateUtil.getUTCTimeInMilli(year, monthOfYear, dayOfMonth, 0, 0, 0);
                                if (showToDate)
                                {
                                    showDateToDialog(activity, listener, fromDate, isUTC);
                                }
                                else
                                {
                                    if (isToDate)
                                    {
                                        fromDate = DateUtil.getUTCTimeInMilli(year, monthOfYear, dayOfMonth, 23, 59, 59);
                                        listener.dateFilterInfo(0L, fromDate);
                                    }
                                    else
                                    {
                                        listener.dateFilterInfo(fromDate, 0L);
                                    }
                                }
                            }
                            else
                            {
                                long fromDate = DateUtil.getTimeInMilli(year, monthOfYear, dayOfMonth, 0, 0, 0);
                                if (showToDate)
                                {
                                    showDateToDialog(activity, listener, fromDate, false);
                                }
                                else
                                {
                                    if (isToDate)
                                    {
                                        fromDate = DateUtil.getTimeInMilli(year, monthOfYear, dayOfMonth, 23, 59, 59);
                                        listener.dateFilterInfo(0L, fromDate);
                                    }
                                    else
                                    {
                                        listener.dateFilterInfo(fromDate, 0L);
                                    }
                                }
                            }
                        }
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dateDialog.setTitle(activity.getString(R.string.from_date));
            dateDialog.show(activity.getFragmentManager(), "DatePickerDialog");
        } catch (Exception e)
        {
            LogUtil.logException("DateFilter-->selectYMDRange" + "-->showDateDialog", e);
        }
    }

    private static void showDateToDialog(final Activity activity, final DateFilterListener listener, final long fromDate, final boolean isUTC)
    {
        try
        {
            Calendar now = Calendar.getInstance();
            DatePickerDialog dateDialog = DatePickerDialog.newInstance(
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
                        {
                            long toDate = DateUtil.getTimeInMilli(year, monthOfYear, dayOfMonth, 23, 59, 59);
                            if (isUTC)
                            {
                                toDate = DateUtil.getUTCTimeInMilli(year, monthOfYear, dayOfMonth, 23, 59, 59);
                            }
                            listener.dateFilterInfo(fromDate, toDate);
                        }
                    },
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dateDialog.setTitle(activity.getString(R.string.to_date));
            dateDialog.show(activity.getFragmentManager(), "DatePickerDialog");
        } catch (Exception e)
        {
            LogUtil.logException("DateFilter-->showDateToDialog" + "-->showDateDialog", e);
        }
    }

    public interface DateFilterListener {
        void dateFilterInfo(long fromDate, long toDate);
    }
}
