package cr.ac.ucr.ecci.arceshopping;
import cr.ac.ucr.ecci.arceshopping.adapters.PurchaseHistoryAdapter;
import cr.ac.ucr.ecci.arceshopping.db.FirebaseHelper;
import cr.ac.ucr.ecci.arceshopping.model.Purchase;
import cr.ac.ucr.ecci.arceshopping.model.User;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class PurchaseHistoryActivity extends ConnectedActivity implements IPurchaseHistoryReceiver,
        PurchaseHistoryAdapter.ItemClickListener {

    FirebaseHelper firebaseHelper;
    User user;
    Purchase[] userPurchaseHistory;
    PurchaseHistoryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        SharedPreferences sp = this.getSharedPreferences("login", Context.MODE_PRIVATE);
        firebaseHelper = new FirebaseHelper();
        firebaseHelper.setPurchaseHistoryReceiver(this);
        firebaseHelper.retrieveUserPurchases(sp.getString("userEmail",""));
        setContentView(R.layout.activity_purchase_history);

        if(bundle != null){
            this.user = bundle.getParcelable("LOGGED_IN_USER");
            ImageView userPic = findViewById(R.id.user_pic);
            TextView userName = findViewById(R.id.user_name);
            userName.setText(this.user.getName());
            if(!user.getPath().equals("")){
                Picasso.get().load(this.user.getPath()).into(userPic);
            }
        }

    }

    @Override
    public void onHistoryLoaded(Purchase[] purchases) {
        //Display purchase history
        this.userPurchaseHistory = purchases;
        setHistory();
    }

    private void setHistory(){
        RecyclerView rView = findViewById(R.id.history_view);
        LinearLayoutManager lManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rView.getContext(),
                lManager.getOrientation());
        rView.setLayoutManager(lManager);
        rView.addItemDecoration(dividerItemDecoration);
        adapter = new PurchaseHistoryAdapter(this, this.userPurchaseHistory);
        adapter.setClickListener(this);
        rView.setAdapter(adapter);
    }

    private void launchPurchaseDetail(Purchase selectedPurchase, int position){
        Intent intent = new Intent(this,PurchaseDetailActivity.class);
        intent.putExtra("SELECTED_PURCHASE", selectedPurchase);
        intent.putExtra("LOGGED_IN_USER", this.user );
        intent.putExtra("PURCHASE_NUMBER", position);
        startActivity(intent);
    }

    @Override
    public void onItemClick(int position) {
        System.out.println(userPurchaseHistory[position].toString());
        launchPurchaseDetail(userPurchaseHistory[position], position);
    }
}