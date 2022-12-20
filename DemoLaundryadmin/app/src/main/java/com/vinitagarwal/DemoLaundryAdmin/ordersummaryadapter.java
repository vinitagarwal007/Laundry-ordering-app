package com.vinitagarwal.DemoLaundryAdmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ordersummaryadapter extends RecyclerView.Adapter {
    ArrayList<JSONObject> list;

    public ordersummaryadapter(ArrayList<JSONObject> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ordersummaryrecv, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        viewholder viewholder = (viewholder) holder;
        try {
            viewholder.productname.setText(list.get(position).getString("productname"));
            viewholder.productprice.setText(list.get(position).getString("productprice"));
            viewholder.producttotal.setText(list.get(position).getString("producttotal"));
            viewholder.slno.setText(String.valueOf(position+1)+".");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView productname, productprice, producttotal, slno;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            productname = itemView.findViewById(R.id.productname);
            productprice = itemView.findViewById(R.id.productprice);
            producttotal = itemView.findViewById(R.id.producttotal);
            slno = itemView.findViewById(R.id.slno);
        }
    }
}
