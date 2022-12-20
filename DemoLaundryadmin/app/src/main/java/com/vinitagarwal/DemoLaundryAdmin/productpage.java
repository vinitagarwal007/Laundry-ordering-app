package com.vinitagarwal.DemoLaundryAdmin;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class productpage extends Fragment {
    boolean gotratelist = false;
    Home mainActivity;

    public productpage(Home mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    Context context;
    CardView washiron;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        return inflater.inflate(R.layout.fragment_productpage, container, false);
    }

    public int catselection;
    ArrayList<JSONObject> ratelist = new ArrayList<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        washiron = view.findViewById(R.id.washironcard);
        washiron.setOnClickListener(cardclick);
    }

    View.OnClickListener cardclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                ratelist = new ArrayList<>();
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
                        updategotrate();
                        try {
                            handleorder();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    Dialog dialog;
    String[] productcat = new String[]{"Men", "Women", "Household", "Woolen"};
    Boolean newdata = false;
    public void handleorder() throws JSONException {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_order);
        dialog.getWindow().setLayout(width - 100, height - 100);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.orderbackground);
        ImageView backbtn = dialog.findViewById(R.id.backbutton);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        menbtn = dialog.findViewById(R.id.menbtn);
        femalebtn = dialog.findViewById(R.id.femalebtn);
        householdbtn = dialog.findViewById(R.id.householdbtn);
        woolenbtn = dialog.findViewById(R.id.woolenbtn);
        addbtn = dialog.findViewById(R.id.addbtn);
        recyclerView = dialog.findViewById(R.id.orderrecv);


        menbtn.setOnClickListener(catbtnselection);
        femalebtn.setOnClickListener(catbtnselection);
        householdbtn.setOnClickListener(catbtnselection);
        woolenbtn.setOnClickListener(catbtnselection);
        menbtn.performClick();
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject temp = new JSONObject();
                FirebaseDatabase.getInstance().getReference("lastprodid").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            temp.put("productid", String.valueOf(Integer.parseInt(snapshot.getValue().toString()) + 1));
                            temp.put("productcat", productcatselection);
                            newdata = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        handlechangeproduct(temp);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        dialog.show();
    }

    Button menbtn;
    Button femalebtn;
    Button householdbtn;
    Button woolenbtn;
    ImageView addbtn;
    int productcatselection;
    View.OnClickListener catbtnselection = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.menbtn:
                    productcatselection = 1;
                    menbtn.setTextColor(Color.parseColor("#FFFFFF"));
                    menbtn.setBackgroundResource(R.drawable.ordercatbtnbackground);
                    femalebtn.setTextColor(Color.parseColor("#000000"));
                    femalebtn.setBackgroundResource(R.drawable.ordercatbtnbackgroundnotselected);
                    householdbtn.setTextColor(Color.parseColor("#000000"));
                    householdbtn.setBackgroundResource(R.drawable.ordercatbtnbackgroundnotselected);
                    woolenbtn.setTextColor(Color.parseColor("#000000"));
                    woolenbtn.setBackgroundResource(R.drawable.ordercatbtnbackgroundnotselected);
                    break;

                case R.id.femalebtn:
                    productcatselection = 2;
                    Log.d("test", "onClick: femaleclick");
                    menbtn.setTextColor(Color.parseColor("#000000"));
                    menbtn.setBackgroundResource(R.drawable.ordercatbtnbackgroundnotselected);
                    femalebtn.setTextColor(Color.parseColor("#FFFFFF"));
                    femalebtn.setBackgroundResource(R.drawable.ordercatbtnbackground);
                    householdbtn.setTextColor(Color.parseColor("#000000"));
                    householdbtn.setBackgroundResource(R.drawable.ordercatbtnbackgroundnotselected);
                    woolenbtn.setTextColor(Color.parseColor("#000000"));
                    woolenbtn.setBackgroundResource(R.drawable.ordercatbtnbackgroundnotselected);
                    break;
                case R.id.householdbtn:
                    productcatselection = 3;
                    menbtn.setTextColor(Color.parseColor("#000000"));
                    menbtn.setBackgroundResource(R.drawable.ordercatbtnbackgroundnotselected);
                    femalebtn.setTextColor(Color.parseColor("#000000"));
                    femalebtn.setBackgroundResource(R.drawable.ordercatbtnbackgroundnotselected);
                    householdbtn.setTextColor(Color.parseColor("#FFFFFF"));
                    householdbtn.setBackgroundResource(R.drawable.ordercatbtnbackground);
                    woolenbtn.setTextColor(Color.parseColor("#000000"));
                    woolenbtn.setBackgroundResource(R.drawable.ordercatbtnbackgroundnotselected);
                    break;
                case R.id.woolenbtn:
                    productcatselection = 4;
                    menbtn.setTextColor(Color.parseColor("#000000"));
                    menbtn.setBackgroundResource(R.drawable.ordercatbtnbackgroundnotselected);
                    femalebtn.setTextColor(Color.parseColor("#000000"));
                    femalebtn.setBackgroundResource(R.drawable.ordercatbtnbackgroundnotselected);
                    householdbtn.setTextColor(Color.parseColor("#000000"));
                    householdbtn.setBackgroundResource(R.drawable.ordercatbtnbackgroundnotselected);
                    woolenbtn.setTextColor(Color.parseColor("#FFFFFF"));
                    woolenbtn.setBackgroundResource(R.drawable.ordercatbtnbackground);
                    break;
            }
            handleselction();
        }
    };

    public void handleselction() {
        ArrayList<JSONObject> templist = new ArrayList<>();
        updatedratelist = ratelist;
        for (JSONObject element : updatedratelist) {
            try {
                if (element.getString("productcat").equals(String.valueOf(productcatselection))) {
                    templist.add(element);
                    Log.d(TAG, "handleselction: selected");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        updaterecv(templist);
    }

    String TAG = "test";
    String productid = "";
    int lastindex = 0;
    ArrayList<JSONObject> updatedratelist;

    public void updategotrate() {
        gotratelist = true;
    }

    public void updatelist(ArrayList<JSONObject> ratelist) {
        updatedratelist = ratelist;
    }

    RecyclerView recyclerView;

    public void updaterecv(ArrayList<JSONObject> ratelist) {
        ArrayList<JSONObject> list = new ArrayList<>();
        JSONObject object;
        try {
            for (int i = 0; i < ratelist.size(); i++) {
                JSONObject element = ratelist.get(i);
                object = new JSONObject();
                object.put("productid", element.getString("productid"));
                object.put("productname", element.getString("productname"));
                object.put("productqty", element.getString("productqty"));
                object.put("productcat", element.getString("productcat"));
                object.put("productprice", element.getString("productprice").replace(",","/pcs\n₹"));
                list.add(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        recvadapter recvadapter = new recvadapter(context, list, this);
        recyclerView.setAdapter(recvadapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }


    public void handlechangeproduct(JSONObject item) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Dialog dialog1 = new Dialog(context);
        dialog1.setContentView(R.layout.itemdetails);
        dialog1.getWindow().setLayout(width - 100, height - 100);
        dialog1.getWindow().setBackgroundDrawableResource(R.drawable.orderbackground);
        ImageView backbtn = dialog1.findViewById(R.id.backbutton);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });
        dialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                newdata = false;
            }
        });
        EditText name, category, rateiron, ratewashfold, ratewashiron, ratedryclean;
        Button submit,delete;
        name = dialog1.findViewById(R.id.productname);
        category = dialog1.findViewById(R.id.productcat);
        rateiron = dialog1.findViewById(R.id.productrateiron);
        ratewashfold = dialog1.findViewById(R.id.productratewashfold);
        ratewashiron = dialog1.findViewById(R.id.productratewashiro);
        ratedryclean = dialog1.findViewById(R.id.productratedryclean);
        submit = dialog1.findViewById(R.id.submitbtn);
        delete = dialog1.findViewById(R.id.deletebtn);
        productid = "";
        for (JSONObject element : ratelist) {
            try {
                if (element.getString("productid").equals(String.valueOf(item.getString("productid")))) {
                    name.setText(element.getString("productname"));
                    rateiron.setText(element.getString("productprice").split(",")[0]);
                    ratewashfold.setText(element.getString("productprice").split(",")[1]);
                    ratewashiron.setText(element.getString("productprice").split(",")[2]);
                    ratedryclean.setText(element.getString("productprice").split(",")[3]);
                }
                productid = item.getString("productid");
                category.setText(productcat[item.getInt("productcat") - 1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ratedryclean.addTextChangedListener(new Textwatcher(ratedryclean));
        ratewashfold.addTextChangedListener(new Textwatcher(ratewashfold));
        ratewashiron.addTextChangedListener(new Textwatcher(ratewashiron));
        rateiron.addTextChangedListener(new Textwatcher(rateiron));
        DatabaseReference datebase = FirebaseDatabase.getInstance().getReference("rates");
        if(!newdata){
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    datebase.child(productid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseDatabase.getInstance().getReference("rateversion").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    lastindex = Integer.parseInt(snapshot.getValue().toString());
                                    FirebaseDatabase.getInstance().getReference("rateversion").setValue(String.valueOf(lastindex + 1)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(newdata){
                                                FirebaseDatabase.getInstance().getReference("lastprodid").setValue(String.valueOf(Integer.parseInt(productid))).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(mainActivity, "Rate Updated", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                        dialog1.dismiss();
                                                        washiron.performClick();
                                                        newdata = false;
                                                    }
                                                });
                                            }else{
                                                Toast.makeText(mainActivity, "Rate Updated", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                dialog1.dismiss();
                                                washiron.performClick();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                }
            });
        }else{
            delete.setVisibility(View.GONE);
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().equals("") || category.getText().toString().equals("") || ratewashiron.getText().toString().equals("") || ratewashfold.getText().toString().equals("") || rateiron.getText().toString().equals("") || ratedryclean.getText().toString().equals("")) {
                    Toast.makeText(mainActivity, "Wrong/Blank Data", Toast.LENGTH_SHORT).show();
                    return;
                }
                datebase.child(productid).child("productname").setValue(name.getText().toString());
                String str = "";
                str += rateiron.getText().toString();
                str += ",";
                str += ratewashfold.getText().toString();
                str += ",";
                str += ratewashiron.getText().toString();
                str += ",";
                str += ratedryclean.getText().toString();
                datebase.child(productid).child("productprice").setValue(str);
                datebase.child(productid).child("productcat").setValue(productcatselection);
                lastindex = 0;
                FirebaseDatabase.getInstance().getReference("rateversion").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        lastindex = Integer.parseInt(snapshot.getValue().toString());
                        FirebaseDatabase.getInstance().getReference("rateversion").setValue(String.valueOf(lastindex + 1)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(newdata){
                                    FirebaseDatabase.getInstance().getReference("lastprodid").setValue(String.valueOf(Integer.parseInt(productid))).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(mainActivity, "Rate Updated", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                            dialog1.dismiss();
                                            washiron.performClick();
                                            newdata = false;
                                        }
                                    });
                                }else{
                                    Toast.makeText(mainActivity, "Rate Updated", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    dialog1.dismiss();
                                    washiron.performClick();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        dialog1.show();
    }

    public class Textwatcher implements TextWatcher {
        EditText editText;

        public Textwatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length() == 0) {
                editText.setText("0");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}