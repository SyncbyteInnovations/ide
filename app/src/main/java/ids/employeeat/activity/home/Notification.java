package ids.employeeat.activity.home;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import helper.logger.LogUtil;
import ids.employeeat.JsonTag;
import ids.employeeat.R;
import ids.employeeat.data.database.SyncmeDB;
import ids.employeeat.data.database.notfication.NotificationInfo;
import ids.employeeat.server.ServerComm;
import ids.employeeat.server.ServerConstant;
import ids.employeeat.ui.list.home.notification.NotificationAdapter;

public class Notification implements ServerComm.ServerResponseListener {

    private Context context;
    private NotificationAdapter adapter;


    public Notification(Context context, View view, long personId)
    {
        this.context = context;
        init(view);
        try
        {
            ServerConstant.getServerDataWithPersonId(context, personId, this, ServerConstant.AUTHORISE_MANUAL_LOG, null);
        } catch (Exception e)
        {

        }
    }

    private void init(View view)
    {
        initRecycleView(view);
    }

    private void initRecycleView(View view)
    {
        RecyclerView rvList = view.findViewById(R.id.rv_right_menu);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        this.adapter = new NotificationAdapter(context);
        rvList.setAdapter(adapter);
    }

    @Override
    public void responseSuccess(int processType, JSONArray requestData, JSONArray responseData)
    {
        switch (processType)
        {
            case ServerConstant.AUTHORISE_MANUAL_LOG:
                manualLogNotification(responseData);
        }
    }

    private void manualLogNotification(JSONArray dataArray)
    {
        try
        {
            for (int i = 0; i < dataArray.length(); i++)
            {
                JSONObject requestData = dataArray.getJSONObject(i);
                NotificationInfo notificationInfo = new NotificationInfo();
                notificationInfo.setType(NotificationInfo.CON_NOTIFICATION_TYPE_MANUAL);
                notificationInfo.setMessage(requestData.getString(JsonTag.REASON));
                notificationInfo.setPersonCode(requestData.getLong(JsonTag.PERSON_CODE));
                notificationInfo.setName(requestData.getString(JsonTag.NAME));
                notificationInfo.setFromDate(requestData.getLong(JsonTag.FROM_DATE));
                notificationInfo.setCreatedDate(System.currentTimeMillis());
                showNotification(notificationInfo);
                new InsertNotification().execute(notificationInfo);
            }

            getAdapter().setAdapterList(dataArray);

        } catch (Exception e)
        {
            LogUtil.logException(getClass(), "manualLogNotification", e);
        }
    }

    private class InsertNotification extends AsyncTask<NotificationInfo, Void, Void> {
        @Override
        protected Void doInBackground(NotificationInfo... notificationInfo)
        {
            SyncmeDB.getDatabase(context).notificationDao().insert(notificationInfo[0]);
            return null;
        }
    }

    public void showNotification(NotificationInfo notificationInfo)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "Demo")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationInfo.getName() + " ["+notificationInfo.getPersonCode()+"]")
                .setContentText(notificationInfo.getMessage())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationInfo.getMessage()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(1, builder.build());


    }

    @Override
    public void responseFail(int processType, JSONArray requestData, int statusCode)
    {

    }

    public Context getContext()
    {
        return context;
    }

    public NotificationAdapter getAdapter()
    {
        return adapter;
    }
}
