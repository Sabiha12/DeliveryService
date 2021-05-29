package com.vanillacoder.delivery.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.vanillacoder.delivery.R;
import com.vanillacoder.delivery.activity.DynamicAllActivity;
import com.vanillacoder.delivery.activity.SearchActivity;
import com.vanillacoder.delivery.activity.ServiceAllActivity;
import com.vanillacoder.delivery.activity.SubCategoryActivity;
import com.vanillacoder.delivery.activity.SubCategoyTypeActivity;
import com.vanillacoder.delivery.adepter.BannerAdapter;
import com.vanillacoder.delivery.adepter.CategoryAdapter;
import com.vanillacoder.delivery.adepter.DynamicAdapter;
import com.vanillacoder.delivery.adepter.ServiceAdapter;
import com.vanillacoder.delivery.adepter.UserAdapter;
import com.vanillacoder.delivery.model.CatlistItem;
import com.vanillacoder.delivery.model.DynmaicSectionItem;
import com.vanillacoder.delivery.model.Home;
import com.vanillacoder.delivery.model.ServiceDataItem;
import com.vanillacoder.delivery.model.SubcatDatum;
import com.vanillacoder.delivery.model.SubcatSectionItem;
import com.vanillacoder.delivery.model.User;
import com.vanillacoder.delivery.retrofit.APIClient;
import com.vanillacoder.delivery.retrofit.GetResult;
import com.vanillacoder.delivery.utils.CustPrograssbar;
import com.vanillacoder.delivery.utils.DrawableTextView;
import com.vanillacoder.delivery.utils.SessionManager;
import com.vanillacoder.delivery.utils.Utility;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

import static com.vanillacoder.delivery.utils.SessionManager.about;
import static com.vanillacoder.delivery.utils.SessionManager.contact;
import static com.vanillacoder.delivery.utils.SessionManager.currency;
import static com.vanillacoder.delivery.utils.SessionManager.policy;
import static com.vanillacoder.delivery.utils.SessionManager.terms;
import static com.vanillacoder.delivery.utils.SessionManager.wallet;

public class HomeFragment extends Fragment implements CategoryAdapter.RecyclerTouchListener, ServiceAdapter.RecyclerTouchListener, GetResult.MyListener, DynamicAdapter.RecyclerTouchListener {
    @BindView(R.id.my_recycler_banner)
    RecyclerView recyclerBanner;
    @BindView(R.id.recycler_category)
    RecyclerView recyclerCategory;
    @BindView(R.id.lvl_service)
    LinearLayout lvlService;
    @BindView(R.id.lvl_dynamic)
    LinearLayout lvlDynamic;
    @BindView(R.id.txt_address)
    TextView txtAddress;
    @BindView(R.id.ed_search)
    TextView edSearch;
    @BindView(R.id.recycler_user)
    RecyclerView recyclerUser;
    Unbinder unbinder;

    LinearLayoutManager layoutManager;
    int position;
    Timer timer;
    TimerTask timerTask;
    FusedLocationProviderClient fusedLocationClient;
    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;
    User user;
    boolean isTextViewClicked = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        custPrograssbar = new CustPrograssbar();
        sessionManager = new SessionManager(getActivity());
        user = sessionManager.getUserDetails("");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerBanner.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerUser.setLayoutManager(layoutManager1);

        recyclerCategory.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        recyclerCategory.setItemAnimator(new DefaultItemAnimator());


        getHome();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && Utility.hasGPSDevice(getActivity())) {
            Toast.makeText(getActivity(), "Gps not enabled", Toast.LENGTH_SHORT).show();
            Utility.enableLoc(getActivity());
        }


        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        getCompleteAddressString(location.getLatitude(), location.getLongitude());
                        Log.e("Location ", "-->" + location.getLatitude());
                    }

                });

        //Response to Click Events on the Right Picture
     /*   txtAddress.setDrawableRightClickListener(new DrawableTextView.DrawableRightClickListener() {
            @Override
            public void onDrawableRightClickListener(View view) {
                //Here execute the corresponding logic executed after the right control clicks
            }
        });*/

       /* txtAddress.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    // your action here
                    txtAddress.setMaxLines(2);
                    return event.getRawX() >= (txtAddress.getRight() - txtAddress.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width());
                }
                return false;
            }
        });*/
