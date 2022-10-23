package cr.ac.ucr.ecci.arceshopping;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cr.ac.ucr.ecci.arceshopping.model.Product;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private ArrayList<Product> productsList;

    public CartAdapter(ArrayList<Product> productsList) {
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

        Picasso.get().load(product.images.get(0)).into(holder.getProductPhoto());


    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        private ImageView productPhoto;
        private TextView productName;
        private TextView quantity;
        private TextView price;

        public ImageView getProductPhoto() {
            return productPhoto;
        }

        public void setProductPhoto(ImageView productPhoto) {
            this.productPhoto = productPhoto;
        }

        public TextView getProductName() {
            return productName;
        }

        public void setProductName(TextView productName) {
            this.productName = productName;
        }

        public TextView getQuantity() {
            return quantity;
        }

        public void setQuantity(TextView quantity) {
            this.quantity = quantity;
        }

        public TextView getPrice() {
            return price;
        }

        public void setPrice(TextView price) {
            this.price = price;
        }

        public ViewHolder(View itemView) {
            super(itemView);

            productPhoto = (ImageView) itemView.findViewById(R.id.productPhoto);
            productName = (TextView) itemView.findViewById(R.id.productName);
            quantity = (TextView) itemView.findViewById(R.id.quantity);
            price = (TextView) itemView.findViewById(R.id.price);

        }

        /**
         * Increases the product counter

        public void addCounter(View view) {
            int counter = Integer.parseInt(quantity.getText().toString()) + 1;
            if (counter <= 10 && counter <= productsList.getStock()) {
                quantity.setText(Integer.toString(counter));
            }
        }*/

        /**
         * Decreases the product counter

        public void deductCounter(View view) {
            int counter = Integer.parseInt(textCounter.getText().toString()) - 1;
            if (counter >= 1) {
                quantity.setText(Integer.toString(counter));
            }
        }*/
    }
}
