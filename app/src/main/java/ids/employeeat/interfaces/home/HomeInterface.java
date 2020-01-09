package ids.employeeat.interfaces.home;

import ids.employeeat.helper.MyLocation;
import ids.employeeat.menu.reside.menu.Reside;

public interface HomeInterface
{
    void headerTitle(String title);

    Reside getReside();

    MyLocation getLocation();
}
