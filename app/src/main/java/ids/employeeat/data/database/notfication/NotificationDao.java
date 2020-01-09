package ids.employeeat.data.database.notfication;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotificationDao {

    @Query("SELECT * from tbl_notification order by createdDate desc limit 15")
    LiveData<List<NotificationInfo>> getNotificationList();

    @Insert
    void insert(NotificationInfo notificationInfo);
}
