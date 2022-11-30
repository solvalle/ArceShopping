package cr.ac.ucr.ecci.arceshopping;

import cr.ac.ucr.ecci.arceshopping.model.Purchase;

public interface IPurchaseHistoryReceiver {
    public void onHistoryLoaded(Purchase[] purchases);
}
