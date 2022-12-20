package com.vinitagarwal.DemoLaundry;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class rates {
    //TODO: get rate from firebase and convert it to thi form
    ArrayList<JSONObject> list = new ArrayList<>();
    Home home;
    cart cart;
    int lastorder = 0;

    public rates(Home home) {
        this.home = home;
    }

    public rates(com.vinitagarwal.DemoLaundry.cart cart) {
        this.cart = cart;
    }

    SharedPreferences sharedPreferencesrate;
    SharedPreferences.Editor editorrate;
    JSONObject rates;

    void initsharedpref(Home home) {
        sharedPreferencesrate = home.getActivity().getSharedPreferences("rates", Context.MODE_PRIVATE);
        editorrate = sharedPreferencesrate.edit();
    }

    void initsharedpref(cart cart) {
        sharedPreferencesrate = cart.getActivity().getSharedPreferences("rates", Context.MODE_PRIVATE);
        editorrate = sharedPreferencesrate.edit();
        DatabaseReference ratever = FirebaseDatabase.getInstance().getReference("lastorder");
        ratever.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cart.lastorder = Integer.parseInt(snapshot.getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    int getLastorder() {

        return lastorder;
    }

    void initialize() throws JSONException {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (cart != null) {
            initsharedpref(cart);
        }
        if (home != null) {
            initsharedpref(home);
        }
        try {
            DatabaseReference ratever = database.getReference("rateversion");
            ratever.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                    if (snapshot1.getValue().toString().equalsIgnoreCase(sharedPreferencesrate.getString("rateversion", ""))) {
                        //if(false){
                        Gson gson = new Gson();
                        list = new ArrayList<>();
                        Type type = new TypeToken<ArrayList<JSONObject>>() {
                        }.getType();
                        list = gson.fromJson(sharedPreferencesrate.getString("ratelist", "[]"), type);
                        for (JSONObject element : list) {
                            try {
                                Log.d("test", "onDataChange: " + cartlist().getJSONObject(element.getString("productid")).getString("productqty"));
                                element.put("productqty", cartlist().getJSONObject(element.getString("productid")).getString("productqty"));
                            } catch (Exception e) {
                                try {
                                    element.put("productqty", "0");
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                        home.updategotrate();
                        manageqty();
                    } else {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference dbref = database.getReference("rates");
                        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    rates = new JSONObject();
                                    try {
                                        rates.put("productid", ds.getKey());
                                        try {
                                            Log.d("test", "onDataChange: " + cartlist().getJSONObject(ds.getKey()).getString("productqty"));
                                            rates.put("productqty", cartlist().getJSONObject(ds.getKey()).getString("productqty"));
                                        } catch (Exception e) {
                                            rates.put("productqty", "0");
                                        }
                                        for (DataSnapshot subds : snapshot.child(ds.getKey()).getChildren()) {
                                            rates.put(subds.getKey(), subds.getValue().toString());
                                        }
                                        list.add(rates);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Gson gson = new Gson();
                                editorrate.putString("ratelist", gson.toJson(list));
                                editorrate.putString("rateversion", snapshot1.getValue().toString());
                                editorrate.commit();
                                home.updategotrate();
                                manageqty();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
        }
    }

    String TAG = "test";

    ArrayList<JSONObject> manageqty() {

        for (JSONObject rates : list) {
            try {
                rates.put("productqty", cartlist().getJSONObject(String.valueOf(home.catselection)).getJSONObject(rates.getString("productid")).getString("productqty"));
            } catch (JSONException e) {
                try {
                    rates.put("productqty", "0");
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return list;
    }

    public JSONObject cartlist() {
        JSONObject cartlist = null;
        try {
            cartlist = new JSONObject(sharedPreferencesrate.getString("cartlist", "{}"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cartlist;
    }

    void deletecart(JSONObject item) {
        JSONObject cartlist = null;
        try {
            cartlist = new JSONObject(sharedPreferencesrate.getString("cartlist", "{}"));
            JSONObject temp;
            try {
                temp = cartlist.getJSONObject(item.getString("catselection"));
            } catch (Exception e) {
                temp = new JSONObject();
            }
            cartlist.getJSONObject(item.getString("catselection")).remove(item.getString("productid"));
            editorrate.putString("cartlist", cartlist.toString());
            editorrate.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void emptycart() {
        editorrate.putString("cartlist", "{}");
        editorrate.commit();
    }

    void updatecart(JSONObject item) {
        try {
            JSONObject cartlist = new JSONObject(sharedPreferencesrate.getString("cartlist", "{}"));
            JSONObject temp;
            try {
                temp = cartlist.getJSONObject(item.getString("catselection"));
            } catch (Exception e) {
                temp = new JSONObject();
            }
            temp.put(item.getString("productid"), item);
            if (item.getString("productqty").equals("0")) {
                Log.d(TAG, "updatecart deleted: ");
                cartlist.getJSONObject(item.getString("catselection")).remove(item.getString("productid"));
            } else {
                cartlist.put(item.getString("catselection"), temp);
            }
            editorrate.putString("cartlist", cartlist.toString());
            editorrate.commit();
            Log.d("test", "updatecartfinal: " + sharedPreferencesrate.getString("cartlist", "{}"));

        } catch (JSONException e) {
            Log.d(TAG, "updatecarterr: " + e.toString());
            e.printStackTrace();
        }
    }

    String getdeliverydetails() throws JSONException {
        String temp = "";
        temp = sharedPreferencesrate.getString("delivery", "");
        return temp.trim();
    }

    String getpickupdetails() throws JSONException {
        String temp = "";
        temp = sharedPreferencesrate.getString("pickup", "");
        return temp.trim();
    }

    void changedeliverydetails(String text) {
        editorrate.putString("delivery", text.split("@")[0].trim());
        editorrate.putString("pickup", text.split("@")[1].trim());
        editorrate.commit();
        Log.d(TAG, "changedeliverydetails: "+text);
        Uri uri = Uri.parse(text);
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
        FirebaseAuth.getInstance().getCurrentUser().updateProfile(request);
    }

}
