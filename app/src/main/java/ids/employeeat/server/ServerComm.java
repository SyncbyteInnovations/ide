package ids.employeeat.server;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONObject;

public class ServerComm
{
    public static void  requestPost(Context context, int requestMethod, ServerResponseListener listener, JSONObject requestData)
    {
        JSONArray requestArray = new JSONArray();
        if (requestData != null)
        {
            requestArray.put(requestData);
        }
        new GetServerData().execute(context, Request.Method.POST, requestMethod, listener, requestArray);
    }

    public static void requestGet(Context context, int requestMethod, ServerResponseListener listener)
    {
        JSONArray requestArray = new JSONArray();
        new GetServerData().execute(context, Request.Method.GET, requestMethod, listener, requestArray);
    }

    private static class GetServerData extends AsyncTask<Object, Void, Void>
    {
        @Override
        protected Void doInBackground(Object... params)
        {
            Context context = (Context) params[0];
            int requestType = (int) params[1];
            int requestMethod = (int) params[2];
            ServerResponseListener listener = (ServerResponseListener) params[3];
            JSONArray requestDataArray = (JSONArray) params[4];
            new RequestHandler(context, requestType, requestMethod, listener, requestDataArray);
            return null;
        }
    }

    public interface ServerResponseListener
    {
        void responseSuccess(int processType, JSONArray requestData, JSONArray responseData);

        void responseFail(int processType, JSONArray requestData, int statusCode);
    }
}
