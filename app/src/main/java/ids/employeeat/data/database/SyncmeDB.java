package ids.employeeat.data.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import ids.employeeat.data.database.menu.MenuDao;
import ids.employeeat.data.database.menu.MenuInfo;
import ids.employeeat.data.database.notfication.NotificationDao;
import ids.employeeat.data.database.notfication.NotificationInfo;

@Database(entities = {MenuInfo.class, NotificationInfo.class}, version = 1,exportSchema = false)
public abstract class SyncmeDB extends RoomDatabase {

    private static volatile SyncmeDB INSTANCE;

    public abstract MenuDao menuDao();

    public abstract NotificationDao notificationDao();

    public SyncmeDB()
    {

    }


    public static SyncmeDB getDatabase(final Context context)
    {
        if (INSTANCE == null)
        {
            synchronized (SyncmeDB.class)
            {
                if (INSTANCE == null)
                {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SyncmeDB.class, "synctime_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {

                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db)
                {
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };


    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final MenuDao menuDao;

        PopulateDbAsync(SyncmeDB db)
        {
            menuDao = db.menuDao();
        }

        @Override
        protected Void doInBackground(final Void... params)
        {
            if (menuDao.getCount() == 0)
            {
                menuDao.insert(new MenuInfo(101, "Dashboard", 0, "Dashboard", 1, 3));
                menuDao.insert(new MenuInfo(102, "Leaves", 0, "Leaves", 3, 3));
                menuDao.insert(new MenuInfo(103, "Holiday", 0, "Holiday", 4, 3));
                menuDao.insert(new MenuInfo(104, "Attendance", 0, "Attendance", 5, 3));
                menuDao.insert(new MenuInfo(105, "Team", 0, "Team", 5, 3));
                menuDao.insert(new MenuInfo(106, "Device", 0, "Device Management", 6, 3));
                menuDao.insert(new MenuInfo(107, "UserManage", 0, "User Management", 7, 3));

                menuDao.insert(new MenuInfo(102101, "LeaveEntry", 0, "Apply Leave", 3, 102));
                menuDao.insert(new MenuInfo(102102, "CompOffEntry", 0, "Apply Comp Off", 3, 102));
                menuDao.insert(new MenuInfo(102103, "LeaveBalance", 0, "Leave Balance", 3, 102));
                menuDao.insert(new MenuInfo(102104, "LeaveInfo", 0, "Leave Info", 3, 102));
                menuDao.insert(new MenuInfo(103101, "HolidayInfo", 0, "Public Holiday List", 4, 103));
                menuDao.insert(new MenuInfo(104101, "ManualEntry", 0, "Manual Entry", 5, 104));
                menuDao.insert(new MenuInfo(104102, "OutdoorEntry", 0, "Outdoor Entry", 5, 104));
                menuDao.insert(new MenuInfo(104103, "AttendanceInfo", 0, "Attendance Info", 5, 104));
                menuDao.insert(new MenuInfo(104104, "SwipeDetails", 0, "Swipe Details", 5, 104));
                menuDao.insert(new MenuInfo(104105, "ShiftRoaster", 0, "Shift Roaster", 5, 104));
                menuDao.insert(new MenuInfo(106101, "BluetoothSetting", 0, "Bluetooth Setting", 6, 106));
                menuDao.insert(new MenuInfo(106102, "WifiSetting", 0, "Wifi Setting", 6, 106));
                menuDao.insert(new MenuInfo(106103, "BindSetting", 0, "Attendance Binding", 6, 106));
            }
            return null;
        }
    }

}
