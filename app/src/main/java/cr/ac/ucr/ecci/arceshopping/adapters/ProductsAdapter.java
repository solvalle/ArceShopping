package cr.ac.ucr.ecci.arceshopping.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cr.ac.ucr.ecci.arceshopping.R;
import cr.ac.ucr.ecci.arceshopping.model.Product;
import cr.ac.ucr.ecci.arceshopping.api.ListProductViewInterface;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> implements Filterable {

    public ArrayList<Product> mProducts;
    public ArrayList<Product> mFilteredProducts;
    public Context mContext;
    public ListProductViewInterface mListProductViewInterface;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);


        View productView = inflater.inflate(R.layout.product, parent, false);


        ViewHolder viewHolder = new ViewHolder(productView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Product product = mProducts.get(position);


        TextView textView = holder.nameTextView;
        textView.setText(product.getTitle());
        TextView textViewPrice = holder.priceTextView;
        textViewPrice.setText("$"+product.getPrice());
        //ImageView
        ImageView imageView = holder.pictureImageView;
        //using only the first image
        Picasso.get().load(mProducts.get(position).images.get(0)).into(imageView);
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    //Constructor
    public ProductsAdapter(ArrayList<Product> products, Context context,
                           ListProductViewInterface listProductViewInterface){
        mProducts = products;
        mFilteredProducts = products;
        mContext = context;
        mListProductViewInterface = listProductViewInterface;
    }

    /**
     * Receives a new list and change the content displayed on screen
     * @param filterlist the new filtered list
     */
    public void filterList(ArrayList<Product> filterlist) {

        //losing the reference to filtered products here
        //this.mFilteredProducts = filterlist;
        this.mProducts = filterlist;

        notifyDataSetChanged();
    }


    // Filters the product list
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults = new FilterResults();
                if(constraint == null || constraint.length() == 0){
                    filterResults.count = mProducts.size();
                    filterResults.values = mProducts;

                }else{
                    List<Product> resultsModel = new ArrayList<Product>();
                    String searchStr = constraint.toString().toLowerCase();

                    for(Product itemsModel:mProducts){
                        if(itemsModel.getTitle().contains(searchStr) || itemsModel.getCategory().contains(searchStr)){
                            resultsModel.add(itemsModel);

                        }
                        filterResults.count = resultsModel.size();
                        filterResults.values = resultsModel;
                    }

                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                mFilteredProducts = (ArrayList<Product>) results.values;
                notifyDataSetChanged();

            }
        };
        return filter;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ImageView pictureImageView;
        public TextView priceTextView;


        public ViewHolder(View itemView) {

            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.product_name);
            pictureImageView = (ImageView) itemView.findViewById(R.id.product_image);
            priceTextView = (TextView) itemView.findViewById(R.id.product_price);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListProductViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            mListProductViewInterface.onItemClick(mProducts.get(pos));
                        }
                    }
                }
            });
        }

    }
}
