package com.vinitagarwal.DemoLaundry;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class recvadapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    ArrayList<JSONObject> list = new ArrayList<>();
    Home home;
    public recvadapter(Context context, ArrayList<JSONObject> list,Home home) {
        this.context = context;
        this.list = list;
        this.home = home;
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
            viewholder.productqty.setText(element.getString("productqty"));
            viewholder.productprice.setText("₹"+element.getString("productprice")+"/pcs");
            viewholder.minbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int qty = Integer.parseInt(viewholder.productqty.getText().toString());
                    if(qty > 0){
                        qty -= 1;
                        viewholder.productqty.setText(String.valueOf(qty));
                    }else{
                        viewholder.productqty.setText(String.valueOf(0));
                    }
                    qtychanged(element,viewholder.productqty.getText().toString());
                }
            });
            viewholder.maxbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int qty = Integer.parseInt(viewholder.productqty.getText().toString());
                    if(qty >= 0){
                        qty += 1;
                        viewholder.productqty.setText(String.valueOf(qty));
                    }
                    qtychanged(element,viewholder.productqty.getText().toString());
                }
            });
            viewholder.productqty.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "100")});

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    void qtychanged(JSONObject item,String currentqty){
        try {
            item.put("productqty",currentqty);
            item.put("catselection",home.catselection);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        rates rates = new rates(home);
        rates.initsharedpref(home);
        rates.updatecart(item);
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    public class viewholder extends RecyclerView.ViewHolder{
        public Button minbtn,maxbtn;
        public TextView productname,productprice;
        EditText productqty;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            minbtn = itemView.findViewById(R.id.minbtn);
            maxbtn = itemView.findViewById(R.id.maxbtn);
            productname = itemView.findViewById(R.id.productname);
            productprice = itemView.findViewById(R.id.productprice);
            productqty = itemView.findViewById(R.id.productqty);
        }
    }
}





class InputFilterMinMax implements InputFilter {

    private int min, max;

    public InputFilterMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) { }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
