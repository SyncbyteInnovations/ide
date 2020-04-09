//package ids.employeeat.wifi;
//
//import android.app.Activity;
//import android.content.Context;
//import android.net.wifi.ScanResult;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.xcheng.attendance.helper.ble.BleClientActivity;
//import com.xcheng.attendance.helper.R;
//
//import java.util.List;
//
//public class WifiListAdapter extends BaseAdapter {
//    private static final String TAG = "WifiListAdapter";
//
//    LayoutInflater inflater;
//    List<ScanResult> list;
//    BleClientActivity mActivity;
//    public WifiListAdapter(BleClientActivity activity, List<ScanResult> list) {
//        // TODO Auto-generated constructor stub
//        this.inflater = LayoutInflater.from(activity);
//        this.list = list;
//        this.mActivity = activity;
//    }
//
//    @Override
//    public int getCount() {
//        // TODO Auto-generated method stub
//        return list.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        // TODO Auto-generated method stub
//        return position;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        // TODO Auto-generated method stub
//        return position;
//    }
//
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View view = null;
//        view = inflater.inflate(R.layout.wifi_list_item, null);
//        ScanResult scanResult = list.get(position);
//        TextView textView = (TextView) view.findViewById(R.id.textView);
//        textView.setText(scanResult.SSID);
//        TextView signalStrenth = (TextView) view.findViewById(R.id.signal_strenth);
//        signalStrenth.setText(String.valueOf(Math.abs(scanResult.level)));
//        signalStrenth.setVisibility(View.GONE);
//        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
//        if (Math.abs(scanResult.level) > 100) {
//            imageView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.stat_sys_wifi_signal_0));
//        } else if (Math.abs(scanResult.level) > 80) {
//            imageView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.stat_sys_wifi_signal_1));
//        } else if (Math.abs(scanResult.level) > 70) {
//            imageView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.stat_sys_wifi_signal_1));
//        } else if (Math.abs(scanResult.level) > 60) {
//            imageView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.stat_sys_wifi_signal_2));
//        } else if (Math.abs(scanResult.level) > 50) {
//            imageView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.stat_sys_wifi_signal_3));
//        } else {
//            imageView.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.stat_sys_wifi_signal_4));
//        }
//        view.setOnClickListener(new OnClickListener() {
//            public void onClick(View view) {
//                mActivity.setScanResult(scanResult);
//                Log.d(TAG, "onClick, SSID = " + scanResult.SSID);
//            }
//        });
//        return view;
//    }
//}
