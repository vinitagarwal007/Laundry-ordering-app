package com.vinitagarwal.DemoLaundryAdmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends Fragment {
    ArrayList<JSONObject> ratelist = new ArrayList<>();
    ArrayList<JSONObject> list = new ArrayList<>();
    ArrayList<String> spinnerlist, orderlist;
    Home mainActivity;

    public MainActivity(Home mainActivity) {
        this.mainActivity = mainActivity;
    }

    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        SwipeRefreshLayout swipeRefreshLayout = getView().findViewById(R.id.orderhistoryswiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateorderlist();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        updateorderlist();
    }


    public void updateorderlist() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbref = database.getReference("rates");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    JSONObject rates = new JSONObject();
                    try {
                        rates.put("productid", ds.getKey());
                        rates.put("productqty", "0");
                        for (DataSnapshot subds : snapshot.child(ds.getKey()).getChildren()) {
                            rates.put(subds.getKey(), subds.getValue().toString());
                        }
                        ratelist.add(rates);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference orderlist = FirebaseDatabase.getInstance().getReference("orders");
        list = new ArrayList<JSONObject>();
        orderlist.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    {
                        for (DataSnapshot snapshot1 : snap.getChildren()) {
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
                                temp.put("status", snapshot1.child("status").getValue().toString());
                                temp.put("qty", snapshot1.child("qty").getValue().toString());
                                temp.put("proprice", snapshot1.child("proprice").getValue().toString());
                                temp.put("pro", snapshot1.child("product").getValue().toString());
                                temp.put("procat", snapshot1.child("procat").getValue().toString());
                                temp.put("cat", snapshot1.child("cat").getValue().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            list.add(temp);
                        }
                    }
                    Log.d("test", "onDataChange: " + list.size());
                    if (list.size() == 0) {
                        TextView emptycart = getView().findViewById(R.id.emptyorder);
                        emptycart.setVisibility(View.VISIBLE);
                    } else {
                        TextView emptycart = getView().findViewById(R.id.emptyorder);
                        emptycart.setVisibility(View.GONE);
                    }
                    for (int i = 0; i < list.size()-1; i++) {
                        for (int j = 0; j < list.size()-1; j++) {
                            try {
                                if(list.get(j).getInt("orderid") < list.get(j+1).getInt("orderid")){
                                    JSONObject tempvar = new JSONObject();
                                    tempvar = list.get(j);
                                    list.set(j,list.get(j+1));
                                    list.set(j+1,tempvar);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (list.size() == 0) {
                        TextView emptycart = getView().findViewById(R.id.emptyorder);
                        emptycart.setVisibility(View.VISIBLE);
                    } else {
                        TextView emptycart = getView().findViewById(R.id.emptyorder);
                        emptycart.setVisibility(View.GONE);
                    }
                    RecyclerView recyclerView = getView().findViewById(R.id.orderhistoryrecv);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new orderhistoryadapter(list, MainActivity.this));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void itemclicked(int position) throws JSONException {
        JSONObject element = list.get(position);
        ArrayList<JSONObject> list = new ArrayList<>();
        int sum = 0;
        for (int i = 0; i < element.getString("pro").split("/").length; i++) {
            JSONObject temp = new JSONObject();
            for (JSONObject item : ratelist) {
                if (item.getString("productid").equals(element.getString("pro").split("/")[i])) {
                    String str = "";
                    str = item.getString("productname") + "\n" + orderlist.get(Integer.parseInt(element.getString("cat").split("/")[i]) - 1) + "\n" + spinnerlist.get(Integer.parseInt(element.getString("procat").split("/")[i]) - 1);
                    temp.put("productname", str);
                    str = "₹" + item.getString("productprice").split(",")[Integer.parseInt(element.getString("cat").split("/")[i])] + "x" + element.getString("qty").split("/")[i];
                    temp.put("productprice", str);
                    str = "₹" + String.valueOf(Integer.parseInt(item.getString("productprice").split(",")[Integer.parseInt(element.getString("cat").split("/")[i])]) * Integer.parseInt(element.getString("qty").split("/")[i]));
                    temp.put("producttotal", str);
                    sum += Integer.parseInt(item.getString("productprice").split(",")[Integer.parseInt(element.getString("cat").split("/")[i])]) * Integer.parseInt(element.getString("qty").split("/")[i]);
                    list.add(temp);
                }
            }
        }
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.orderhistorypopup);
        TextView totalView = dialog.findViewById(R.id.orderhistorytotaltextview);
        TextView deliverydetails = dialog.findViewById(R.id.orderhistorydeliverydetails);
        TextView pickupdetails = dialog.findViewById(R.id.orderhistorypickupdetails);
        deliverydetails.setText(element.getString("delivery"));
        pickupdetails.setText(element.getString("pickup"));
        totalView.setText("Total Amount:" + String.valueOf(sum));
        RecyclerView recyclerView = dialog.findViewById(R.id.orderhistorypopuprecv);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new ordersummaryadapter(list));
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        dialog.getWindow().setLayout(width - 100, height - 100);
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

    public void cancelorder(int position, int status) throws JSONException {
        JSONObject element = list.get(position);
        Log.d("test", "cancelorder: " + element.getString("delivery").split(System.lineSeparator())[1]);
        FirebaseDatabase.getInstance().getReference("orders").child(element.getString("delivery").split(System.lineSeparator())[1]).child(element.getString("orderid")).child("status").setValue(status);
        updateorderlist();
    }
}