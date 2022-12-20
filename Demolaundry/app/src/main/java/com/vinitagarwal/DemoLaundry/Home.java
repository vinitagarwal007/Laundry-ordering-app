package com.vinitagarwal.DemoLaundry;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Home extends Fragment {
    MainActivity mainActivity;
    boolean gotratelist = false;

    public Home(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    Context context;
    CardView washiron, washfold, dryclean, iron;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public int catselection;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rates = new rates(this);
        try {
            rates.initialize();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        washfold = view.findViewById(R.id.washfoldcard);
        washiron = view.findViewById(R.id.washironcard);
        dryclean = view.findViewById(R.id.drycleancard);
        iron = view.findViewById(R.id.ironcard);
        washfold.setOnClickListener(cardclick);
        washiron.setOnClickListener(cardclick);
        dryclean.setOnClickListener(cardclick);
        iron.setOnClickListener(cardclick);
    }

    View.OnClickListener cardclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(gotratelist)
            {
                switch (view.getId()) {
                    case R.id.washironcard:
                        catselection = 2;
                        break;
                    case R.id.washfoldcard:
                        catselection = 1;
                        break;
                    case R.id.ironcard:
                        catselection = 0;
                        break;
                    case R.id.drycleancard:
                        catselection = 3;
                        break;
                }
                try {
                    handleorder();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(context, "Getting List\nTry Again..", Toast.LENGTH_SHORT).show();
            }
        }
    };
    Dialog dialog;
    rates rates;

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
        menbtn.setOnClickListener(catbtnselection);
        femalebtn.setOnClickListener(catbtnselection);
        householdbtn.setOnClickListener(catbtnselection);
        woolenbtn.setOnClickListener(catbtnselection);
        menbtn.performClick();
        dialog.show();
    }

    Button menbtn;
    Button femalebtn;
    Button householdbtn;
    Button woolenbtn;
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
        updatedratelist = rates.manageqty();
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
    ArrayList<JSONObject> updatedratelist;
    
    public void updategotrate(){
        gotratelist = true;
    }
    public void updatelist(ArrayList<JSONObject> ratelist) {
        updatedratelist = ratelist;
    }

    public void updaterecv(ArrayList<JSONObject> ratelist) {
        RecyclerView recyclerView = dialog.findViewById(R.id.orderrecv);
        ArrayList<JSONObject> list = new ArrayList<>();
        JSONObject object;
        try {
            for (int i = 0; i < ratelist.size(); i++) {
                JSONObject element = ratelist.get(i);
                object = new JSONObject();
                object.put("productid", element.getString("productid"));
                object.put("productname", element.getString("productname"));
                object.put("productqty", element.getString("productqty"));
                object.put("productcat",element.getString("productcat"));
                object.put("productprice", element.getString("productprice").split(",")[catselection]);
                if(Integer.parseInt(element.getString("productprice").split(",")[catselection]) == 0) continue;
                list.add(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        recvadapter recvadapter = new recvadapter(context, list,this);
        recyclerView.setAdapter(recvadapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }
}