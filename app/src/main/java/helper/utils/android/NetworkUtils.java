package helper.utils.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils
{
    private static final int CON_HTTP_STATUS_SUCCESS = 200;
    private static final int CON_HTTP_STATUS_TIME_OUT = 10 * 1000;

    public static boolean isURLReachable(Context context, String serverUrl)
    {
        boolean valid = false;
        if (isNetworkConnected(context))
        {
            try
            {
                URL url = new URL(serverUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(CON_HTTP_STATUS_TIME_OUT);
                httpURLConnection.connect();
                if (httpURLConnection.getResponseCode() == CON_HTTP_STATUS_SUCCESS)
                {
                    valid = true;
                }
            }
            catch (Exception e)
            {
                valid = false;
            }
            valid = true;
        }
        return valid;
    }

    public static boolean isNetworkConnected(Context context)
    {
        boolean valid = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            valid = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return valid;
    }
}
