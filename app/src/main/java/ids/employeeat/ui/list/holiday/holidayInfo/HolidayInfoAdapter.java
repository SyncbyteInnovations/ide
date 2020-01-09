package ids.employeeat.ui.list.holiday.holidayInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import helper.logger.LogUtil;
import helper.utils.java.DateUtil;
import ids.employeeat.R;

public class HolidayInfoAdapter extends RecyclerView.Adapter<HolidayInfoAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private JSONArray dataArray = new JSONArray();

    public HolidayInfoAdapter(Context context)
    {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.adapter_holiday_info, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        try
        {
            JSONObject data = this.dataArray.getJSONObject(position);
            holder.tvHoliday.setText(data.getString("name"));

            String date = " ";
            date = date.concat(DateUtil.getDate(data.getLong("date"), DateUtil.DATE_FORMAT_DATE));
            holder.tvDate.setText(date);

        } catch (Exception e)
        {
            LogUtil.logException("HolidayInfoAdapter", e);
        }
    }

    @Override
    public int getItemCount()
    {
        if(dataArray!=null)
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
        TextView tvHoliday, tvDate;

        MyViewHolder(View itemView)
        {
            super(itemView);
            tvHoliday = itemView.findViewById(R.id.tv_holiday);
            tvDate = itemView.findViewById(R.id.tv_time);
        }


    }
}
