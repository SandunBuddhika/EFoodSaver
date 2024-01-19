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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.sandun.efoodsaver.R;
import com.sandun.efoodsaver.SingleProductFragment;
import com.sandun.efoodsaver.dto.Product;
import com.sandun.efoodsaver.dto.Review;
import com.sandun.efoodsaver.entities.User;
import com.sandun.efoodsaver.model.AlertPopUp;
import com.sandun.efoodsaver.model.CartService;
import com.sandun.efoodsaver.model.DoProcess;
import com.sandun.efoodsaver.model.IsLogIn;
import com.sandun.efoodsaver.model.ResponceAlertDialog;
import com.sandun.efoodsaver.model.WaitAlertPopUp;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> productList;
    private Context context;
    private FirebaseStorage storage;
    private int limit;
    private FragmentManager fragmentManager;

    User user = IsLogIn.user;

    public ProductAdapter(List<Product> productList, Context context, int limit, FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.productList = productList;
        this.context = context;
        if (limit != 0) {
            this.limit = limit;
        }
        this.storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.product_card, parent, false);
        return new ProductAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        Product currentProduct = this.productList.get(position);
        List<String> imgList = currentProduct.getImgList();
        if (imgList != null) {
            storage.getReference("product/" + currentProduct.getId() + "/" + imgList.get(0)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).fit().centerCrop().into(holder.productImage);
                }
            });
        }
        holder.productName.setText(currentProduct.getName());
        holder.realPrice.setText("LKR " + currentProduct.getPrice());
        double price = (currentProduct.getPrice() / 100) * currentProduct.getDiscount();
        holder.productPrice.setText("/LKR " + price);

        int totalStars = 0;
        int totalReviews = 0;
        List<Review> reviewList = currentProduct.getReviewList();
        if (reviewList != null) {
            for (Review review : currentProduct.getReviewList()) {
                totalStars += review.getStars();
                totalReviews++;
            }
        }
        double averageStars = 0;
        if (totalReviews > 0) {
            averageStars = (double) totalStars / totalReviews;
        }
        holder.rating.setText(String.valueOf(averageStars));

        holder.exg.setText(currentProduct.getExpire_date());
        holder.mfg.setText(currentProduct.getManufacture_date());
        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.hiddenComponent.setVisibility(View.VISIBLE);
                holder.hiddenComponent.setAlpha(0.0f);
                holder.hiddenComponent.animate()
                        .alpha(1.0f)
                        .setDuration(500);
                new Handler(Looper.myLooper()).postDelayed(() -> {
                    holder.hiddenComponent.animate()
                            .alpha(0.0f)
                            .setDuration(500)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    holder.hiddenComponent.setVisibility(View.GONE);
                                }
                            });
                }, 5000);
                return true;
            }
        });
        WaitAlertPopUp waitAlertPopUp = new WaitAlertPopUp(context);
        if (user != null) {
            CartService service = new CartService(String.valueOf(user.getId()));
            holder.addToCart.setOnClickListener(v -> {
                service.addProductToCart(currentProduct.getId(), new DoProcess() {
                    @Override
                    public void process() {
                        waitAlertPopUp.setUp("The Product Added To Your Cart!", WaitAlertPopUp.SUCCESS);
                        waitAlertPopUp.changeButtonState(WaitAlertPopUp.VISIBLE_STATE);
                        waitAlertPopUp.customButtonSetUp((dialog, alertPopUp) -> {
                            dialog.dismiss();
                        });
                        waitAlertPopUp.show();
                    }
                }, 1);
            });
        } else {
            holder.addToCart.setVisibility(View.GONE);
        }


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", currentProduct);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, SingleProductFragment.class, bundle).setReorderingAllowed(true).addToBackStack("single_product_view").commit();
            }
        };
        holder.layout.setOnClickListener(onClickListener);
        holder.viewProduct.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        if (limit >= productList.size()) {
            return productList.size();
        } else {
            return limit;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layout;
        ConstraintLayout hiddenComponent;
        ImageView productImage;
        ImageView addToWishList;
        TextView productName;
        TextView realPrice;
        TextView productPrice;
        TextView rating;
        TextView exg;
        TextView mfg;
        Button addToCart;
        Button viewProduct;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.card_lo);
            hiddenComponent = itemView.findViewById(R.id.hide_components_outer_lo);
            productImage = itemView.findViewById(R.id.productImage);
            addToWishList = itemView.findViewById(R.id.add_watch_list);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.price);
            realPrice = itemView.findViewById(R.id.product_card_real_price);
            rating = itemView.findViewById(R.id.rating);
            exg = itemView.findViewById(R.id.exp_date);
            mfg = itemView.findViewById(R.id.mfg_date);
            addToCart = itemView.findViewById(R.id.card_add_to_cart_btn);
            viewProduct = itemView.findViewById(R.id.card_view_product_btn);
        }
    }
}
