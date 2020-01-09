package ids.employeeat.ui.list.attendance.shiftRoaster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

import helper.logger.LogUtil;
import helper.utils.java.DateUtil;
import ids.employeeat.R;

public class ShiftRoasterAdapter extends RecyclerView.Adapter<ShiftRoasterAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private JSONArray dataArray = new JSONArray();

    public ShiftRoasterAdapter(Context context)
    {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.adapter_shift_roaster, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        try
        {
            JSONObject data = this.dataArray.getJSONObject(position);
            holder.tvShift.setText(data.getString("shift"));
            holder.tvWeek.setText(data.getString("weekDay"));
            String date = " ";
            date = date.concat(DateUtil.getUTCDate(data.getLong("date") , DateUtil.DATE_FORMAT_DATE));
            date = date.concat(" ");
            holder.tvDate.setText(date);

        } catch (Exception e)
        {
            LogUtil.logException("ShiftRoasterAdapter", e);
        }
    }

    @Override
    public int getItemCount()
    {
        return dataArray.length();
    }

    public void setAdapterList(JSONArray dataArray)
    {
        this.dataArray = dataArray;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvShift, tvWeek, tvDate;

        MyViewHolder(View itemView)
        {
            super(itemView);
            tvShift = itemView.findViewById(R.id.tv_shift);
            tvDate = itemView.findViewById(R.id.tv_time);
            tvWeek = itemView.findViewById(R.id.tv_week);
        }


    }
}
