package com.sandun.efoodsaver.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.sandun.efoodsaver.R;
import com.sandun.efoodsaver.SingleProductFragment;
import com.sandun.efoodsaver.dto.CartItemModel;
import com.sandun.efoodsaver.dto.Product;
import com.sandun.efoodsaver.model.CartService;
import com.sandun.efoodsaver.model.IsLogIn;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {

    private List<CartItemModel> productList;
    private Context context;

    private FirebaseStorage storage;
    private FragmentManager fragmentManager;

    public CartItemAdapter(Context context, List<CartItemModel> productList, FragmentManager fragmentManager) {
        this.productList = productList;
        this.context = context;
        this.fragmentManager = fragmentManager;
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItemModel item = productList.get(position);
        Product product = item.getProduct();
        int count = item.getCount();
        List<String> imgList = product.getImgList();
        System.out.println(imgList);

        if (imgList != null) {
            storage.getReference("product/" + product.getId() + "/" + imgList.get(0)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).fit().centerCrop().into(holder.img);
                }
            });
        }
        holder.name.setText(product.getName());
        holder.qty.setText(String.valueOf(count));
        CartService service = new CartService(String.valueOf(IsLogIn.getUser().getId()));
        holder.addItem.setOnClickListener(v -> {
            service.changeItemCount((count + 1), product.getId());
        });
        holder.removeItem.setOnClickListener(v -> {
            if (count > 1) {
                service.changeItemCount((count - 1), product.getId());
            }
        });
        holder.layout.setOnLongClickListener(v -> {
            holder.hiddenMenu.setVisibility(View.VISIBLE);
            holder.hiddenMenu.setAlpha(0.0f);
            holder.hiddenMenu.animate()
                    .alpha(1.0f)
                    .setDuration(500);
            new Handler(Looper.myLooper()).postDelayed(() -> {
                holder.hiddenMenu.animate()
                        .alpha(0.0f)
                        .setDuration(500)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                holder.hiddenMenu.setVisibility(View.GONE);
                            }
                        });
            }, 5000);
            return true;
        });
        holder.closeBtn.setOnClickListener(v -> {
            service.removeProduct(product.getId());
        });
        holder.viewBtn.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("product", product);
            fragmentManager.beginTransaction().replace(R.id.fragment_container, SingleProductFragment.class, bundle).setReorderingAllowed(true).addToBackStack("single_product_view").commit();
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private LinearLayout layout;
        private LinearLayout hiddenMenu;
        private Button closeBtn;
        private Button viewBtn;
        private TextView name;
        private TextView qty;
        private TextView addItem;
        private TextView removeItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.cart_layout);
            hiddenMenu = itemView.findViewById(R.id.cart_hidden_menu);
            img = itemView.findViewById(R.id.cart_product_img);
            closeBtn = itemView.findViewById(R.id.cart_item_close_btn);
            viewBtn = itemView.findViewById(R.id.cart_item_view_product_btn);
            name = itemView.findViewById(R.id.cart_product_name);
            qty = itemView.findViewById(R.id.cart_product_qty);
            addItem = itemView.findViewById(R.id.cart_add_product);
            removeItem = itemView.findViewById(R.id.cart_remove_product);
        }
    }
}
