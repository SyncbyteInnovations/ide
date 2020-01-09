package ids.employeeat.data.repository.menu;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import ids.employeeat.data.database.SyncmeDB;
import ids.employeeat.data.database.menu.MenuDao;
import ids.employeeat.data.database.menu.MenuInfo;
import ids.employeeat.data.database.notfication.NotificationDao;
import ids.employeeat.data.database.notfication.NotificationInfo;

public class NotificationRepository {

    private NotificationDao notificationDao;


    public NotificationRepository(Application application)
    {
        SyncmeDB db = SyncmeDB.getDatabase(application);
        notificationDao = db.notificationDao();
    }


    public void insert(NotificationInfo notificationInfo)
    {
        new insertAsyncTask(notificationDao).execute(notificationInfo);
    }

    private static class insertAsyncTask extends AsyncTask<NotificationInfo, Void, Void> {

        private NotificationDao asyncTaskDao;

        insertAsyncTask(NotificationDao dao)
        {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final NotificationInfo... params)
        {
            asyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
