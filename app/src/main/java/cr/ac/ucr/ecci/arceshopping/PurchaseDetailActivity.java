package cr.ac.ucr.ecci.arceshopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cr.ac.ucr.ecci.arceshopping.adapters.CartRvAdapter;
import cr.ac.ucr.ecci.arceshopping.cart.CartFragment;
import cr.ac.ucr.ecci.arceshopping.model.Product;
import cr.ac.ucr.ecci.arceshopping.model.Purchase;
import cr.ac.ucr.ecci.arceshopping.model.User;

public class PurchaseDetailActivity extends ConnectedActivity {

    Purchase selectedPurchase;
    User loggedInUser;
    CartRvAdapter scAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        setContentView(R.layout.activity_purchase_detail);
        if(bundle != null){
            this.loggedInUser = bundle.getParcelable("LOGGED_IN_USER");
            this.selectedPurchase = bundle.getParcelable("SELECTED_PURCHASE");
            int purchaseNumber = bundle.getInt("PURCHASE_NUMBER");
            populate(purchaseNumber);
            loadProducts();
        }

    }

    private void loadProducts(){
        ArrayList<Product> productsList = new ArrayList<Product>();
        RecyclerView rView = findViewById(R.id.shopping_cart);
        scAdapter = new CartRvAdapter(productsList);
        LinearLayoutManager lManager = new LinearLayoutManager(this);
        DividerItemDecoration divider = new DividerItemDecoration(rView.getContext(), lManager.getOrientation());
        rView.addItemDecoration(divider);
        rView.setLayoutManager(lManager);
        CartFragment.loadProducts(selectedPurchase.getShoppingCart(), getBaseContext(),productsList, scAdapter, rView);
    }

    private void populate(int purchaseId){
        ImageView userPic = findViewById(R.id.user_pic);
        TextView userName = findViewById(R.id.user_name);
        TextView purchaseNumber = findViewById(R.id.purchase_number);
        TextView purchaseDate = findViewById(R.id.date);
        TextView purchaseTotal = findViewById(R.id.total);
        if(loggedInUser != null){
            if(!loggedInUser.getPath().equals("")) {
                Picasso.get().load(loggedInUser.getPath()).into(userPic);
            }
            userName.setText(loggedInUser.getName());
        }

        if(selectedPurchase != null){
            purchaseNumber.setText("Compra # "+ String.valueOf(purchaseId));
            purchaseDate.setText(selectedPurchase.getPurchaseTime());
            purchaseTotal.setText("Total $"+selectedPurchase.getTotal());
        }

        findViewById(R.id.share_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });
    }

    private void share(){
        //Solution taken from StackOverflow, available at:
        //https://stackoverflow.com/questions/20236947/adding-a-share-button-to-share-the-app-on-social-networks
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "¡He realizado una compra por Arce Shopping!");
        startActivity(Intent.createChooser(intent, "Share"));
    }
}