package ids.employeeat.data.database.menu;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MenuDao {

    @Insert
    void insert(MenuInfo menuInfo);

    @Query("SELECT * from tbl_menuInfo where masterId = :masterId")
    LiveData<List<MenuInfo>> getMenuList(int masterId);

    @Query("SELECT count(menuId) from tbl_menuInfo")
    long getCount();




}
