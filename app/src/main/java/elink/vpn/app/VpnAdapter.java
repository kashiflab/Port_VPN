package elink.vpn.app;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import elink.vpn.app.util.Vpn;

class VpnAdapter extends RecyclerView.Adapter<VpnViewHolder> {
    private List<Vpn>                     items;
    private VpnViewHolder.OnClickListener listener;

    VpnAdapter(@NonNull List<Vpn> items,
               @NonNull VpnViewHolder.OnClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    void updateList(@NonNull List<Vpn> items) {
        this.items = items;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public VpnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return VpnViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull VpnViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