/*
        txtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isTextViewClicked){
                    //This will shrink textview to 2 lines if it is expanded.
                    txtAddress.setMaxLines(2);
                    isTextViewClicked = false;
                } else {
                    //This will expand the textview if it is of 2 lines
                    txtAddress.setMaxLines(Integer.MAX_VALUE);
                    isTextViewClicked = true;
                }
            }
        });*/

        return view;
    }

    private void getHome() {
        custPrograssbar.prograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().getHome(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");

    }


    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();
            if (callNo.equalsIgnoreCase("1")) {
                Gson gson = new Gson();
                Home home = gson.fromJson(result.toString(), Home.class);
                if (home.getResult().equalsIgnoreCase("true")) {


                    BannerAdapter bannerAdapter = new BannerAdapter(getActivity(), home.getResultData().getBanner());
                    recyclerBanner.setAdapter(bannerAdapter);

                    CategoryAdapter categoryAdapter = new CategoryAdapter(getActivity(), home.getResultData().getCatlist(), this, "single");
                    recyclerCategory.setAdapter(categoryAdapter);

                    sessionManager.setStringData(currency, home.getResultData().getMainData().getCurrency());
                    sessionManager.setStringData(policy, home.getResultData().getMainData().getPolicy());
                    sessionManager.setStringData(about, home.getResultData().getMainData().getAbout());
                    sessionManager.setStringData(terms, home.getResultData().getMainData().getTerms());
                    sessionManager.setStringData(contact, home.getResultData().getMainData().getContact());
                    sessionManager.setStringData(wallet, home.getResultData().getMainData().getWallet());


                    setDynamicList(lvlDynamic, home.getResultData().getDynmaicSection());
                    setServicerList(lvlService, home.getResultData().getSubcatSection());
                    UserAdapter adapter = new UserAdapter(getActivity(), home.getResultData().getTestimonial());
                    recyclerUser.setAdapter(adapter);

                    setbanner();

                }
            }
        } catch (Exception e) {
            Log.e("Error", "-->" + e.toString());

        }
    }

    private void setDynamicList(LinearLayout lnrView, List<DynmaicSectionItem> dataList) {

        lnrView.removeAllViews();
        for (int i = 0; i < dataList.size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.home_dynamic_item, null);
            TextView itemTitle = view.findViewById(R.id.itemTitle);
            TextView itemsubTitle = view.findViewById(R.id.itemsubTitle);
            TextView txtViewall = view.findViewById(R.id.txt_viewall);
            RecyclerView recyclerViewList = view.findViewById(R.id.recycler_view_list);
            int finalI = i;
            txtViewall.setOnClickListener(v -> startActivity(new Intent(getActivity(), DynamicAllActivity.class).putParcelableArrayListExtra("ListExtra", dataList.get(finalI).getServiceData())));
            itemTitle.setText("" + dataList.get(i).getSecTitle());
            itemsubTitle.setText("" + dataList.get(i).getSecSubtitle());
            DynamicAdapter itemAdp = new DynamicAdapter(dataList.get(i).getServiceData(), this, "home");
            recyclerViewList.setLayoutManager(new GridLayoutManager(getActivity(), 2));


            recyclerViewList.setAdapter(itemAdp);
            lnrView.addView(view);
        }
    }

    private void setServicerList(LinearLayout lnrView, List<SubcatSectionItem> dataList) {

        lnrView.removeAllViews();
        for (int i = 0; i < dataList.size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.home_service_item, null);
            TextView itemTitle = view.findViewById(R.id.itemTitle);
            TextView itemsubTitle = view.findViewById(R.id.itemsubTitle);
            TextView txtViewall = view.findViewById(R.id.txt_viewall);
            RecyclerView recyclerViewList = view.findViewById(R.id.recycler_view_list);
            int finalI = i;
            txtViewall.setOnClickListener(v -> startActivity(new Intent(getActivity(), ServiceAllActivity.class).putParcelableArrayListExtra("ListExtra", dataList.get(finalI).getSubcatData())));
            itemTitle.setText("" + dataList.get(i).getCatName());
            itemsubTitle.setText("" + dataList.get(i).getCatSubtitle());


            ServiceAdapter itemAdp = new ServiceAdapter(dataList.get(i).getSubcatData(), this, "home");
            recyclerViewList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            recyclerViewList.setAdapter(itemAdp);
            lnrView.addView(view);
        }
    }


    @Override
    public void onClickCategoryItem(CatlistItem catlistItem, int position) {

        if (catlistItem.getTotalSubcat() == 0) {
            startActivity(new Intent(getActivity(), SubCategoryActivity.class)
                    .putExtra("vurl", catlistItem.getCatVideo())
                    .putExtra("name", catlistItem.getCatName())
                    .putExtra("named", catlistItem.getCatSubtitle())
                    .putExtra("cid", catlistItem.getCatId())
                    .putExtra("sid", "0"));

        } else {
            startActivity(new Intent(getActivity(), SubCategoyTypeActivity.class)
                    .putExtra("cid", catlistItem.getCatId()));

        }
    }

    @Override
    public void onClickServiceItem(SubcatDatum dataItem, int position) {

        startActivity(new Intent(getActivity(), SubCategoryActivity.class)
                .putExtra("vurl", dataItem.getVideo())
                .putExtra("name", dataItem.getTitle())
                .putExtra("named", dataItem.getSubtitle())
                .putExtra("cid", dataItem.getCatid())
                .putExtra("sid", dataItem.getSubcatId()));


    }

    @Override
    public void onClickDynamicItem(ServiceDataItem dataItem, int position) {

        startActivity(new Intent(getActivity(), SubCategoryActivity.class)
                .putExtra("vurl", dataItem.getVideo())
                .putExtra("name", dataItem.getCatTitle())
                .putExtra("named", dataItem.getCatSubtitle())
                .putExtra("cid", dataItem.getCatId())
                .putExtra("sid", "0"));

    }

    private String getCompleteAddressString(double latitude, double longitude) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.e("My Current address", strReturnedAddress.toString());
            } else {
                Log.e("My Current address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("My Current address", "Canont get Address!");
        }
        txtAddress.setText(strAdd);

        return strAdd;
    }

    private void setbanner() {
        position = 0;
        recyclerBanner.scrollToPosition(position);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerBanner);
        recyclerBanner.smoothScrollBy(5, 0);

        recyclerBanner.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == 1) {
                    stopAutoScrollBanner();
                } else if (newState == 0) {
                    position = layoutManager.findFirstCompletelyVisibleItemPosition();
                    runAutoScrollBanner();
                }
            }
        });
    }

    private void stopAutoScrollBanner() {
        if (timer != null && timerTask != null) {
            timerTask.cancel();
            timer.cancel();
            timer = null;
            timerTask = null;
            position = layoutManager.findFirstCompletelyVisibleItemPosition();
        }
    }

    private void runAutoScrollBanner() {
        if (timer == null && timerTask == null) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (position == recyclerBanner.getAdapter().getItemCount() - 1) {
                            position = 0;
                            recyclerBanner.smoothScrollBy(5, 0);
                            recyclerBanner.smoothScrollToPosition(position);
                        } else {
                            position++;
                            recyclerBanner.smoothScrollToPosition(position);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

            };
            timer.schedule(timerTask, 4000, 4000);
        }

    }

    @OnClick({R.id.ed_search})
    public void onClick(View view) {
        if (view.getId() == R.id.ed_search) {
            startActivity(new Intent(getActivity(), SearchActivity.class));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}