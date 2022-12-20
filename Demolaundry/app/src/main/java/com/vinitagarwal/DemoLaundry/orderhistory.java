package com.vinitagarwal.DemoLaundry;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class orderhistory extends Fragment {
ArrayList<JSONObject> list;
ArrayList<String> spinnerlist,orderlist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spinnerlist = new ArrayList<>();
        spinnerlist.add("Men");
        spinnerlist.add("Female");
        spinnerlist.add("Household");
        spinnerlist.add("Woolen");
        orderlist = new ArrayList<>();
        orderlist.add("Iron");
        orderlist.add("Wash & Fold");
        orderlist.add("Wash & Iron");
        orderlist.add("Dry Clean");
    }
    Home home;
    MainActivity mainActivity;
    public orderhistory(Home home,MainActivity mainActivity) {
        this.home = home;
        this.mainActivity = mainActivity;
    }

    public void itemclicked(JSONObject element) throws JSONException {
        if (home.gotratelist){
            Log.d(TAG, "itemclicked: "+home.rates.manageqty().toString());
        }
        ArrayList<JSONObject> list1 = new ArrayList<>();
        int sum = 0;
        for (int i = 0;i < element.getString("pro").split("/").length;i++){
            JSONObject temp = new JSONObject();
            for (JSONObject item:home.rates.manageqty()){
                if(item.getString("productid").equals(element.getString("pro").split("/")[i])){
                    String str = "";
                    Log.d("debug", "itemclicked: "+element.getString("cat"));
                    Log.d("debug", "itemclicked: "+element.getString("procat"));
                    str = item.getString("productname")+"\n"+orderlist.get(Integer.parseInt(element.getString("cat").split("/")[i]))+"\n"+spinnerlist.get(Integer.parseInt(element.getString("procat").split("/")[i])-1);
                    temp.put("productname",str);
                    str = "₹" + item.getString("productprice").split(",")[Integer.parseInt(element.getString("cat").split("/")[i])] + "x" + element.getString("qty").split("/")[i];
                    temp.put("productprice",str);
                    str = "₹" + String.valueOf(Integer.parseInt(item.getString("productprice").split(",")[Integer.parseInt(element.getString("cat").split("/")[i])])*Integer.parseInt(element.getString("qty").split("/")[i]));
                    temp.put("producttotal",str);
                    sum += Integer.parseInt(item.getString("productprice").split(",")[Integer.parseInt(element.getString("cat").split("/")[i])])*Integer.parseInt(element.getString("qty").split("/")[i]);
                    list1.add(temp);
                }
            }
        }
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.orderhistorypopup);
        TextView totalView = dialog.findViewById(R.id.orderhistorytotaltextview);
        totalView.setText("Total Amount:" + String.valueOf(sum));
        TextView deliverydetails = dialog.findViewById(R.id.orderhistorydeliverydetails);
        TextView pickupdetails = dialog.findViewById(R.id.orderhistorypickupdetails);
        deliverydetails.setText(element.getString("delivery"));
        pickupdetails.setText(element.getString("pickup"));
        RecyclerView recyclerView = dialog.findViewById(R.id.orderhistorypopuprecv);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        Log.d(TAG, "popup list: "+list1.toString());
        recyclerView.setAdapter(new ordersummaryadapter(list1));
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        dialog.getWindow().setLayout(width-100,height-100);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.orderbackground);
        ImageView back = dialog.findViewById(R.id.backbuttonpopup);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    String TAG = "test";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SwipeRefreshLayout swipeRefreshLayout = getView().findViewById(R.id.orderhistoryswiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateorderlist();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        return inflater.inflate(R.layout.fragment_orderhistory, container, false);
    }
    public void updateorderlist(){
        DatabaseReference orderlist = FirebaseDatabase.getInstance().getReference("orders").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        list = new ArrayList<>();
        orderlist.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    JSONObject temp = new JSONObject();
                    Log.d("test", "orderid:" + snapshot1.getKey());
                    try {
                        temp.put("orderid", snapshot1.getKey());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int totalqty = 0, totalprice = 0;
                    for (int i = 0; i < snapshot1.child("qty").getValue().toString().split("/").length; i++) {
                        totalqty += Integer.parseInt(snapshot1.child("qty").getValue().toString().split("/")[i]);
                        totalprice += Integer.parseInt(snapshot1.child("qty").getValue().toString().split("/")[i]) * Integer.parseInt(snapshot1.child("proprice").getValue().toString().split("/")[i]);
                    }
                    try {
                        temp.put("delivery", snapshot1.child("delivery").getValue().toString());
                        temp.put("pickup", snapshot1.child("pickup").getValue().toString());
                        temp.put("orderamt", totalprice);
                        temp.put("orderqty", totalqty);
                        temp.put("orderdate", snapshot1.child("orderdate").getValue().toString());
                        temp.put("deliverydate", snapshot1.child("deliverydate").getValue().toString());
                        temp.put("status",snapshot1.child("status").getValue().toString());
                        temp.put("qty",snapshot1.child("qty").getValue().toString());
                        temp.put("proprice",snapshot1.child("proprice").getValue().toString());
                        temp.put("pro",snapshot1.child("product").getValue().toString());
                        temp.put("procat",snapshot1.child("procat").getValue().toString());
                        temp.put("cat",snapshot1.child("cat").getValue().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    list.add(temp);
                }
                Log.d(TAG, "onViewCreated:ended " + list.toString());
                if(list.size() == 0){
                    TextView emptycart = getView().findViewById(R.id.emptyorder);
                    emptycart.setVisibility(View.VISIBLE);
                }else{
                    TextView emptycart = getView().findViewById(R.id.emptyorder);
                    emptycart.setVisibility(View.GONE);
                }
                RecyclerView recyclerView = getView().findViewById(R.id.orderhistoryrecv);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(new orderhistoryadapter(list, orderhistory.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void cancelorder(int position) throws JSONException {
        JSONObject element = list.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Cancel");
        builder.setMessage("Are you sure you want to cancel this order?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                try {
                    FirebaseDatabase.getInstance().getReference("orders").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child(element.getString("orderid")).child("status").setValue(3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                updateorderlist();
                Toast.makeText(mainActivity, "Order Canceled Successfully", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}