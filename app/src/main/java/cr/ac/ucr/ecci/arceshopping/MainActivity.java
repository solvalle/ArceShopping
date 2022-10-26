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
import java.util.ArrayList;

import cr.ac.ucr.ecci.arceshopping.databinding.ActivityProductsBinding;
import cr.ac.ucr.ecci.arceshopping.db.DbUsers;
import cr.ac.ucr.ecci.arceshopping.model.Product;


public class MainActivity extends ConnectedActivity {
    private ActivityProductsBinding binding;

    ArrayList<Product> mProducts;
    ProductsAdapter productsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        DbUsers dbUsers = new DbUsers(this);
        String userEmail = dbUsers.getLoginUser();

        if (userEmail.equals("")) {
            sp.edit().putBoolean("logged" , false).apply();
            sp.edit().putString("userEmail" , "").apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            sp.edit().putBoolean("logged", true).apply();
            sp.edit().putString("userEmail", userEmail).apply();

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

    //https://www.geeksforgeeks.org/searchview-in-android-with-recyclerview/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // below line is to get our inflater
        MenuInflater inflater = getMenuInflater();

        // inside inflater we are inflating our menu file.
        inflater.inflate(R.menu.search_menu, menu);

        // below line is to get our menu item.
        MenuItem searchItem = menu.findItem(R.id.actionSearch);

        // getting search view of our item.
        SearchView searchView = (SearchView) searchItem.getActionView();

        // below line is to call set on query text listener method.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(newText);
                return false;
            }
        });
        return true;
    }

    private void filter(String text) {
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

