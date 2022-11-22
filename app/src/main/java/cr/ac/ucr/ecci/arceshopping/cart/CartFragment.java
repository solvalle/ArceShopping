package cr.ac.ucr.ecci.arceshopping.cart;

import android.content.Context;
import android.content.Intent;
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

import cr.ac.ucr.ecci.arceshopping.ICartResponder;
import cr.ac.ucr.ecci.arceshopping.PurchaseHistoryActivity;
import cr.ac.ucr.ecci.arceshopping.R;
import cr.ac.ucr.ecci.arceshopping.databinding.FragmentCartBinding;
import cr.ac.ucr.ecci.arceshopping.db.DbShoppingCart;
import cr.ac.ucr.ecci.arceshopping.db.DbUsers;
import cr.ac.ucr.ecci.arceshopping.db.FirebaseHelper;
import cr.ac.ucr.ecci.arceshopping.model.EmailManager;
import cr.ac.ucr.ecci.arceshopping.model.Product;
import cr.ac.ucr.ecci.arceshopping.model.ShoppingCartRow;
import cr.ac.ucr.ecci.arceshopping.model.User;

/**
 * Fragment that represents and controls the cart. Here the user can administrate his/her cart and buy
 * the products that he/she wants
 */
public class CartFragment extends Fragment implements ICartResponder {
    private ArrayList<Product> productList = new ArrayList<Product>();
    private FragmentCartBinding binding;
    private RecyclerView productsRV;
    private TextView emptyCartTV;
    private TextView priceTV;
    private CartRvAdapter adapter;
    private User user;
    private ImageView userPhotoIV;
    private TextView userFullNameTV;
    private FirebaseHelper firebaseHelper;
    //private DbShoppingCart dbShoppingCart;
    HashMap<Integer, Integer> shoppingCart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        this.shoppingCart = new HashMap<Integer,Integer>();
        SharedPreferences sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);

        //DbUsers dbUsers = new DbUsers(root.getContext());
        //this.user = dbUsers.selectUser(sp.getString("userEmail",""));
        this.firebaseHelper = new FirebaseHelper();
        firebaseHelper.setCartResponder(this);
        firebaseHelper.selectUser(sp.getString("userEmail",""));
        this.userPhotoIV = (ImageView) root.findViewById(R.id.userPhoto);
        this.userFullNameTV = (TextView) root.findViewById(R.id.userFullName);
        this.priceTV = (TextView) root.findViewById(R.id.priceTV);
        Button cancelButton = (Button) root.findViewById(R.id.cancel_button);
        Button payButton = (Button) root.findViewById(R.id.pay_button);
        Button shoppingHistoryButton = (Button) root.findViewById(R.id.shopping_history);
        productsRV = (RecyclerView) root.findViewById(R.id.productsRV);
        emptyCartTV = (TextView) root.findViewById(R.id.emptyCart);



        //this.dbShoppingCart = new DbShoppingCart(root.getContext());

        // Listeners to buttons
        payButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pay(v);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cleanCart(v);
            }
        });
        shoppingHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchHistory();
            }
        });

        // sets the delete swipe on cart products
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
                firebaseHelper.deleteItem(user.getEmail(), product.getId());
                shoppingCart.remove(product.getId());
                productList.remove(position);
                adapter.setProductsList(productList);
                firebaseHelper.getTotalPriceOfUserShoppingCart(user.getEmail());
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

    public void launchHistory()
    {
        Intent intent = new Intent(getActivity(), PurchaseHistoryActivity.class);
        startActivity(intent);
    }

    /**
     * Consults the api to load the products in the cart with the necessary information
     */
    public void loadProducts(HashMap<Integer, Integer> shoppingCart) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

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
                    volleyError -> Toast.makeText(getActivity(), volleyError.getMessage(), Toast.LENGTH_SHORT).show()
            );
            requestQueue.add(myRequest);
        }
    }

    /**
     * This method is attached to the "cancel button". This method clears the cart, including the
     * data stored in the data base
     */
    private void cleanCart(View root) {
        //DbShoppingCart dbShoppingCart = new DbShoppingCart(root.getContext());
        // Delete the data base
        //dbShoppingCart.deleteUserCart(user.getEmail());
        firebaseHelper.clearShoppingCart(user.getEmail());
        // Reflect the data on the screen
    }

    /**
     * This method is attached to the "cancel button". This method clears the cart, including the
     * data stored in the data base
     */
    private void pay(View root) {
        firebaseHelper.commitPurchase( Integer.parseInt(priceTV.getText().toString().substring(1)), user.getEmail(),
                                        shoppingCart);
    }

    private void clearUI(){
        productList.clear();
        shoppingCart.clear();
        productsRV.setAdapter(null);
        emptyCartTV.setVisibility(View.VISIBLE);
        priceTV.setText("$0");
    }

    @Override
    public void onSuccessfulPurchase(boolean success){
        if(success) {
            Toast.makeText(getActivity(), "Compra exitosa. Revisa tu correo",
                    Toast.LENGTH_SHORT).show();
            EmailManager emailManager = new EmailManager();
            emailManager.sendPurchaseEmail(user.getName(), user.getEmail(),productList, priceTV.getText().toString());
            clearUI();
        } else {
            Toast.makeText(getActivity(), "Ocurrió un problema con el carrito. Intentelo de nuevo",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onShoppingCartLoaded() {
        if (shoppingCart.size() > 0) {
            emptyCartTV.setVisibility(View.INVISIBLE);
            productsRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            loadProducts(shoppingCart);
        } else {
            emptyCartTV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onShoppingCartEmptied(boolean deleted) {
        if(deleted) {
            clearUI();
        } else {
            Toast.makeText(getActivity(), "Ocurrió un problema limpiando el carrito. Intentelo de nuevo",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPriceCalculated(int total){
        priceTV.setText("$" + total);
    }

    @Override
    public void onUserDataLoaded(User user) {
        this.user = user;
        System.out.println(user.getName());
        this.userFullNameTV.setText(user.getName());

        if(this.user.getPath().compareTo("") > 0)
            Picasso.get().load(user.getPath()).into(userPhotoIV);

        firebaseHelper.getTotalPriceOfUserShoppingCart(user.getEmail());
        // Load products list
        firebaseHelper.getShoppingCart(this.user.getEmail(), shoppingCart); //dbShoppingCart.selectUserShoppingCart(user.getEmail());

    }

    /**
     * This class is the recycleview adapter
     */
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

        /**
         * This class is the adapter's viewholder
         */
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

            /**
             * Increases the product counter
             */
            public void addCounter() {
                int counter = Integer.parseInt(quantity.getText().toString()) + 1;
                if (counter <= 10 && counter <= this.stock) {
                    modifyPrices(counter, 1);
                }
            }

            /**
             * Decreases the product counter
             */
            public void deductCounter() {
                int counter = Integer.parseInt(quantity.getText().toString()) - 1;
                if (counter >= 1) {
                    modifyPrices(counter, -1);
                }
            }

            /**
             * Saves the cart changes on the data base
             */
            public void modifyPrices(int counter, int number) {
                Product product = productsList.get(this.getAdapterPosition());
                //insertShoppingCartRow will determine that prduct is already in shopping cart, so it will just update its quantity
                firebaseHelper.insertShoppingCartRow(new ShoppingCartRow(user.getEmail(),
                                                    product.getId(), number,
                                                    product.getPrice()),Toast.makeText(getActivity(),"",Toast.LENGTH_SHORT),
                                                    product.getStock(), getActivity());
                quantity.setText(Integer.toString(counter));
                //firebaseHelper.getTotalPriceOfUserShoppingCart(user.getEmail());
            }
        }
    }

}