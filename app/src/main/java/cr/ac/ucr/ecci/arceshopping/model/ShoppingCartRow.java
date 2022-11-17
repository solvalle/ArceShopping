package cr.ac.ucr.ecci.arceshopping.model;

public class ShoppingCartRow {
    private String ownerEmail;
    private int productId;
    private int quantity;
    private int price;


    public ShoppingCartRow(String ownerEmail, int productId,
                           int quantity, int price){

        this.ownerEmail = ownerEmail;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;

    }

    public String getOwnerEmail(){ return ownerEmail; }
    public int getPrice() { return price; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }

}
