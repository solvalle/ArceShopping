package cr.ac.ucr.ecci.arceshopping;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import cr.ac.ucr.ecci.arceshopping.databinding.ActivityProductsBinding;
import cr.ac.ucr.ecci.arceshopping.db.DbUsers;
import cr.ac.ucr.ecci.arceshopping.model.Product;


public class MainActivity extends ConnectedActivity {
    private ActivityProductsBinding binding;

    ArrayList<Product> mProducts;
    ProductsAdapter productsAdapter;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            BottomNavigationView navView = findViewById(R.id.nav_view);
            AppBarConfiguration appBarConfiguration = new
                    AppBarConfiguration.Builder(
                    R.id.navigation_account, R.id.navigation_api,
                    R.id.navigation_cart)
                    .build();
            NavController navController = Navigation.findNavController(this,
                    R.id.nav_host_fragment_activity_main);
            NavigationUI.setupActionBarWithNavController(this, navController,
                    appBarConfiguration);
            NavigationUI.setupWithNavController(binding.navView, navController);
        }
    }


    public void setmProducts(ArrayList<Product> mProducts) {
        this.mProducts = mProducts;
    }

    public void setProductsAdapter(ProductsAdapter productsAdapter){
        this.productsAdapter = productsAdapter;
    }

    public void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<Product> filteredList = new ArrayList<>();

        // running a for loop to compare elements.
        for (Product item : mProducts) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.

            productsAdapter.filterList(filteredList);
        }
    }
}

