package cr.ac.ucr.ecci.arceshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;
import java.util.List;

import cr.ac.ucr.ecci.arceshopping.db.DbShoppingCart;
import cr.ac.ucr.ecci.arceshopping.db.FirebaseHelper;
import cr.ac.ucr.ecci.arceshopping.model.Product;
import cr.ac.ucr.ecci.arceshopping.model.ShoppingCartRow;

public class SingleProductActivity extends AppCompatActivity {

    private TextView productTitle;
    private TextView productDescription;
    private TextView productCategory;
    private TextView productPrice;
    private TextView textCounter;
    private Product product;
    private ImageSlider carousel;
    private  FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseHelper = new FirebaseHelper();
        setContentView(R.layout.activity_single_product);
        productTitle = (TextView) findViewById(R.id.single_product_title);
        productDescription = (TextView) findViewById(R.id.single_product_description);
        productCategory = (TextView) findViewById(R.id.single_product_category);
        productPrice = (TextView) findViewById(R.id.single_product_price);
        textCounter = (TextView) findViewById(R.id.cart_quantity);
        textCounter.setText("1");
        carousel = (ImageSlider) findViewById(R.id.single_product_image_slider);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Receive the product that was selected
        this.product = getIntent().getExtras().getParcelable("product");
        // Load the product's values
        productTitle.setText(this.product.getTitle());
        productDescription.setText(this.product.getDescription());
        productCategory.setText("Category: " + this.product.getCategory());
        productPrice.setText("$"+Integer.toString(this.product.getPrice()));
        ArrayList<String> productList = new ArrayList<>(this.product.getImages());
        // Create a SlideModel list for the carousel
        List<SlideModel> slideList = new ArrayList<>();
        for (int index = 0; index < productList.size(); index++) {
            String var = productList.get(index);
            slideList.add(new SlideModel(var,"", ScaleTypes.FIT));
        }
        carousel.setImageList(slideList, ScaleTypes.FIT);
    }

    /**
     * Increases the product counter
     */
    public void addCounter(View view) {
        int counter = Integer.parseInt(textCounter.getText().toString()) + 1;
        if (counter <= 10 && counter <= product.getStock()) {
            textCounter.setText(Integer.toString(counter));
        }
    }

    /**
     * Decreases the product counter
     */
    public void deductCounter(View view) {
        int counter = Integer.parseInt(textCounter.getText().toString()) - 1;
        if (counter >= 1) {
            textCounter.setText(Integer.toString(counter));
        }
    }

    /**
     * Adds the current product to the cart and then goes the CartActivity
     */
    public void addToCart(View view) {

        SharedPreferences sp = this.getSharedPreferences("login", Context.MODE_PRIVATE);
        Toast toast= Toast.makeText(this,"", Toast.LENGTH_SHORT);
        ShoppingCartRow newRow = new ShoppingCartRow(sp.getString("userEmail",""),
                this.product.getId(),
                Integer.parseInt(textCounter.getText().toString()),
                this.product.getPrice());

        firebaseHelper.insertShoppingCartRow(newRow, toast, this.product.getStock(), this);
        /*
        long result = db.insertProduct(sp.getString("userEmail",""), this.product.getId(),
                Integer.parseInt(textCounter.getText().toString()), this.product.getPrice(),
                this.product.getStock());
        /*
        if (result == 1) {
            Toast.makeText(this, "Producto añadido éxitosamente", Toast.LENGTH_SHORT).show();
            finish();
        }
        else if (result == 2) {
            Toast.makeText(this, "La cantidad excede del stock o de 10", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Hubo un error", Toast.LENGTH_SHORT).show();
        }
    */

    }

}