package cr.ac.ucr.ecci.arceshopping.model;

import java.util.ArrayList;

public class Products {
    public ArrayList<Product> products;
    public int total;
    public int skip;
    public int limit;

    public ArrayList<Product> getProducts() {
        return products;
    }

    @Override
    public String toString() {
        return "Products{" +
                "products=" + products +
                ", total=" + total +
                ", skip=" + skip +
                ", limit=" + limit +
                '}';
    }
}
