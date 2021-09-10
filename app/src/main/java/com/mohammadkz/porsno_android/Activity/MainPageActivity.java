package com.mohammadkz.porsno_android.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.mohammadkz.porsno_android.Fragment.AboutUsFragment;
import com.mohammadkz.porsno_android.Fragment.HistoryFragment;
import com.mohammadkz.porsno_android.Fragment.MainFragment;
import com.mohammadkz.porsno_android.Fragment.ProfileFragment;
import com.mohammadkz.porsno_android.Fragment.ReBuyFragment;
import com.mohammadkz.porsno_android.Model.User;
import com.mohammadkz.porsno_android.R;
import com.mohammadkz.porsno_android.StaticFun;
import com.mohammadkz.porsno_android.util.IabHelper;
import com.mohammadkz.porsno_android.util.IabResult;
import com.mohammadkz.porsno_android.util.Purchase;

import org.json.JSONObject;

import java.util.Random;

import es.dmoral.toasty.Toasty;

public class MainPageActivity extends AppCompatActivity {

    DrawerLayout drawer_layout;
    MaterialToolbar topAppBar;
    NavigationView nav_view;

    TextView name, phoneNumber;
    View header;
    User user;

    // buy part
    int trackingCode;
    String SKU;

    // bazar
    static final String TAG = "test";

    // SKUs for our products: the premium upgrade (non-consumable)
    static final String SKU_PREMIUM = "";

    // Does the user have the premium upgrade?
    boolean mIsPremium = false;

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 1;

