package ids.employeeat.ui.list.home.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import helper.logger.LogUtil;
import helper.utils.java.DateUtil;
import ids.employeeat.JsonTag;
import ids.employeeat.R;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private JSONArray dataArray = new JSONArray();

    public NotificationAdapter(Context context)
    {
        inflater = LayoutInflater.from(context);
    }

    @Override
    @NonNull
    public NotificationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.adapter_notfication, parent, false);
        return new NotificationAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        try
        {
            JSONObject data = this.dataArray.getJSONObject(position);
            holder.tvDate.setText(setDate(data));
            holder.tvPersonCode.setText(setName(data));
            holder.tvReason.setText(setReason(data));

        } catch (Exception e)
        {
            LogUtil.logException("HolidayInfoAdapter", e);
        }
    }

    private String setName(JSONObject data) throws Exception
    {
        String name = null;

        LogUtil.logInfo(data.toString() + ">>>>");

        if (data.has(JsonTag.NAME))
        {
            name = data.getString(JsonTag.NAME);
        }

        if (data.has(JsonTag.PERSON_CODE))
        {
            String personCode = data.getString(JsonTag.PERSON_CODE);
            if (name != null)
            {
                name = name + " [" + personCode + "]";
            }
            else
            {
                name = personCode;
            }
        }
        return name;
    }

    private String setReason(JSONObject data) throws Exception
    {
        String reason = null;
        if (data.has(JsonTag.REASON))
        {
            reason = data.getString(JsonTag.REASON);
        }
        return reason;
    }


    private String setDate(JSONObject data) throws Exception
    {
        String date = null;
        if (data.has(JsonTag.FROM_DATE))
        {
            date = DateUtil.getDate(data.getLong(JsonTag.FROM_DATE), DateUtil.DATE_FORMAT_DATE);
        }
        if (data.has(JsonTag.TO_DATE))
        {
            date = date + " - " + DateUtil.getDate(data.getLong(JsonTag.TO_DATE), DateUtil.DATE_FORMAT_DATE);
        }
        return date;
    }

    @Override
    public int getItemCount()
    {
        if (dataArray != null)
        {
            return dataArray.length();
        }
        else
        {
            return 0;
        }
    }

    public void setAdapterList(JSONArray dataArray)
    {
        this.dataArray = dataArray;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvReason, tvPersonCode;
        ImageView iv_icon;
        Button btnAccept, btnReject;

        MyViewHolder(View itemView)
        {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvReason = itemView.findViewById(R.id.tv_reason);
            tvPersonCode = itemView.findViewById(R.id.tv_person_code);
            iv_icon = itemView.findViewById(R.id.iv_icon);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }


    }
}
