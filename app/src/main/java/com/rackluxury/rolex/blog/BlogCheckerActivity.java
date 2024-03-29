package com.rackluxury.rolex.blog;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rackluxury.rolex.R;
import com.rackluxury.rolex.activities.HomeActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import es.dmoral.toasty.Toasty;

public class BlogCheckerActivity extends AppCompatActivity implements PurchasesUpdatedListener {


    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private SharedPreferences prefs;
    private TextView people;
    private TextView purchasesRemaining;


    private BillingClient billingClient;
    private final List<String> skulist = new ArrayList<>();
    private final String categories = "blog_checker";
    private TextView timer;
    private final String TAG = "Main";
    private SharedPreferences coins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_checker);

        people = findViewById(R.id.peopleNumBlogChecker);
        purchasesRemaining = findViewById(R.id.purchaseNumBlogChecker);

        int valPurc = 6;
        purchasesRemaining.setText(Integer.toString(valPurc));

        Random random = new Random();
        int valPeop = random.nextInt(500); // save random number in an integer variable
        people.setText(Integer.toString(valPeop));

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        coins = getSharedPreferences("Rewards", MODE_PRIVATE);

        timer = findViewById(R.id.tvTimerBlog);
        Intent intent = new Intent(this, BroadcastServiceBlog.class);
        startService(intent);




        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("blogCheckerFirst", true);
        if (firstStart) {
            firstDialogue();
        }

        blogCheckerFunctionality();



    }
    private final BroadcastReceiver broadcastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent);
        }
    };
    @Override
    protected void onStop(){
        try {
            unregisterReceiver(broadcastReciever);
        }catch(Exception e){

        }
        super.onStop();
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(broadcastReciever);

    }
    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver(broadcastReciever,new IntentFilter(BroadcastServiceBlog.COUNTDOWN_BR));

    }
    @Override
    protected void onDestroy(){
        stopService(new Intent(this, BroadcastServiceBlog.class));
        super.onDestroy();
    }
    private void updateGUI(Intent intent){
        if(intent.getExtras() != null){
            long millisUntilFinished = intent.getLongExtra("countdownBlog",300000);

            timer.setText(Long.toString(millisUntilFinished/1000));
            SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(),MODE_PRIVATE);
            sharedPreferences.edit().putLong("timeBlog",millisUntilFinished).apply();
        }
    }
    private void firstDialogue(){
        LayoutInflater inflater = LayoutInflater.from(BlogCheckerActivity.this);
        View viewInformation = inflater.inflate(R.layout.alert_dialog_purchase_information, null);
        Button acceptButton = viewInformation.findViewById(R.id.btnOkAlertPurchaseInformation);
        final AlertDialog alertDialogInformation = new AlertDialog.Builder(BlogCheckerActivity.this)
                .setView(viewInformation)
                .show();

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogInformation.dismiss();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("blogCheckerFirst", false);
                editor.apply();

            }
        });
    }


    private void blogCheckerFunctionality() {
        Toolbar toolbar = findViewById(R.id.toolbarBlogCheckerActivity);
        Button buttonBlogChecker = findViewById(R.id.btnBlogChecker);


        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        billingClient = BillingClient.newBuilder(BlogCheckerActivity.this).enablePendingPurchases().setListener(new PurchasesUpdatedListener() {
            @Override
            //This method starts when user buys a categories
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                if (list != null && billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    for (Purchase purchase : list) {
                        handlePurchase(purchase);
                    }
                } else {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                        Toasty.error(BlogCheckerActivity.this, "Try Purchasing Again", Toast.LENGTH_LONG).show();
                    } else {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                            finish();
                            Intent openBlogFromBlogChecker = new Intent(BlogCheckerActivity.this, BlogActivity.class);
                            startActivity(openBlogFromBlogChecker);
                            Animatoo.animateSwipeRight(BlogCheckerActivity.this);
                        }
                    }
                }
            }
        }).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {


                } else {
                    Toasty.error(BlogCheckerActivity.this, "Failed to connect", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toasty.error(BlogCheckerActivity.this, "Disconnected from the Billing Client", Toast.LENGTH_LONG).show();

            }
        });
        skulist.add(categories);
        final SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skulist).setType(BillingClient.SkuType.INAPP);
        buttonBlogChecker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(@NonNull BillingResult billingResult, List<SkuDetails> list) {
                        if (list != null && billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            for (final SkuDetails skuDetails : list) {
                                String sku = skuDetails.getSku();
                                String price = skuDetails.getPrice();
                                String description = skuDetails.getDescription();

                                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                        .setSkuDetails(skuDetails)
                                        .build();
                                BillingResult responsecode = billingClient.launchBillingFlow(BlogCheckerActivity.this, flowParams);
                            }
                        }
                    }
                });
            }
        });
    }


    private void handlePurchase(Purchase purchase) {
        try {
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                if (purchase.getProducts().equals(categories)) {
                    ConsumeParams consumeParams = ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
                    ConsumeResponseListener consumeResponseListener = new ConsumeResponseListener() {
                        @Override
                        public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                            Toasty.success(BlogCheckerActivity.this, "Request Acknowledged", Toast.LENGTH_LONG).show();

                        }
                    };
                    billingClient.consumeAsync(consumeParams, consumeResponseListener);
                    //now you can purchase same categories again and again
                    //Here we give coins to user.

                    FirebaseMessaging.getInstance().unsubscribeFromTopic("purchase_blog");

                    LayoutInflater inflater = LayoutInflater.from(BlogCheckerActivity.this);
                    View view = inflater.inflate(R.layout.alert_dialog_purchased, null);
                    Button acceptButton = view.findViewById(R.id.btnOkAlertPurchased);
                    final AlertDialog alertDialog = new AlertDialog.Builder(BlogCheckerActivity.this)
                            .setView(view)
                            .show();

                    acceptButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            finish();
                            Intent openBlogFromBlogChecker = new Intent(BlogCheckerActivity.this, BlogActivity.class);
                            startActivity(openBlogFromBlogChecker);
                            Animatoo.animateSwipeRight(BlogCheckerActivity.this);
                        }
                    });

                    StorageReference imageReference1 = storageReference.child(firebaseAuth.getUid()).child("Blog Purchased");
                    Uri uri1 = Uri.parse("android.resource://com.rackluxury.rolex/drawable/img_blog_checker");
                    UploadTask uploadTask = imageReference1.putFile(uri1);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error(BlogCheckerActivity.this, "Please Check Your Internet Connectivity", Toast.LENGTH_LONG).show();

                        }
                    });
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            Toasty.success(BlogCheckerActivity.this, "Purchase Successful", Toast.LENGTH_LONG).show();
                            int valPurc = 5;
                            purchasesRemaining.setText(Integer.toString(valPurc));
                            int coinCount = Integer.parseInt(coins.getString("Coins", "0"));
                            coinCount = coinCount + 100000;
                            SharedPreferences.Editor coinsEdit = coins.edit();
                            coinsEdit.putString("Coins", String.valueOf(coinCount));
                            coinsEdit.apply();

                        }
                    });


                }
            }
        } catch (Exception e) {
            Toasty.error(BlogCheckerActivity.this, "Transaction Failed", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        Toasty.info(BlogCheckerActivity.this, "onPurchases Updated", Toast.LENGTH_LONG).show();

    }


    @Override
    public void onBackPressed() {
        finish();
        Intent openHomeFromBlogChecker = new Intent(BlogCheckerActivity.this, HomeActivity.class);
        startActivity(openHomeFromBlogChecker);
        Animatoo.animateSwipeLeft(BlogCheckerActivity.this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}