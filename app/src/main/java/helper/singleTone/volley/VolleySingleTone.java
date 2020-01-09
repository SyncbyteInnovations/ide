package helper.singleTone.volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleTone
{
    private static VolleySingleTone instance;
    private RequestQueue requestQueue;

    private VolleySingleTone(Context context)
    {
        requestQueue = getRequestQueue(context);
    }

    public static synchronized VolleySingleTone getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new VolleySingleTone(context);
        }
        return instance;
    }

    private RequestQueue getRequestQueue(Context context)
    {
        if (requestQueue == null)
        {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Context context, Request<T> request)
    {
        getRequestQueue(context).add(request);
    }
}
