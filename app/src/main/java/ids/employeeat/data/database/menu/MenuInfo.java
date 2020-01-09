package ids.employeeat.data.database.menu;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_menuInfo")
public class MenuInfo {


    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "menuId")
    private int id;
    private String className;
    private int iconId;
    private String name;
    private int groupId;
    private int masterId;

    public MenuInfo(int id, String className, int iconId, String name, int groupId, int masterId)
    {
        this.id = id;
        this.className = className;
        this.iconId = iconId;
        this.name = name;
        this.groupId = groupId;
        this.masterId = masterId;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public int getIconId()
    {
        return iconId;
    }

    public void setIconId(int iconId)
    {
        this.iconId = iconId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getGroupId()
    {
        return groupId;
    }

    public void setGroupId(int groupId)
    {
        this.groupId = groupId;
    }

    public int getMasterId()
    {
        return masterId;
    }

    public void setMasterId(int masterId)
    {
        this.masterId = masterId;
    }
}