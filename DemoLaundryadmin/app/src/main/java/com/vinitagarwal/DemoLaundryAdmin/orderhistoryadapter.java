package com.vinitagarwal.DemoLaundryAdmin;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class orderhistoryadapter extends RecyclerView.Adapter {
    ArrayList<JSONObject> list;
    MainActivity orderhistory;

    public orderhistoryadapter(ArrayList<JSONObject> list, MainActivity orderhistory) {
        this.list = list;
        this.orderhistory = orderhistory;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderhistoryrecv,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position1) {
        viewholder viewholder = (orderhistoryadapter.viewholder) holder;
        int position = position1;
        JSONObject element = list.get(position);
        try {
            viewholder.deliverydate.setText("Delivery Date: "+element.getString("deliverydate"));
            viewholder.orderdate.setText("Ordered On: "+element.getString("orderdate"));
            viewholder.orderamt.setText("Total Order Amount: "+element.getString("orderamt"));
            viewholder.orderid.setText("Receipt Id:"+element.getString("orderid"));
            viewholder.productqty.setText("Total Qty: "+element.getString("orderqty"));
            viewholder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        orderhistory.itemclicked(position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            viewholder.cancelbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        orderhistory.cancelorder(position,3);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            viewholder.receivedbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        orderhistory.cancelorder(position,1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            viewholder.deliveredbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        orderhistory.cancelorder(position,2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            if (element.getInt("status") == 1){
                viewholder.cardView.setCardBackgroundColor(Color.parseColor("#dbc37f"));
            }
            if (element.getInt("status") == 2){
                viewholder.cardView.setCardBackgroundColor(Color.parseColor("#a1e38f"));
            }
            if (element.getInt("status") == 3){
                viewholder.cardView.setCardBackgroundColor(Color.parseColor("#f28aab"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("test", "onBindViewHoldererr:"+e.toString());
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }
    public class viewholder extends RecyclerView.ViewHolder{
        TextView orderid,productqty,orderamt,orderdate,deliverydate;
        CardView cardView;
        Button cancelbtn,receivedbtn,deliveredbtn;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            orderid = itemView.findViewById(R.id.orderid);
            productqty = itemView.findViewById(R.id.productqty);
            orderamt = itemView.findViewById(R.id.orderamt);
            orderdate = itemView.findViewById(R.id.orderdate);
            deliverydate = itemView.findViewById(R.id.deliverydate);
            cardView = itemView.findViewById(R.id.parentcard);
            cancelbtn = itemView.findViewById(R.id.cancelbtn);
            receivedbtn = itemView.findViewById(R.id.receivedbtn);
            deliveredbtn = itemView.findViewById(R.id.deliveredbtn);
        }
    }
}
