package cr.ac.ucr.ecci.arceshopping;

import cr.ac.ucr.ecci.arceshopping.model.User;

public interface ICartResponder {
    public void onShoppingCartLoaded();
    public void onShoppingCartEmptied(boolean deleted);
    public void onPriceCalculated(int total);
    public void onUserDataLoaded(User user);
    public void onSuccessfulPurchase(boolean success);
}
