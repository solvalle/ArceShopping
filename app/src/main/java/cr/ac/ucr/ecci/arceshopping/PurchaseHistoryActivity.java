package cr.ac.ucr.ecci.arceshopping;
import cr.ac.ucr.ecci.arceshopping.db.FirebaseHelper;
import cr.ac.ucr.ecci.arceshopping.model.Purchase;
import cr.ac.ucr.ecci.arceshopping.model.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class PurchaseHistoryActivity extends ConnectedActivity implements IPurchaseHistoryReceiver,
        PurchaseHistoryAdapter.ItemClickListener {

    FirebaseHelper firebaseHelper;
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
            User user = bundle.getParcelable("LOGGED_IN_USER");
            ImageView userPic = findViewById(R.id.user_pic);
            TextView userName = findViewById(R.id.user_name);
            userName.setText(user.getName());
            if(!user.getPath().equals("")){
                Picasso.get().load(user.getPath()).into(userPic);
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

    private void launchPurchaseDetail(){

    }

    @Override
    public void onItemClick(int position) {
        System.out.println(userPurchaseHistory[position].toString());
        launchPurchaseDetail();
    }
}