package cr.ac.ucr.ecci.arceshopping.api;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cr.ac.ucr.ecci.arceshopping.GridSpacingItemDecoration;
import cr.ac.ucr.ecci.arceshopping.SingleProductActivity;
import cr.ac.ucr.ecci.arceshopping.model.Product;
import cr.ac.ucr.ecci.arceshopping.model.Products;
import cr.ac.ucr.ecci.arceshopping.ProductsAdapter;
import cr.ac.ucr.ecci.arceshopping.R;
import cr.ac.ucr.ecci.arceshopping.StoreActivity;
import cr.ac.ucr.ecci.arceshopping.databinding.FragmentApiBinding;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

//import com.example.communication.ListaQuotes;
//import com.example.communication.Quote;
//import com.example.communication.R;

import com.google.gson.*;

public class ApiFragment extends Fragment implements ListProductViewInterface{

    private FragmentApiBinding binding;
    private final String URLEXAMPLE = "https://dummyjson.com/products";

    public Products productos;
    RecyclerView rvProducts;
    ProductsAdapter adapter;

    private RecyclerView listProducts;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentApiBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        rvProducts = (RecyclerView) root.findViewById(R.id.rvProducts);
        StringRequest myRequest = new StringRequest(Request.Method.GET,
                URLEXAMPLE,
                response -> {
                    try{
                        JSONObject myJsonObject = new JSONObject(response);
                        String items = myJsonObject.toString();
                        Gson gson = new Gson();

                        Products products = gson.fromJson(items, Products.class);
                        listProducts = root.findViewById(R.id.rvProducts);

                        adapter = new ProductsAdapter(products.getProducts(), root.getContext(), this);

                        rvProducts.setAdapter(adapter);
                        rvProducts.addItemDecoration(new GridSpacingItemDecoration(2,50, true));
                        // second arg is the column ammount
                        rvProducts.setLayoutManager(new GridLayoutManager(root.getContext(), 2));

                        productos = products;
                        ((StoreActivity)getActivity()).setAdapter(adapter);
                        ((StoreActivity)getActivity()).setmProducts(products.getProducts());

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                volleyError -> Toast.makeText(getActivity(),
                        volleyError.getMessage(), Toast.LENGTH_SHORT).show()
        );
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(myRequest);
        return root;
    }

    @Override
    public void onItemClick(Product product) {
        Intent intent = new Intent(getActivity(), SingleProductActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

