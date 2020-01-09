package ids.employeeat.data.database.notfication;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_notification")
public class NotificationInfo {

    public static final int CON_NOTIFICATION_TYPE_MANUAL = 0;
    public static final int CON_NOTIFICATION_TYPE_ON_DUTY = 1;
    public static final int CON_NOTIFICATION_TYPE_ON_LEAVE = 2;

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "notificationId")
    private int id;
    private long typeId;
    private int type;
    private String message;
    private String name;
    private long personCode;
    private long fromDate;
    private long toDate;
    private int status;
    private long createdDate;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public long getTypeId()
    {
        return typeId;
    }

    public void setTypeId(long typeId)
    {
        this.typeId = typeId;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public long getPersonCode()
    {
        return personCode;
    }

    public void setPersonCode(long personCode)
    {
        this.personCode = personCode;
    }

    public long getFromDate()
    {
        return fromDate;
    }

    public void setFromDate(long fromDate)
    {
        this.fromDate = fromDate;
    }

    public long getToDate()
    {
        return toDate;
    }

    public void setToDate(long toDate)
    {
        this.toDate = toDate;
    }

    public long getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(long createdDate)
    {
        this.createdDate = createdDate;
    }
}
