package ids.employeeat.ui.list.outdoor.outdoorInfo;

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

public class OutdoorInfoAdapter extends RecyclerView.Adapter<OutdoorInfoAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private JSONArray dataArray = new JSONArray();

    public OutdoorInfoAdapter(Context context)
    {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.adapter_swipe_details, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        try
        {
            JSONObject data = this.dataArray.getJSONObject(position);
            holder.tvDevice.setText(data.getString("deviceName"));

            String date = " ";
            date = date.concat(DateUtil.getDate(data.getLong("logDate"), DateUtil.DATE_FORMAT_FULL));
            holder.tvDate.setText(date);

        } catch (Exception e)
        {
            LogUtil.logException("OutdoorInfoAdapter", e);
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
        TextView tvDevice, tvDate;

        MyViewHolder(View itemView)
        {
            super(itemView);
            tvDevice = itemView.findViewById(R.id.tv_device);
            tvDate = itemView.findViewById(R.id.tv_time);
        }


    }
}
