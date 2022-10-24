package cr.ac.ucr.ecci.arceshopping.cart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cr.ac.ucr.ecci.arceshopping.CartAdapter;
import cr.ac.ucr.ecci.arceshopping.R;
import cr.ac.ucr.ecci.arceshopping.databinding.FragmentCartBinding;
import cr.ac.ucr.ecci.arceshopping.db.DbShoppingCart;
import cr.ac.ucr.ecci.arceshopping.db.DbUsers;
import cr.ac.ucr.ecci.arceshopping.model.Product;
import cr.ac.ucr.ecci.arceshopping.model.User;

public class CartFragment extends Fragment {
    private ArrayList<Product> productList = new ArrayList<Product>();
    private FragmentCartBinding binding;
    private RecyclerView productsRV;
    private TextView emptyCartTV;
    private TextView priceTV;
    private CartAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);

        DbUsers dbUsers = new DbUsers(root.getContext());
        User user = dbUsers.selectUser(sp.getString("userEmail",""));

        ImageView userPhotoIV = (ImageView) root.findViewById(R.id.userPhoto);
        TextView userFullNameTV = (TextView) root.findViewById(R.id.userFullName);
        this.priceTV = (TextView) root.findViewById(R.id.priceTV);
        Button cancelButton = (Button) root.findViewById(R.id.cancel_button);
        Button payButton = (Button) root.findViewById(R.id.pay_button);
        productsRV = (RecyclerView) root.findViewById(R.id.productsRV);
        emptyCartTV = (TextView) root.findViewById(R.id.emptyCart);

        userFullNameTV.setText(user.getName());

        // TODO: load user photo
        //Picasso.get().load(user.getPhoto()).into(userPhotoIV);

        DbShoppingCart dbShoppingCart = new DbShoppingCart(root.getContext());
        priceTV.setText("$" + dbShoppingCart.getTotalPriceOfUserShoppingCart(user.getEmail()));

        // Load products list
        HashMap<Integer, Integer> shoppingCart = dbShoppingCart.selectUserShoppingCart(user.getEmail());
        if (shoppingCart.size() > 0) {
            emptyCartTV.setVisibility(View.INVISIBLE);
            productsRV.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));
            loadProducts(root, shoppingCart);
        } else {
            emptyCartTV.setVisibility(View.VISIBLE);
        }

        // Listeners to buttons
        payButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pay();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cleanCart(v, user.getEmail());
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void loadProducts(View root, HashMap<Integer, Integer> shoppingCart) {
        RequestQueue requestQueue = Volley.newRequestQueue(root.getContext());

        for (int productId : shoppingCart.keySet()) {
            final String URL_PRODUCT = "https://dummyjson.com/products/" + productId;

            StringRequest myRequest = new StringRequest(Request.Method.GET, URL_PRODUCT,
                    response -> {
                        try {
                            JSONObject myJsonObject = new JSONObject(response);
                            Gson gson = new Gson();

                            Product product = gson.fromJson(myJsonObject.toString(), Product.class);

                            if (product != null) {
                                product.setItemsInCart(shoppingCart.get(productId));
                                productList.add(product);
                            }

                            adapter = new CartAdapter(productList);
                            productsRV.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    volleyError -> Toast.makeText(root.getContext(), volleyError.getMessage(), Toast.LENGTH_SHORT).show()
            );
            requestQueue.add(myRequest);
        }
    }

    private void cleanCart(View root, String userEmail) {
        DbShoppingCart dbShoppingCart = new DbShoppingCart(root.getContext());

        boolean deleted = dbShoppingCart.deleteUserCart(userEmail);
        if(deleted) {
            productList.clear();
            productsRV.setAdapter(null);
            emptyCartTV.setVisibility(View.VISIBLE);
            priceTV.setText("$0");
        } else {
            Toast.makeText(root.getContext(), "Ocurrió un problema limpiando el carrito. Intentelo de nuevo", Toast.LENGTH_SHORT).show();
        }
    }

    private void pay() {
    }

}