    // The helper object
    IabHelper mHelper;
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {

            String base64EncodedPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwCn4F3QKkRgV3LSOJQV6fPGb6tsys7m0HPQknNgeBVcaSE2/izyT0RXY60koR6W7xqNn8jc1Ed/6+FJM5xYA8rHPRKrEMfONyAo+xr2ggrrfKh+EPCwUK5rh1YSt4ELov4lrB4oDUKcB86QZYxPVdZMF5Lo/XBlLCMHZrtnSFiDvlhzeBreUmWyKd9vcO5fPbUUt/DBbXYtXKZAH8e9FL+6iA/A00NgD9zisgDwY3sCAwEAAQ==";

            mHelper = new IabHelper(MainPageActivity.this, base64EncodedPublicKey);

            Log.d(TAG, "Starting setup.");
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                @Override
                public void onIabSetupFinished(IabResult result) {
                    Log.d(TAG, "Setup finished.");

                    if (!result.isSuccess()) {
                        // Oh noes, there was a problem.
                        Log.d(TAG, "Problem setting up In-app Billing: " + result);
                    }
                    // Hooray, IAB is fully set up!
                    //mHelper.queryInventoryAsync(mGotInventoryListener);
                }

            });

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main_page);

            initViews();
            getDate();
            controllerViews();
            startFragment();

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_layout, R.string.openDrawerContentRes, R.string.closeDrawerContentRes);
            drawer_layout.addDrawerListener(toggle);
            toggle.syncState();
        } catch (Exception e) {
            Toasty.error(getApplicationContext(), "متاسفانه در دریافت اطلاعات با مشکل مواجه شدیم", Toasty.LENGTH_LONG, true).show();
            StaticFun.setLog((user == null) ? "-"
                    : (user.getPn().length() > 0 ? user.getPn() : "-"), e.getMessage().toString(), "main page Activity - create");
        }

    }

    private void initViews() {
        drawer_layout = findViewById(R.id.drawer_layout);
        topAppBar = findViewById(R.id.topAppBar);
        nav_view = findViewById(R.id.nav_view);

        header = nav_view.getHeaderView(0);
        name = header.findViewById(R.id.user_name);
        phoneNumber = header.findViewById(R.id.phoneNumber);

    }

    private void controllerViews() {
        topAppBar.setNavigationOnClickListener(v -> {

            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START);
            } else {
                drawer_layout.openDrawer(GravityCompat.START);
            }

        });

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.main:
                        nav_view.setCheckedItem(R.id.main);
                        startFragment();
                        break;
                    case R.id.newQuestion:
                        Intent intent = new Intent(getApplicationContext(), NewQuestionActivity.class);
                        Gson gson = new Gson();
                        String a = gson.toJson(user);
                        intent.putExtra("userInfo", a);//user.getID()
                        startActivity(intent);
                        break;
                    case R.id.account:
                        setTopAppBar("حساب کاربری");
                        nav_view.setCheckedItem(R.id.account);
                        ProfileFragment profileFragment = new ProfileFragment(user);
                        fragmentTransaction.replace(R.id.frameLayout, profileFragment).commit();
                        break;
                    case R.id.reBuy:
                        setTopAppBar("تمدید حساب");
                        nav_view.setCheckedItem(R.id.reBuy);
                        ReBuyFragment reBuyFragment = new ReBuyFragment(user);
                        fragmentTransaction.replace(R.id.frameLayout, reBuyFragment).commit();
                        break;
                    case R.id.history:
                        setTopAppBar("تاریخچه");
                        nav_view.setCheckedItem(R.id.history);
                        HistoryFragment historyFragment = new HistoryFragment(user);
                        fragmentTransaction.replace(R.id.frameLayout, historyFragment).commit();
                        break;
                    case R.id.about_us:
                        setTopAppBar("راه ارتباطی");
                        nav_view.setCheckedItem(R.id.about_us);
                        AboutUsFragment contactUsFragment = new AboutUsFragment();
                        fragmentTransaction.replace(R.id.frameLayout, contactUsFragment).commit();
                        break;
                    case R.id.exit:
                        removeSharedPreferences();
                        finish();
                        break;
                }

                drawer_layout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Callback for when a purchase is finished
        mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

                // if we were disposed of in the meantime, quit.
                if (mHelper == null) return;

                if (result.isFailure()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("result-Purchase", "fail");
                    bundle.putString("package-code", SKU);
                    bundle.putString("tracking-code", String.valueOf(trackingCode));
                    getSupportFragmentManager().setFragmentResult("result", bundle);
                    return;
                }
                if (!verifyDeveloperPayload(purchase)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("result-Purchase", "not-confirmed");
                    bundle.putString("package-code", SKU);
                    bundle.putString("order-id", purchase.getOrderId());
                    bundle.putString("token", purchase.getToken());
                    bundle.putString("tracking-code", String.valueOf(trackingCode));
                    getSupportFragmentManager().setFragmentResult("result", bundle);
                    return;
                }


                Log.d(TAG, "Purchase successful.");

                if (purchase.getSku().equals(SKU)) {
                    mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                }
            }
        };

        // Called when consumption is complete
        mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
            public void onConsumeFinished(Purchase purchase, IabResult result) {
                Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

                // if we were disposed of in the meantime, quit.
                if (mHelper == null) return;

                // We know this is the "gas" sku because it's the only one we consume,
                // so we don't check which sku was consumed. If you have more than one
                // sku, you probably should check...
                if (result.isSuccess()) {
                    // successfully consumed, so we apply the effects of the item in our
                    // game world's logic, which in our case means filling the gas tank a bit
                    Log.d(TAG, "Consumption successful. Provisioning.");

                    Bundle bundle = new Bundle();
                    bundle.putString("result-Purchase", "done");
                    bundle.putString("package-code", SKU);
                    bundle.putString("order-id", purchase.getOrderId());
                    bundle.putString("order-token", purchase.getToken());
                    bundle.putString("tracking-code", String.valueOf(trackingCode));
                    getSupportFragmentManager().setFragmentResult("result", bundle);

                } else {
                    Log.e("test-1", "11");
                }
                Log.d(TAG, "End consumption flow.");
            }
        };

    }

    private void startFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        MainFragment mainFragment = new MainFragment(user);
        fragmentTransaction.replace(R.id.frameLayout, mainFragment).commit();
    }

    // get data from sign up page(intent)
    private void getDate() {
        String data = getIntent().getStringExtra("userInfo");
        Log.e("user", " " + data);
        if (data != null)
            try {
                JSONObject jsonObject = new JSONObject(data);

                user = new User();
                user.setID(jsonObject.getString("ID"));
                user.setPn(jsonObject.getString("pn"));
                user.setName(jsonObject.getString("name"));
                user.setCreatedTime(jsonObject.getString("createdTime"));
                user.setEndTime(jsonObject.getString("endTime"));
                user.setQuestionRemaining(jsonObject.getString("questionRemaining"));

                if (jsonObject.getString("birthdayDate") != null) {
                    user.setBirthdayDate(jsonObject.getString("birthdayDate"));
                }

                if (jsonObject.getString("accountLevel").equals("Bronze")) {
                    user.setAccountLevel(StaticFun.account.Bronze);
                } else if (jsonObject.getString("accountLevel").equals("Steel")) {
                    user.setAccountLevel(StaticFun.account.Steel);
                } else if (jsonObject.getString("accountLevel").equals("Gold")) {
                    user.setAccountLevel(StaticFun.account.Gold);
                } else if (jsonObject.getString("accountLevel").equals("Diamond")) {
                    user.setAccountLevel(StaticFun.account.Diamond);
                }

                setHeaderValue();

            } catch (Exception e) {
                e.printStackTrace();
                Toasty.error(getApplicationContext(), "متاسفانه در دریافت اطلاعات با مشکل مواجه شدیم", Toasty.LENGTH_LONG, true).show();
                StaticFun.setLog((user == null) ? "-"
                        : (user.getPn().length() > 0 ? user.getPn() : "-"), e.getMessage().toString(), "main page Activity - get data");
            }
    }

    private void setHeaderValue() {
        name.setText(user.getName().toString());
        phoneNumber.setText(user.getPn().toString());
    }

    public void updateUser(User user) {
        name.setText(user.getName().toString());
        this.user = user;
    }

    public void setDrawerSelect(int id) {
        nav_view.setCheckedItem(id);
    }

    private void removeSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences("userLogin_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public void setTopAppBar(String name) {
        topAppBar.setTitle(name);
    }

    public void buy(String code) {
        trackingCode = generateTrackingCode();
        SKU = code;
        mHelper.launchPurchaseFlow(MainPageActivity.this, code, trackingCode, mPurchaseFinishedListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            if (requestCode == trackingCode) {
                super.onActivityResult(requestCode, resultCode, data);
            }
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            Log.d("qqqqqqqq", "onActivityResult handled by IABUtil.");
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    /**
     * Verifies the developer payload of a purchase.
     */
    boolean verifyDeveloperPayload(Purchase p) { ///
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    private int generateTrackingCode() {
        // It will generate 4 digit random Number.
        // from 0 to 9999
        String number = "";
        while (number.length() != 4) {
            Random rnd = new Random();
            number = Integer.toString(rnd.nextInt(9999));
        }
        Toast.makeText(getApplicationContext(), number, Toast.LENGTH_LONG).show();
        // this will convert any number sequence into 6 character.

        return Integer.valueOf(String.format("%04d", Integer.parseInt(number)));
    }
}