//package ids.employeeat.wifi;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.net.wifi.ScanResult;
//import android.net.wifi.WifiManager;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.ListAdapter;
//import android.widget.ListView;
//import android.widget.ScrollView;
//
//import com.xcheng.attendance.helper.ble.BleClientActivity;
//import com.xcheng.attendance.helper.R;
//
//import java.util.List;
//
//public class WifiListDialog {
//
//    private static final String TAG = "WifiListDialog";
//    public AlertDialog mDialog;
//    private BleClientActivity mAactivity;
//
//    public WifiListDialog(BleClientActivity activity) {
//        mAactivity = activity;
//        mDialog = createDialog(mAactivity);
//    }
//
//    public AlertDialog createDialog(BleClientActivity activity) {
//        Log.d(TAG, "createDialog");
//        WifiManager mWifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
//        List<ScanResult> list = mWifiManager.getScanResults();
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setTitle(R.string.select_wifi_ap);
//        builder.setCancelable(true);
//        View view = activity.getLayoutInflater().inflate(R.layout.wifi_list_dialog, null);
//        ListView listView = (ListView) view.findViewById(R.id.listView);
//        listView.setAdapter(new WifiListAdapter(activity, list));
//        setListViewHeight(listView);
//        builder.setView(view);
//        AlertDialog wifiListDialog = builder.create();
//        return wifiListDialog;
//    }
//
//    private void setListViewHeight(ListView listView) {
//        if(listView == null) return;
//        ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter == null) {
//            // pre-condition
//            return;
//        }
//        int totalHeight = 0;
//        for (int i = 0; i < listAdapter.getCount(); i++) {
//            View listItem = listAdapter.getView(i, null, listView);
//            listItem.measure(0, 0);
//            totalHeight += listItem.getMeasuredHeight();
//        }
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//        Log.d(TAG, "setListViewHeight,params.height = " + params.height);
//        listView.setLayoutParams(params);
//    }
//
//    public void show() {
//        mDialog.show();
//    }
//
//    public void update() {
//        WifiManager mWifiManager = (WifiManager) mAactivity.getSystemService(Context.WIFI_SERVICE);
//        List<ScanResult> list = mWifiManager.getScanResults();
//
//        View view = mAactivity.getLayoutInflater().inflate(R.layout.wifi_list_dialog, null);
//        ListView listView = (ListView) view.findViewById(R.id.listView);
//        listView.setAdapter(new WifiListAdapter(mAactivity, list));
//        setListViewHeight(listView);
//        mDialog.setView(view);
//    }
//
//    public void dismiss() {
//        mDialog.dismiss();
//    }
//
//}
