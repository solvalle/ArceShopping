package cr.ac.ucr.ecci.arceshopping.cart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
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
    private CartRvAdapter adapter;
    private User user;
    private DbShoppingCart dbShoppingCart;
    HashMap<Integer, Integer> shoppingCart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);

        DbUsers dbUsers = new DbUsers(root.getContext());
        this.user = dbUsers.selectUser(sp.getString("userEmail",""));

        ImageView userPhotoIV = (ImageView) root.findViewById(R.id.userPhoto);
        TextView userFullNameTV = (TextView) root.findViewById(R.id.userFullName);
        this.priceTV = (TextView) root.findViewById(R.id.priceTV);
        Button cancelButton = (Button) root.findViewById(R.id.cancel_button);
        Button payButton = (Button) root.findViewById(R.id.pay_button);
        productsRV = (RecyclerView) root.findViewById(R.id.productsRV);
        emptyCartTV = (TextView) root.findViewById(R.id.emptyCart);

        userFullNameTV.setText(user.getName());

        // TODO: load user photo
        Picasso.get().load(user.getPath()).into(userPhotoIV);

        this.dbShoppingCart = new DbShoppingCart(root.getContext());
        priceTV.setText("$" + dbShoppingCart.getTotalPriceOfUserShoppingCart(user.getEmail()));

        // Load products list
        this.shoppingCart = dbShoppingCart.selectUserShoppingCart(user.getEmail());
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

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull
                    RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Product product = productList.get(position);
                dbShoppingCart.deleteItem(user.getEmail(), product.getId());
                productList.remove(position);
                adapter.setProductsList(productList);
                priceTV.setText("$" + dbShoppingCart.getTotalPriceOfUserShoppingCart(user.getEmail()));
                adapter.notifyDataSetChanged();
                if (productList.size() == 0)
                {
                    productsRV.setAdapter(null);
                    emptyCartTV.setVisibility(View.VISIBLE);
                }
            }
        }).attachToRecyclerView(productsRV);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Consults the api to load the products in the cart with the necessary information
     */
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

                            adapter = new CartRvAdapter(productList);
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

    /**
     * This method is attached to the "cancel button". This mehtod clears the cart, including the
     * data stored in the data base
     */
    private void cleanCart(View root, String userEmail) {
        DbShoppingCart dbShoppingCart = new DbShoppingCart(root.getContext());
        // Delete the data base
        boolean deleted = dbShoppingCart.deleteUserCart(userEmail);
        // Reflect the data on the screen
        if(deleted) {
            productList.clear();
            productsRV.setAdapter(null);
            emptyCartTV.setVisibility(View.VISIBLE);
            priceTV.setText("$0");
        } else {
            Toast.makeText(root.getContext(), "Ocurrió un problema limpiando el carrito. Intentelo de nuevo",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void pay() {
    }

    public class CartRvAdapter extends RecyclerView.Adapter<CartRvAdapter.ViewHolder> {
        private ArrayList<Product> productsList;

        public CartRvAdapter(ArrayList<Product> productsList) {
            this.productsList = productsList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.cart_item, parent, false);

            return new ViewHolder(listItem);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Product product = productsList.get(position);

            holder.getProductName().setText(product.getTitle());
            holder.getPrice().setText("$"+product.getPrice());
            holder.getQuantity().setText(Integer.toString(product.getItemsInCart()));
            holder.getAddButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.addCounter();
                }
            });
            holder.getDeductButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.deductCounter();
                }
            });
            holder.setStock(product.getStock());
            Picasso.get().load(product.images.get(0)).into(holder.getProductPhoto());
        }

        @Override
        public int getItemCount() {
            return productsList.size();
        }

        public void setProductsList(ArrayList<Product> productsList) {
            this.productsList = productsList;
        }

        public class ViewHolder extends RecyclerView.ViewHolder  {
            private ImageView productPhoto;
            private TextView productName;
            private TextView quantity;
            private TextView price;
            private Button addButton;
            private Button deductButton;
            private int stock;

            public ImageView getProductPhoto() {
                return productPhoto;
            }

            public TextView getProductName() {
                return productName;
            }

            public TextView getQuantity() {
                return quantity;
            }

            public TextView getPrice() {
                return price;
            }

            public Button getAddButton() {
                return addButton;
            }

            public Button getDeductButton() {
                return deductButton;
            }

            public void setStock(int stock) {
                this.stock = stock;
            }

            public ViewHolder(View itemView) {
                super(itemView);

                productPhoto = (ImageView) itemView.findViewById(R.id.productPhoto);
                productName = (TextView) itemView.findViewById(R.id.productName);
                quantity = (TextView) itemView.findViewById(R.id.quantity);
                price = (TextView) itemView.findViewById(R.id.price);
                addButton = (Button) itemView.findViewById(R.id.plus_button);
                deductButton = (Button) itemView.findViewById(R.id.minus_button);
                stock = 10;
            }

            public void addCounter() {
                int counter = Integer.parseInt(quantity.getText().toString()) + 1;
                if (counter <= 10 && counter <= this.stock) {
                    modifyPrices(counter, 1);
                }
            }

            public void deductCounter() {
                int counter = Integer.parseInt(quantity.getText().toString()) - 1;
                if (counter >= 1) {
                    modifyPrices(counter, -1);
                }
            }

            public void modifyPrices(int counter, int number) {
                Product product = productsList.get(this.getAdapterPosition());
                dbShoppingCart.increaseItemQuantity(user.getEmail(), product.getId(), number, product.getStock());
                quantity.setText(Integer.toString(counter));
                priceTV.setText("$" + dbShoppingCart.getTotalPriceOfUserShoppingCart(user.getEmail()));
            }
        }
    }

}