package com.vanillacoder.delivery.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.vanillacoder.delivery.R;
import com.vanillacoder.delivery.model.PaymentItem;
import com.vanillacoder.delivery.utils.Utility;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


public class PaypalActivity extends AppCompatActivity {
    PaymentItem paymentItem;
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;
    PayPalConfiguration config;
    double amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);
        paymentItem = (PaymentItem) getIntent().getSerializableExtra("detail");

        amount = getIntent().getDoubleExtra("amount", 0.0);
        String configEnvironment;
        List<String> elephantList = Arrays.asList(paymentItem.getmAttributes().split(","));
        if (elephantList.size() == 2) {
            if (elephantList.get(1).equalsIgnoreCase("1")) {
                configEnvironment = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
            } else {
                configEnvironment = PayPalConfiguration.ENVIRONMENT_SANDBOX;
            }
            config = new PayPalConfiguration()
                    .environment(configEnvironment)
                    .clientId(elephantList.get(0))
                    .merchantName(getResources().getString(R.string.app_name));

            Intent intent = new Intent(this, PayPalService.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            startService(intent);
            onBuyPressed();
        } else {
            finish();
        }

    }

    public void onBuyPressed() {
        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);

    }

    private PayPalPayment getThingToBuy(String paymentIntent) {
        return new PayPalPayment(new BigDecimal(amount), "USD", getResources().getString(R.string.app_name),
                paymentIntent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.e("TAG", confirm.toJSONObject().toString(4));
                        Log.e("TAG", confirm.getPayment().toJSONObject().toString(4));

                        JSONObject jsonDetails = new JSONObject(confirm.toJSONObject().toString(4));
                        showDetails(jsonDetails.getJSONObject("response"));

                    } catch (JSONException e) {
                        Log.e("TAG", "failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                finish();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.e(
                        "TAG",
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {

                    displayResultText("Future Payment code received from PayPal");
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                finish();
                Log.e("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.e(
                        "FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    displayResultText("Profile Sharing code received from PayPal");

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("ProfileSharingExample", "The user canceled.");
                finish();
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.e(
                        "ProfileSharingExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }


    protected void displayResultText(String result) {
        Toast.makeText(
                getApplicationContext(),
                result, Toast.LENGTH_LONG)
                .show();
    }

    private void showDetails(JSONObject jsonDetails) throws JSONException {
        if (jsonDetails.getString("state").equalsIgnoreCase("approved")) {
            Utility.paymentsucsses = 1;
            Utility.tragectionID = jsonDetails.getString("id");
            finish();
        }
    }
}
