package cr.ac.ucr.ecci.arceshopping.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Product implements Parcelable {
    public int id;
    public String title;
    public String description;
    public int price;
    public double discountPercentage;
    public double rating;
    public int stock;
    public String brand;
    public String category;
    public String thumbnail;
    public ArrayList<String> images;
    public int itemsInCart;


    public Product(int id, String title, String description, int price, double discountPercentage, double rating, int stock, String brand, String category, String thumbnail) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.discountPercentage = discountPercentage;
        this.rating = rating;
        this.stock = stock;
        this.brand = brand;
        this.category = category;
        this.thumbnail = thumbnail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getCategory() {
        return category;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public int getItemsInCart() {
        return itemsInCart;
    }

    public void setItemsInCart(int itemsInCart) {
        this.itemsInCart = itemsInCart;
    }


    @Override
    public String toString() {
        return

                title + '\n' + description + '\n' +
                price + '\n' + discountPercentage + "% off \n"+
                "rating:" + rating +
                ", stock:" + stock +
                ", brand: " + brand  +
                ", category: " + category +
                ", itemsInCart: " + itemsInCart;
    }

    protected Product(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.description = in.readString();
        this.price = in.readInt();
        this.discountPercentage = in.readDouble();
        this.rating = in.readDouble();
        this.stock = in.readInt();
        this.brand = in.readString();
        this.category = in.readString();
        this.thumbnail = in.readString();
        this.images = in.readArrayList(null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeInt(price);
        parcel.writeDouble(discountPercentage);
        parcel.writeDouble(rating);
        parcel.writeInt(stock);
        parcel.writeString(brand);
        parcel.writeString(category);
        parcel.writeString(thumbnail);
        parcel.writeList(images);
    }

    public static Creator<Product> CREATOR = new Creator<Product>() {

        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
