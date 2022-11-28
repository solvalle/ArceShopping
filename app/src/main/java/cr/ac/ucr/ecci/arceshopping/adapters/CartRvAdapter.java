package cr.ac.ucr.ecci.arceshopping.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cr.ac.ucr.ecci.arceshopping.R;
import cr.ac.ucr.ecci.arceshopping.cart.CartFragment;
import cr.ac.ucr.ecci.arceshopping.db.FirebaseHelper;
import cr.ac.ucr.ecci.arceshopping.model.Product;
import cr.ac.ucr.ecci.arceshopping.model.ShoppingCartRow;

/**
 * This class is the recycleview adapter
 */
public class CartRvAdapter extends RecyclerView.Adapter<CartRvAdapter.ViewHolder> {
    private ArrayList<Product> productsList;
    private FirebaseHelper firebaseHelper;
    private String userEmail;
    private Activity context;

    public CartRvAdapter(ArrayList<Product> productsList) {
        this.productsList = productsList;
    }

    public void setFirebaseHelper(FirebaseHelper firebaseHelper) {
        this.firebaseHelper = firebaseHelper;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @NonNull
    @Override
    public CartRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.cart_item, parent, false);

        return new CartRvAdapter.ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CartRvAdapter.ViewHolder holder, int position) {
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
            firebaseHelper.insertShoppingCartRow(new ShoppingCartRow(userEmail,
                            product.getId(), number,
                            product.getPrice()), Toast.makeText(context,"",Toast.LENGTH_SHORT),
                    product.getStock(), context);
            quantity.setText(Integer.toString(counter));
            //firebaseHelper.getTotalPriceOfUserShoppingCart(user.getEmail());
        }
    }
}
