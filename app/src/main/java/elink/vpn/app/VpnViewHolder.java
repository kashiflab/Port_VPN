package elink.vpn.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import elink.vpn.app.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import elink.vpn.app.util.Vpn;

class VpnViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tvProfileName)
    TextView tvProfileName;

    @BindView(R.id.tvProfileName2)
    TextView tvProfileName2;



    @BindView(R.id.tvFree)
    TextView tvFree;

    @BindView(R.id.tvPremium)
    TextView tvPremium;

    @BindView(R.id.tvVip)
    TextView tvVip;

    @BindView(R.id.imageView)
    ImageView imageView;

    private OnClickListener listener;
    private Vpn vpn;

    VpnViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(v -> {
            if (this.listener != null && this.vpn != null) {
                listener.onClick(vpn);
            }
        });
    }

    static VpnViewHolder create(@NonNull ViewGroup parent) {
        return new VpnViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vpn, parent, false));
    }

    void bind(@NonNull Vpn vpn, @NonNull OnClickListener listener) {
        this.vpn = vpn;
        tvProfileName.setText(vpn.getName());
        tvProfileName2.setText(vpn.getLocation());

        if(vpn.getPremium()==1)
        {
            tvFree.setVisibility(View.GONE);
            tvVip.setVisibility(View.GONE);

            tvPremium.setVisibility(View.VISIBLE);
        }
        else{
            tvPremium.setVisibility(View.GONE);

            tvVip.setVisibility(View.GONE);

            tvFree.setVisibility(View.VISIBLE);
        }


        if(vpn.getVip()==1)
        {
            tvFree.setVisibility(View.GONE);
            tvPremium.setVisibility(View.GONE);

            tvVip.setVisibility(View.VISIBLE);
        }

//        String connected_title = ICSOpenVPNApplication.getInstance().getCurrent_connected();
//
//        if(connected_title.equals(vpn.getConfig_file()));


        Picasso.get().load(Constants.API_SERVER+"resources/uploads/servers/"+vpn.getFlag()).into(imageView);





        this.listener = listener;
    }

    public interface OnClickListener {
        void onClick(@NonNull Vpn vpn);
    }
}
