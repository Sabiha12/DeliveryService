package com.vanillacoder.delivery.activity;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;


import com.vanillacoder.delivery.R;
import com.vanillacoder.delivery.utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.vanillacoder.delivery.utils.SessionManager.policy;


public class PrivecyPolicyActivity extends BasicActivity {

    @BindView(R.id.txt_privacy)
    TextView txtPrivacy;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privecypolicy);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtPrivacy.setText(Html.fromHtml(sessionManager.getStringData(policy), Html.FROM_HTML_MODE_COMPACT));
        } else {
            txtPrivacy.setText(Html.fromHtml(sessionManager.getStringData(policy)));
        }
    }
}
