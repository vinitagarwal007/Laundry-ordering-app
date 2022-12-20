package com.vinitagarwal.DemoLaundryAdmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class recvadapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    ArrayList<JSONObject> list = new ArrayList<>();
    productpage productpage;
    public recvadapter(Context context, ArrayList<JSONObject> list,productpage productpage) {
        this.context = context;
        this.list = list;
        this.productpage = productpage;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderrecv,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        JSONObject element = list.get(position);
        viewholder viewholder = (recvadapter.viewholder) holder;
        try {
            viewholder.productname.setText(element.getString("productname"));
            viewholder.productprice.setText("₹"+element.getString("productprice")+"/pcs");
            viewholder.cardViewcore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    productpage.handlechangeproduct(list.get(position));
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class viewholder extends RecyclerView.ViewHolder{
        public TextView productname,productprice;
        public CardView cardViewcore;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            productname = itemView.findViewById(R.id.productname);
            productprice = itemView.findViewById(R.id.productprice);
            cardViewcore = itemView.findViewById(R.id.cardViewcore);
        }
    }
}

