package com.vanillacoder.delivery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vanillacoder.delivery.R;
import com.vanillacoder.delivery.adepter.ServiceAdapter;
import com.vanillacoder.delivery.model.SubcatDatum;
import com.vanillacoder.delivery.model.TypeSubCat;
import com.vanillacoder.delivery.model.User;
import com.vanillacoder.delivery.retrofit.APIClient;
import com.vanillacoder.delivery.retrofit.GetResult;
import com.vanillacoder.delivery.utils.CustPrograssbar;
import com.vanillacoder.delivery.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class SubCategoyTypeActivity extends BasicActivity implements GetResult.MyListener, ServiceAdapter.RecyclerTouchListener {

    @BindView(R.id.recycler_service)
    RecyclerView recyclerService;
    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;
    User user;
    String cid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcategorytype);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("View All");
        cid =getIntent().getStringExtra("cid");

        custPrograssbar=new CustPrograssbar();
        sessionManager=new SessionManager(this);
        user=sessionManager.getUserDetails("");
        getData();

    }

    private void getData() {
        custPrograssbar.prograssCreate(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
            jsonObject.put("cid", cid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().sSubList(bodyRequest);
        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");

    }
    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            custPrograssbar.closePrograssBar();

            if(callNo.equalsIgnoreCase("1")){
                Gson gson=new Gson();
                TypeSubCat subCat=gson.fromJson(result.toString(),TypeSubCat.class);
                ServiceAdapter itemAdp = new ServiceAdapter( subCat.getSubcatdata(), this, "viewall");
                recyclerService.setLayoutManager(new GridLayoutManager(this, 1));
                recyclerService.setAdapter(itemAdp);
            }
        }catch (Exception e){
            Log.e("Error", "" + e.toString());

        }
    }


    @Override
    public void onClickServiceItem(SubcatDatum dataItem, int position) {
        startActivity(new Intent(this, SubCategoryActivity.class)
                .putExtra("vurl",dataItem.getVideo())
                .putExtra("name", dataItem.getTitle())
                .putExtra("named", dataItem.getSubtitle())
                .putExtra("cid", cid)
                .putExtra("sid",dataItem.getSubcatId()));
    }
}