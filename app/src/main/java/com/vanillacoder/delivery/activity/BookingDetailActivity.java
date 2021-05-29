package com.vanillacoder.delivery.activity;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vanillacoder.delivery.R;
import com.vanillacoder.delivery.model.AddOnDataItem;
import com.vanillacoder.delivery.model.CustomerorderlistItem;
import com.vanillacoder.delivery.model.OrderProductDataItem;
import com.vanillacoder.delivery.utils.SessionManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookingDetailActivity extends BasicActivity {

    @BindView(R.id.txt_orderid)
    TextView txtOrderid;
    @BindView(R.id.txt_status)
    TextView txtStatus;
    @BindView(R.id.txt_data)
    TextView txtData;
    @BindView(R.id.txt_subtotal)
    TextView txtSubtotal;
    @BindView(R.id.txt_addon)
    TextView txtAddon;
    @BindView(R.id.txt_total)
    TextView txtTotal;
    @BindView(R.id.txt_address)
    TextView txtAddress;
    @BindView(R.id.txt_paymentmethod)
    TextView txtPaymentmethod;
    @BindView(R.id.recycleview_service)
    RecyclerView recycleviewService;
    @BindView(R.id.lvl_addon)
    LinearLayout lvlAddon;
    @BindView(R.id.lvl_wallet)
    LinearLayout lvlWallet;
    @BindView(R.id.txt_wallet)
    TextView txtWallet;
    CustomerorderlistItem item;
    ArrayList<OrderProductDataItem> orderProductData;
    ArrayList<AddOnDataItem> addOnData;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(this);
        item = getIntent().getParcelableExtra("myclass");
        addOnData = getIntent().getParcelableArrayListExtra("addon");
        orderProductData = getIntent().getParcelableArrayListExtra("itemlist");

        txtOrderid.setText("" + item.getOrderId());
        txtStatus.setText("" + item.getOrderStatus());
        txtData.setText("" + item.getOrderDateslot() + " " + item.getOrderTime());
        txtSubtotal.setText(sessionManager.getStringData(SessionManager.currency) + item.getOrderSubTotal());
        txtAddon.setText(sessionManager.getStringData(SessionManager.currency) + item.getAddTotal());
        txtTotal.setText(sessionManager.getStringData(SessionManager.currency) + item.getOrderTotal());

        txtPaymentmethod.setText("" + item.getPMethodName());
        txtAddress.setText("" + item.getCustomerAddress());
        if (item.getWallet().equalsIgnoreCase("0")) {
            lvlWallet.setVisibility(View.GONE);
        } else {
            lvlWallet.setVisibility(View.VISIBLE);
            txtWallet.setText(sessionManager.getStringData(SessionManager.currency)+item.getWallet());
        }
        setAddonList(lvlAddon, addOnData);
        recycleviewService.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter cartAdapter = new cartAdapter(this, orderProductData);
        recycleviewService.setAdapter(cartAdapter);

    }


    private void setAddonList(LinearLayout lnrView, List<AddOnDataItem> dataList) {

        lnrView.removeAllViews();

        for (int i = 0; i < dataList.size(); i++) {
            if (!dataList.get(i).getPrice().equalsIgnoreCase("0")) {
                LayoutInflater inflater = LayoutInflater.from(this);
                View view = inflater.inflate(R.layout.item_adon, null);
                TextView itemTitle = view.findViewById(R.id.txt_title);
                TextView txtPrice = view.findViewById(R.id.txt_price);
                itemTitle.setText("" + dataList.get(i).getTitle());
                txtPrice.setText(sessionManager.getStringData(SessionManager.currency) + dataList.get(i).getPrice());
                lnrView.addView(view);
            }

        }

    }


    public class cartAdapter extends RecyclerView.Adapter<cartAdapter.CartHolder> {
        private Context context;
        private List<OrderProductDataItem> mBanner;
        SessionManager sessionManager;


        public cartAdapter(Context context, List<OrderProductDataItem> mBanner) {
            this.context = context;
            this.mBanner = mBanner;
            sessionManager = new SessionManager(context);

        }

        @NonNull
        @Override
        public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_service, parent, false);
            return new CartHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CartHolder holder, int position) {
            OrderProductDataItem item = mBanner.get(position);
            holder.txtTitle.setText("" + item.getProductName());
            holder.txtQty.setText("Qty " + item.getProductQuantity());
            double res = (item.getProductPrice() / 100.0f) * item.getProductDiscount();
            double pp = item.getProductPrice() - res;
            holder.txtPriced.setText(sessionManager.getStringData(SessionManager.currency) + new DecimalFormat("##.##").format(pp));
            holder.txtPrice.setText(sessionManager.getStringData(SessionManager.currency) + item.getProductPrice());
            holder.txtPrice.setPaintFlags(holder.txtPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        }

        @Override
        public int getItemCount() {
            return mBanner.size();

        }

        public class CartHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.txt_title)
            TextView txtTitle;
            @BindView(R.id.txt_qty)
            TextView txtQty;

            @BindView(R.id.txt_price)
            TextView txtPrice;
            @BindView(R.id.txt_priced)
            TextView txtPriced;

            public CartHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }


    }
}