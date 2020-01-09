package helper.utils.java;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@SuppressWarnings("all")
public class DateUtil {
    public static final int ONE_DAY = 86400000;
    public static final String NULL = "null";
    public static String LOG_FILE_NAME = "logfile.log";
    public static String DATE_FORMAT_SHORT = "dd/MM/yyyy cccc";
    public static String DATE_FORMAT_DATE_TIME = "dd-M-yyyy HH:mm";
    public static String DATE_FORMAT_DATE = "dd/MM/yyyy";
    public static String DATE_FORMAT_DATE_2 = "EEE, MMMM dd";
    public static String DATE_FORMAT_DAY_MONTH = "dd/MM";
    public static String DATE_FORMAT_TIME = "HH:mm:ss";
    public static String DATE_FORMAT_HOUR_MIN = "HH:mm";
    public static String DATE_FORMAT_FULL = "dd/MM/yyyy HH:mm:ss";
    public static String DATE_FORMAT_FULL_NS = "dd/MM/yyyy HH:mm";
    public static String DATE_FORMAT_DAY_STRING = "EEE";


    public static final String CON_TIMEZONE_DEFAULT = "GMT";

    public static Date valueToDate(String strVal) throws ParseException
    {
        Date dtResultDate = null;
        if (strVal != null && !strVal.equals(""))
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_FULL, Locale.getDefault());
            dtResultDate = simpleDateFormat.parse(strVal);
        }
        return dtResultDate;
    }

    public static long getCurrentTime()
    {
        return System.currentTimeMillis();
    }

    public static String DateToValue(Object dateObject)
    {
        return getDate((Date) dateObject, DATE_FORMAT_FULL);
    }

    public static long getUTCTimeInMilli(int year, int month, int day, int hour, int min, int sec)
    {
        Calendar calendar = getUTCCalendar();
        calendar.set(year, month, day, hour, min, sec);
        return calendar.getTimeInMillis();
    }

    public static long getTimeInMilli(int year, int month, int day, int hour, int min, int sec)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, min, sec);
        return calendar.getTimeInMillis();
    }


    public static Calendar getUTCCalendar()
    {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_FULL, Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone(CON_TIMEZONE_DEFAULT));
        dateFormat.format(new Date(System.currentTimeMillis()));
        return dateFormat.getCalendar();
    }

    public static long getStartOfDay(long date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getEndOfDay(long date)
    {
        Calendar calendar  = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }


    public static long getUTCStartOfDay(long date)
    {
        Calendar calendar = getUTCCalendar();
        calendar.setTimeInMillis(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getUTCEndOfDay(long date)
    {
        Calendar calendar = getUTCCalendar();
        calendar.setTimeInMillis(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    public static long getTimeInMilli(int hour, int min, int sec)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1970, 0, 1, hour, min, sec);
        return calendar.getTimeInMillis();
    }

    public static int getYear()
    {
        return Integer.parseInt(getDate(new Date(), "yyyy"));
    }

    public static int getMonth()
    {
        return Integer.parseInt(getDate(new Date(), "MM")) - 1;
    }

    public static int getMonth(long time)
    {
        return Integer.parseInt(getDate(new Date(time), "MM")) - 1;
    }

    public static int getDay()
    {
        return Integer.parseInt(getDate(new Date(), "dd"));
    }

    public static int getHour()
    {
        return Integer.parseInt(getDate(new Date(), "HH"));
    }

    public static int getMinute()
    {
        return Integer.parseInt(getDate(new Date(), "mm"));
    }

    public static String getDate(long lTime)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lTime);
        return getDate(calendar.getTime(), DATE_FORMAT_DATE);
    }

    public static String getTime(long lTime)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lTime);
        return getDate(calendar.getTime(), DATE_FORMAT_TIME);
    }

    public static String getTime(long lTime, String strFormat)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lTime);
        return getDate(calendar.getTime(), strFormat);
    }

    public static String getDate(Date date, String strDateFormat)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(strDateFormat, Locale.getDefault());
        return dateFormat.format(date);
    }

    public static String getDate(long time, String strDateFormat)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(strDateFormat, Locale.getDefault());
        return dateFormat.format(new Date(time));
    }

    public static String getUTCDate(long time, String strDateFormat)
    {
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat, Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone(CON_TIMEZONE_DEFAULT));
        return dateFormat.format(new Date(time));
    }


    public static boolean compareDates(String startDate, String endDate, String dateFormatDayMonth) throws Exception
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatDayMonth, Locale.getDefault());
        return simpleDateFormat.parse(startDate).after(simpleDateFormat.parse(endDate));
    }

    public static boolean compareTimeWithCurrent(String strFrom) throws Exception
    {
        String currentTime = getTime(System.currentTimeMillis() + 60000, DATE_FORMAT_HOUR_MIN);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_HOUR_MIN, Locale.getDefault());
        return simpleDateFormat.parse(currentTime).after(simpleDateFormat.parse(strFrom));
    }

    public static boolean compareTimeWithCurrent(String strFrom, String strTo) throws Exception
    {
        String strCurrentTime = getTime(System.currentTimeMillis() + 60000, DATE_FORMAT_HOUR_MIN);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_HOUR_MIN, Locale.getDefault());
        return simpleDateFormat.parse(strCurrentTime).after(simpleDateFormat.parse(strFrom)) && simpleDateFormat.parse(strCurrentTime).before(simpleDateFormat.parse(strTo));
    }

    public static boolean compareTime(String strFrom, String strTo, String strTimeToCompare) throws Exception
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_HOUR_MIN, Locale.getDefault());
        return simpleDateFormat.parse(strTimeToCompare).after(simpleDateFormat.parse(strFrom)) && simpleDateFormat.parse(strTimeToCompare).before(simpleDateFormat.parse(strTo));
    }

    public static long getTimeInMilli(String strDate, String strFormat) throws ParseException
    {
        SimpleDateFormat oFormat = new SimpleDateFormat(strFormat, Locale.getDefault());
        Date date = oFormat.parse(strDate);
        return date.getTime();
    }

    public static long getTimeInMilli(long fromDate, long fromTime) throws ParseException
    {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(fromDate);

        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(fromTime);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(fromDate);
        calendar.set(date.get(date.YEAR),date.get(date.MONTH),date.get(date.DAY_OF_MONTH),time.get(time.HOUR_OF_DAY),time.get(time.MINUTE), time.get(time.SECOND));
        return calendar.getTimeInMillis();
    }

    public static long getUTCTimeInMilli(long startTime)
    {
        return startTime + (new Date().getTimezoneOffset() * 1000 * 60);
    }


    public static long getDateFromMY(int requestMonth, int requestYear)
    {
        Calendar calendar = getUTCCalendar();
        calendar.setTimeInMillis(getUTCStartOfDay(System.currentTimeMillis()));
        calendar.set(requestYear, requestMonth, getDay());
        return calendar.getTimeInMillis();
    }

    /**
     * Time Zone offset is zero and month start from zero
     */
    public static long getDateFromMYMonthEnd(int requestMonth, int requestYear)
    {
        Calendar calendar = getUTCCalendar();
        calendar.setTimeInMillis(getUTCStartOfDay(System.currentTimeMillis()));
        //called twice to set day of month of selected date
        calendar.set(requestYear, requestMonth, 1);
        calendar.set(requestYear, requestMonth, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        return calendar.getTimeInMillis();
    }

    /**
     * Time Zone offset is zero and month start from zero
     */
    public static long getDateFromMYMonthStart(int requestMonth, int requestYear)
    {
        Calendar calendar = getUTCCalendar();
        calendar.setTimeInMillis(getUTCStartOfDay(System.currentTimeMillis()));
        //called twice to set day of month of selected date
        calendar.set(requestYear, requestMonth, 1);
        return calendar.getTimeInMillis();
    }
}
