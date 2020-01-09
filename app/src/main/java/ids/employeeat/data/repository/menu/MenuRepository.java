package ids.employeeat.data.repository.menu;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import ids.employeeat.data.database.SyncmeDB;
import ids.employeeat.data.database.menu.MenuDao;
import ids.employeeat.data.database.menu.MenuInfo;

public class MenuRepository {

    private MenuDao menuDao;


    public MenuRepository(Application application)
    {
        SyncmeDB db = SyncmeDB.getDatabase(application);
        menuDao = db.menuDao();
    }

    public LiveData<List<MenuInfo>> getMenuList(int masterId)
    {
        return menuDao.getMenuList(masterId);
    }

    public void insert(MenuInfo menuInfo)
    {
        new insertAsyncTask(menuDao).execute(menuInfo);
    }

    private static class insertAsyncTask extends AsyncTask<MenuInfo, Void, Void> {

        private MenuDao asyncTaskDao;

        insertAsyncTask(MenuDao dao)
        {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MenuInfo... params)
        {
            asyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
