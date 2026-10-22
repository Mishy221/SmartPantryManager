package com.smartpantry.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/** Custom RecyclerView adapter that shows the pantry items stored in the SQLite database. */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    public interface Listener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final Listener listener;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        final PantryItem item = items.get(position);
        h.name.setText(item.getName());
        h.qty.setText(IngredientMatcher.formatQty(item.getQuantity()) + " " + item.getUnit());

        int days = ExpiryUtils.daysUntil(item.getExpiry());
        if (days == ExpiryUtils.NO_EXPIRY) {
            h.expiry.setText("No expiry date");
            h.expiry.setTextColor(h.qty.getTextColors());
        } else if (days < 0) {
            h.expiry.setText("EXPIRED (" + item.getExpiry() + ")");
            h.expiry.setTextColor(h.itemView.getResources().getColor(R.color.expired_red));
        } else if (days <= 3) {
            h.expiry.setText("Expires in " + days + " day(s) - " + item.getExpiry());
            h.expiry.setTextColor(h.itemView.getResources().getColor(R.color.orange));
        } else {
            h.expiry.setText("Expires " + item.getExpiry());
            h.expiry.setTextColor(h.qty.getTextColors());
        }

        h.itemView.setOnClickListener(v -> listener.onEdit(item));
        h.delete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView qty;
        final TextView expiry;
        final ImageButton delete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvItemName);
            qty = itemView.findViewById(R.id.tvItemQty);
            expiry = itemView.findViewById(R.id.tvItemExpiry);
            delete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
