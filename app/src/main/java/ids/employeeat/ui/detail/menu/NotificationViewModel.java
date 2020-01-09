package ids.employeeat.ui.detail.menu;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import ids.employeeat.data.repository.menu.NotificationRepository;

public class NotificationViewModel extends AndroidViewModel {

    private NotificationRepository repository;

    public NotificationViewModel(Application application)
    {
        super(application);
        repository = new NotificationRepository(application);
    }

}
