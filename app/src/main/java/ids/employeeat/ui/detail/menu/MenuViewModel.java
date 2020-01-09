package ids.employeeat.ui.detail.menu;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import helper.logger.LogUtil;
import ids.employeeat.data.database.menu.MenuInfo;
import ids.employeeat.data.repository.menu.MenuRepository;

public class MenuViewModel extends AndroidViewModel {

    private MenuRepository repository;

    public MenuViewModel(Application application)
    {
        super(application);
        repository = new MenuRepository(application);
    }

    public LiveData<List<MenuInfo>> getMenuList(int masterId)
    {
        return repository.getMenuList(masterId);
    }

    public void insert(MenuInfo word)
    {
        repository.insert(word);
    }

}
