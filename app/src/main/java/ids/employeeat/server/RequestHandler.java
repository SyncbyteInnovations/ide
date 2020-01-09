package ids.employeeat.server;

import android.content.Context;
import android.util.Base64;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import helper.logger.LogUtil;
import helper.singleTone.volley.VolleySingleTone;
import ids.employeeat.AppPreferences;
import ids.employeeat.JsonTag;
import ids.employeeat.R;

public class RequestHandler {
    protected final String TAG = getClass().getName();

    RequestHandler(Context context, int requestType, int requestMethod, ServerComm.ServerResponseListener listener, JSONArray requestArray)
    {
        processArrayRequestHandler(context, requestType, requestMethod, listener, requestArray);
    }


    private void processArrayRequestHandler(final Context context, int requestType, final int requestMethod, final ServerComm.ServerResponseListener listener, final JSONArray requestArray)
    {
        try
        {
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(requestType, ServerConstant.getUrl(context, requestMethod), requestArray,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray responseData)
                        {
                            if(responseData != null )
                            {
                                if (responseData.length() == 2)
                                {
                                    try
                                    {
                                        JSONArray labelArray = responseData.getJSONArray(0);
                                        JSONArray dataArray = responseData.getJSONArray(1);

                                        JSONArray sortedArray = new JSONArray();
                                        int iLength = labelArray.length();
                                        int jLength = dataArray.length();

                                        for (int jIndex = 0; jIndex < jLength; jIndex++)
                                        {
                                            JSONObject tempObject = new JSONObject();
                                            for (int iIndex = 0; iIndex < iLength; iIndex++)
                                            {
                                                String label = labelArray.getString(iIndex);
                                                tempObject.put(label, dataArray.getJSONArray(jIndex).getString(iIndex));
                                            }
                                            sortedArray.put(tempObject);
                                        }

                                        if (listener != null)
                                        {
                                            JSONObject sortedObject = sortedArray.getJSONObject(0);
                                            if (sortedObject.has(JsonTag.DEFAULT_ERROR) && sortedObject.has(JsonTag.ERROR) && sortedObject.has(JsonTag.UPDATED_ID))
                                            {
                                                Toast.makeText(context, sortedObject.getString(JsonTag.ERROR), Toast.LENGTH_SHORT).show();
                                                listener.responseSuccess(requestMethod, requestArray, null);
                                            }
                                            else
                                            {
                                                listener.responseSuccess(requestMethod, requestArray, sortedArray);
                                            }
                                        }
                                    } catch (Exception e)
                                    {
                                        LogUtil.logException(getClass(), e);
                                    }
                                }
                                else
                                {
                                    listener.responseSuccess(requestMethod, requestArray, responseData);
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError)
                        {
                            LogUtil.logException("", volleyError);
                            NetworkResponse networkResponse = volleyError.networkResponse;
                            if (networkResponse != null)
                            {
                                int statusCode = networkResponse.statusCode;
                                int msg = 0;
                                switch (statusCode)
                                {
                                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                                        msg = R.string.invalid_user_pwd;
                                        break;
                                    case HttpURLConnection.HTTP_NOT_FOUND:
                                        msg = R.string.server_unreachable;
                                        break;
                                    case HttpURLConnection.HTTP_BAD_METHOD:
                                        msg = R.string.method_not_allowed;
                                        break;
                                }
                                if (msg != 0)
                                {
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                }
                                listener.responseFail(requestMethod, requestArray, statusCode);
                            }
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    HashMap<String, String> params = new HashMap<>();
                    AppPreferences preferences = new AppPreferences(context);
                    String credentials = String.format("%s:%s", preferences.getUserName() + "", preferences.getPwd());
                    String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT);
                    params.put("Authorization", auth);
                    return params;
                }
            };
            VolleySingleTone.getInstance(context).

                    addToRequestQueue(context, jsonArrayRequest);
        } catch (Exception e)
        {
            LogUtil.logException(TAG + "-->processArrayRequestHandler", e);
        }
    }


   /* JSONObject getData(String processType, JSONArray dataArray)
    {
        JSONObject jsonObject = null;
        try
        {
            if (dataArray != null && dataArray.length() > 0)
            {
                dataArray = reduceDataToGivenLimit(dataArray);
                jsonObject = new JSONObject();
            }

        }
        catch (Exception e)
        {
            LogUtil.logException(TAG + "-->getData-->onResponse", e);
        }

        return jsonObject;
    }

    private JSONArray reduceDataToGivenLimit(JSONArray dataArray) throws Exception
    {
        if (dataArray.toString().length() > ServerConstant.REQUEST_DATA_SIZE)
        {
            JSONArray reducedDataArray = new JSONArray();
            int length = dataArray.length();
            int size = 0;
            for (int index = 0; index < length; index++)
            {
                JSONObject data = dataArray.getJSONObject(index);
                size = size + data.toString().length();
                if (size > ServerConstant.REQUEST_DATA_SIZE)
                {
                    break;
                }
                reducedDataArray.put(data);
            }
            dataArray = reducedDataArray;
        }
        return dataArray;
    }*/


}
