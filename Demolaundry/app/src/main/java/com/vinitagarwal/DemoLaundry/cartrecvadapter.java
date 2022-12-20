package com.vinitagarwal.DemoLaundry;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class cartrecvadapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    cart cart;
    ArrayList<JSONObject> list = new ArrayList<>();

    public cartrecvadapter(Context context, ArrayList<JSONObject> list, cart cart) {
        this.cart = cart;
        this.context = context;
        this.list = list;
    }

    JSONObject backlist = new JSONObject();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cartrecv, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        JSONObject element = list.get(position);
        viewholder viewholder = (cartrecvadapter.viewholder) holder;
        try {
            viewholder.productname.setText(element.getString("productname"));
            viewholder.productqty.setText("Qty: "+element.getString("productqty")+" Pcs");
            viewholder.productqty.setTag(element.getString("productqty"));
            ArrayList<String> spinnerlist = new ArrayList<>();
            spinnerlist.add("Men");
            spinnerlist.add("Female");
            spinnerlist.add("Household");
            spinnerlist.add("Woolen");
            viewholder.productcat.setText(spinnerlist.get(element.getInt("productcat") - 1));
            ArrayList<String> orderlist = new ArrayList<>();
            orderlist.add("Iron");
            orderlist.add("Wash & Fold");
            orderlist.add("Wash & Iron");
            orderlist.add("Dry Clean");
            viewholder.ordercat.setText(orderlist.get(element.getInt("ordercat")));
            viewholder.deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(cart.context);

                    builder.setTitle("Confirm Delete");
                    builder.setMessage("Are you sure you want to delete the item?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            cart.deletecart(list.get(position));
                            cart.updatecart();
                            dialog.dismiss();
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
            });
//            viewholder.productcat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                    try {
//                        int productprice = Integer.parseInt(element.getString("productprice").split(",")[Integer.parseInt(element.getString("ordercat"))]);
//                        viewholder.productprice2.setText("₹"+String.valueOf(Integer.parseInt(element.getString("productprice").split(",")[i]) * Integer.parseInt(viewholder.productqty.getText().toString())));
//                        viewholder.productprice.setText("₹"+element.getString("productprice").split(",")[i]+"/pcs");
//                        viewholder.productqty.setTag(Integer.parseInt(element.getString("productprice").split(",")[i]));
//                        backlist.put(String.valueOf(position),String.valueOf(Integer.parseInt(viewholder.productqty.getTag().toString()) * Integer.parseInt(viewholder.productqty.getText().toString())));
//                        cart.itemchanged(backlist);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> adapterView) {
//
//                }
//            });
            //int productprice = Integer.parseInt(element.getString("productprice").split(",")[Integer.parseInt(element.getString("productcat"))]);
            int productprice = Integer.parseInt(element.getString("productprice"));
            viewholder.productprice.setText("₹" + String.valueOf(productprice) + "/pcs");
            viewholder.productprice2.setText("₹" + String.valueOf(productprice * Integer.parseInt(viewholder.productqty.getTag().toString())));
            backlist.put(String.valueOf(position), productprice * Integer.parseInt(viewholder.productqty.getTag().toString()));
            cart.itemchanged(backlist);
            viewholder.productqty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        viewholder.productprice2.setText("₹" + String.valueOf(Integer.parseInt(viewholder.productqty.getTag().toString()) * Integer.parseInt(viewholder.productqty.getText().toString())));
                        backlist.put(String.valueOf(position), String.valueOf(Integer.parseInt(viewholder.productqty.getTag().toString()) * Integer.parseInt(viewholder.productqty.getText().toString())));
                        cart.itemchanged(backlist);
                    } catch (Exception e) {
                        Log.d(TAG, "onTextChanged: " + e.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        } catch (JSONException e) {
            Log.d(TAG, "onBindViewHolder: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        public Button deletebtn;
        public TextView productname, productprice, productprice2, productcat, ordercat, productqty;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            deletebtn = itemView.findViewById(R.id.deletebtn);
            productname = itemView.findViewById(R.id.productname);
            productprice = itemView.findViewById(R.id.productprice);
            productprice2 = itemView.findViewById(R.id.productprice2);
            productqty = itemView.findViewById(R.id.productqty);
            productcat = itemView.findViewById(R.id.productcat);
            ordercat = itemView.findViewById(R.id.ordercat);

        }
    }
}


class InputFilterMinMax1 implements InputFilter {

    private int min, max;

    public InputFilterMinMax1(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax1(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) {
        }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
