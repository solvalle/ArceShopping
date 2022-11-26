package cr.ac.ucr.ecci.arceshopping;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cr.ac.ucr.ecci.arceshopping.model.Purchase;

public class PurchaseHistoryAdapter extends RecyclerView.Adapter<PurchaseHistoryAdapter.PurchaseHolder> {
    private Purchase[] purchases;
    private LayoutInflater inflater;



    public PurchaseHistoryAdapter(Context context, Purchase[] purchases){
        this.purchases = purchases;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PurchaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View purchaseHolder = this.inflater.inflate(R.layout.purchase_history_row,parent, false);
        return new PurchaseHolder(purchaseHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseHolder holder, int position) {
        holder.purchaseId.setText(String.valueOf(position));
        holder.purchaseTotal.setText(String.valueOf(purchases[position].getTotal()));
        holder.purchaseTime.setText(purchases[position].getPurchaseTime());
    }

    @Override
    public int getItemCount() {
        return purchases.length;
    }

    public class PurchaseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView purchaseId;
        TextView purchaseTime;
        TextView purchaseTotal;

        public PurchaseHolder(View itemView) {
            super(itemView);
            this.purchaseId = itemView.findViewById(R.id.purchase_id);
            this.purchaseTime = itemView.findViewById(R.id.purchase_date);
            this.purchaseTotal = itemView.findViewById(R.id.purchase_total);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
           System.out.println(purchaseId.getText()+ " " + purchaseTime.getText() + " " + purchaseTotal.getText());
        }
    }
}

