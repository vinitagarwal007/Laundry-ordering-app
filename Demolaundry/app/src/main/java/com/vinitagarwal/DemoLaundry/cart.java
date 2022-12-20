package com.vinitagarwal.DemoLaundry;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class cart extends Fragment {
        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    MainActivity mainActivity;
    public cart(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
    public int lastorder;
    Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = container.getContext();
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    Integer totalsum = 0;
    TextView totalview;
    RecyclerView recyclerView;

    public void itemchanged(JSONObject backlist) {
//        totalsum = 0;
//        try {
//            for (int i = 0; i < backlist.names().length(); i++) {
//                Log.d("itemchanged", "itemchanged: " + backlist.getInt(backlist.names().getString(i)));
//                totalsum += backlist.getInt(backlist.names().getString(i));
//            }
//            totalview.setText("Total Cart: ₹" + String.valueOf(totalsum));
//        } catch (Exception e) {
//            Log.d("itemchanged", "itemchanged: " + e.toString());
//        }
    }

    rates rates;
    TextView noitem;
    ArrayList<String> spinnerlist,orderlist;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkout = getView().findViewById(R.id.checkoutbtn);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlecheckout();
            }
        });
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
        noitem = getView().findViewById(R.id.emptycart);
        recyclerView = getView().findViewById(R.id.cartrecv);
        totalview = getView().findViewById(R.id.totalview);
        updatecart();
    }
    Dialog dialog;
    private void handlecheckout() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.delivery);
        dialog.getWindow().setLayout(width - 100, height - 100);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.orderbackground);
        TextView ordersummarytotal;
        ordersummarytotal = dialog.findViewById(R.id.ordersummarytotaltextview);
        String ordersum = "";
        int sum = 0;
        Iterator<String> itr = rates.cartlist().keys();
        ArrayList<JSONObject> list = new ArrayList<>();
        while (itr.hasNext()){
            String key = itr.next();
            try {
                Iterator<String> productiditr= rates.cartlist().getJSONObject(key).keys();
                while(productiditr.hasNext()){
                    String productid = productiditr.next();
                    JSONObject temp = new JSONObject();
                    ordersum += rates.cartlist().getJSONObject(key).getJSONObject(productid).getString("productname");
                    ordersum += "\n";
                    ordersum += orderlist.get(rates.cartlist().getJSONObject(key).getJSONObject(productid).getInt("catselection"));
                    ordersum += "\n";
                    ordersum += spinnerlist.get(rates.cartlist().getJSONObject(key).getJSONObject(productid).getInt("productcat")-1);
                    temp.put("productname",ordersum);
                    ordersum = "₹";
                    ordersum += rates.cartlist().getJSONObject(key).getJSONObject(productid).getString("productprice");
                    ordersum += " x ";
                    ordersum += rates.cartlist().getJSONObject(key).getJSONObject(productid).getString("productqty");
                    temp.put("productprice",ordersum);
                    ordersum = "";
                    ordersum += "₹";
                    ordersum += String.valueOf(rates.cartlist().getJSONObject(key).getJSONObject(productid).getInt("productprice") * rates.cartlist().getJSONObject(key).getJSONObject(productid).getInt("productqty"));
                    temp.put("producttotal",ordersum);
                    ordersum = "";
                    list.add(temp);
                    sum += rates.cartlist().getJSONObject(key).getJSONObject(productid).getInt("productprice") * rates.cartlist().getJSONObject(key).getJSONObject(productid).getInt("productqty");
                }
            }catch (JSONException ex){}
        }
        RecyclerView ordersummary;
        ordersummary = dialog.findViewById(R.id.ordersummarytextview);
        ordersummary.setLayoutManager(new LinearLayoutManager(context));
        ordersummaryadapter ordersummaryadapter = new ordersummaryadapter(list);
        ordersummary.setAdapter(ordersummaryadapter);
        ordersummarytotal.setText("Total Payable ₹"+String.valueOf(sum));

        TextView deliverycontact,pickupcontact,pickupchange,deliverychange;
        EditText deliveraddress,pickupaddress,pickupname,deliveryname;
        Button orderbtn;
        orderbtn = dialog.findViewById(R.id.orderbtn);
        deliveryname = dialog.findViewById(R.id.deliveryname);
        deliverycontact = dialog.findViewById(R.id.deliverycontact);
        deliveraddress = dialog.findViewById(R.id.deliveryaddress);

        pickupname = dialog.findViewById(R.id.pickupname);
        pickupaddress = dialog.findViewById(R.id.pickupaddress);
        pickupcontact = dialog.findViewById(R.id.pickupcontact);
        try {
            String str = "";
            for (int i = 3; i <rates.getpickupdetails().split(System.lineSeparator()).length; i++) {
                str += rates.getpickupdetails().split(System.lineSeparator())[i];
                str += System.lineSeparator();
            }
            pickupaddress.setText(str);
            pickupname.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            pickupcontact.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+"\nAddress:");

            str = "";
            for (int i = 3; i <rates.getdeliverydetails().split(System.lineSeparator()).length; i++) {
                str += rates.getdeliverydetails().split(System.lineSeparator())[i];
                str += System.lineSeparator();
            }
            deliveraddress.setText(str);
            deliveryname.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            deliverycontact.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+"\nAddress:");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        orderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Iterator<String> itr = rates.cartlist().keys();
                ArrayList<JSONObject> list = new ArrayList<>();
                JSONObject temp = new JSONObject();
                try {
                    temp.put("product","");
                    temp.put("qty","");
                    temp.put("cat","");
                    temp.put("procat","");
                    temp.put("proprice","");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(!pickupaddress.getText().toString().equals("") && !deliveraddress.getText().toString().equals("")) {
                    Log.d("test", "lastorder "+lastorder);
                    while (itr.hasNext()) {
                        String key = itr.next();
                        try {
                            Iterator<String> productiditr = rates.cartlist().getJSONObject(key).keys();
                            while (productiditr.hasNext()) {
                                String productid = productiditr.next();
                                temp.put("product", temp.getString("product") + productid+ "/");
                                temp.put("qty", temp.getString("qty") + rates.cartlist().getJSONObject(key).getJSONObject(productid).getString("productqty")+ "/");
                                temp.put("cat", temp.getString("cat") + rates.cartlist().getJSONObject(key).getJSONObject(productid).getString("catselection")+ "/");
                                temp.put("procat", temp.getString("procat") + rates.cartlist().getJSONObject(key).getJSONObject(productid).getString("productcat")+ "/");
                                temp.put("proprice", temp.getString("proprice") + rates.cartlist().getJSONObject(key).getJSONObject(productid).getString("productprice")+ "/");
                            }
                        } catch (Exception e) {
                            Log.d("test", "onClick: " + e.toString());
                        }
                    }
                    try {
                        Log.d("test", "onClick: " + temp.getString("product"));
                        rates.changedeliverydetails( deliveryname.getText() +System.lineSeparator()+deliverycontact.getText()+System.lineSeparator()+ deliveraddress.getText().toString()+"@"+pickupname.getText() +System.lineSeparator()+pickupcontact.getText()+System.lineSeparator()+pickupaddress.getText().toString());
                        FirebaseDatabase.getInstance().getReference("orders").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child(String.valueOf(lastorder)).child("product").setValue(temp.getString("product"));
                        FirebaseDatabase.getInstance().getReference("orders").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child(String.valueOf(lastorder)).child("qty").setValue(temp.getString("qty"));
                        FirebaseDatabase.getInstance().getReference("orders").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child(String.valueOf(lastorder)).child("cat").setValue(temp.getString("cat"));
                        FirebaseDatabase.getInstance().getReference("orders").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child(String.valueOf(lastorder)).child("procat").setValue(temp.getString("procat"));
                        FirebaseDatabase.getInstance().getReference("orders").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child(String.valueOf(lastorder)).child("proprice").setValue(temp.getString("proprice"));
                        FirebaseDatabase.getInstance().getReference("orders").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child(String.valueOf(lastorder)).child("delivery").setValue(rates.getdeliverydetails());
                        FirebaseDatabase.getInstance().getReference("orders").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child(String.valueOf(lastorder)).child("pickup").setValue(rates.getpickupdetails());
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
                        FirebaseDatabase.getInstance().getReference("orders").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child(String.valueOf(lastorder)).child("orderdate").setValue(sdf.format(new Date()));
                        Calendar c = Calendar.getInstance();
                        c.setTime(new Date());
                        c.add(Calendar.DATE,7);
                        FirebaseDatabase.getInstance().getReference("orders").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child(String.valueOf(lastorder)).child("deliverydate").setValue(sdf.format(c.getTime()));
                        FirebaseDatabase.getInstance().getReference("orders").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child(String.valueOf(lastorder)).child("status").setValue(0);
                        rates.emptycart();
                        updatecart();
                        Dialog dialog2 = new Dialog(context);
                        dialog2.setContentView(R.layout.orderfinished);
                        dialog2.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialog2.show();
                        dialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    FirebaseDatabase.getInstance().getReference("lastorder").setValue(String.valueOf(lastorder + 1));
                }else{
                    Toast.makeText(mainActivity, "Invalid Address", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ImageView back = dialog.findViewById(R.id.backbuttoncheckout);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    Button checkout;
    void updatecart() {
        rates = new rates(this);
        rates.initsharedpref(this);
        Log.d("test", "updatecartdoing: ");
        JSONObject cartlist = rates.cartlist();

        totalview.setText("Total Cart: ₹" + String.valueOf(totalsum));
        ArrayList<JSONObject> list = new ArrayList<>();
        JSONObject object;
        Iterator<String> itr = cartlist.keys();
        totalsum = 0;
        while (itr.hasNext()){
            String key = itr.next();
            try {
                object = new JSONObject();
                Iterator<String> productiditr= cartlist.getJSONObject(key).keys();
                        while(productiditr.hasNext()){
                            String productid = productiditr.next();
                            object = cartlist.getJSONObject(key).getJSONObject(productid);
                            object.put("productid",productid);
                            object.put("ordercat",key);
                            list.add(object);
                            totalsum += (object.getInt("productprice")* object.getInt("productqty"));
                        }
            }catch (JSONException ex){}
        }
        totalview.setText("Total Cart: ₹" + String.valueOf(totalsum));
        Log.d("test", "updatecart: "+totalsum);
        cartrecvadapter recvadapter = new cartrecvadapter(context, list, this);
        recyclerView.setAdapter(recvadapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        if(list.size() == 0){
            noitem.setVisibility(View.VISIBLE);
            checkout.setEnabled(false);
            totalview.setText("Total Cart: ₹0");
            return;
        }else{
            checkout.setEnabled(true);
            noitem.setVisibility(View.GONE);
        }
    }

    void deletecart(JSONObject item){
        rates.deletecart(item);
    }

}