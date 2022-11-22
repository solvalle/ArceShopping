package cr.ac.ucr.ecci.arceshopping;
import cr.ac.ucr.ecci.arceshopping.db.FirebaseHelper;
import cr.ac.ucr.ecci.arceshopping.model.Purchase;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

public class PurchaseHistoryActivity extends ConnectedActivity implements IPurchaseHistoryReceiver {

    FirebaseHelper firebaseHelper;
    Purchase[] userPurchaseHistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = this.getSharedPreferences("login", Context.MODE_PRIVATE);
        firebaseHelper = new FirebaseHelper();
        firebaseHelper.retrieveUserPurchases(sp.getString("userEmail",""));
        firebaseHelper.setPurchaseHistoryReceiver(this);
        setContentView(R.layout.activity_purchase_history);
    }

    @Override
    public void onHistoryLoaded(Purchase[] purchases) {
        //Display purchase history
    }
}