package com.sandun.efoodsaver;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.sandun.efoodsaver.adapter.ReviewAdapter;
import com.sandun.efoodsaver.dto.CartItemModel;
import com.sandun.efoodsaver.dto.Product;
import com.sandun.efoodsaver.dto.Review;
import com.sandun.efoodsaver.entities.User;
import com.sandun.efoodsaver.model.BuyingService;
import com.sandun.efoodsaver.model.CartService;
import com.sandun.efoodsaver.model.DoProcess;
import com.sandun.efoodsaver.model.IsLogIn;
import com.sandun.efoodsaver.model.ProductService;
import com.sandun.efoodsaver.model.WaitAlertPopUp;

import java.util.ArrayList;
import java.util.List;

public class SingleProductFragment extends Fragment {
    private View view;
    private int count = 1;
    private Product product;
    private int page = 0;
    private int offset = 0;
    private int limit = 8;
    private User user;
    private List<Review> reviewList;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;
    private RecyclerView reviewContainer;
    private NotificationManager notificationManager;
    private String chanelId = "info";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_single_product, container, false);
        product = (Product) getArguments().get("product");
        if (product != null) {
            storage = FirebaseStorage.getInstance();
            firestore = FirebaseFirestore.getInstance();
            imageSliderSetup(product.getId());
            reviewContainer = view.findViewById(R.id.review_contaciner);
            this.reviewList = product.getReviewList();
            dataSetup();
            loadReview();
        } else {
            getChildFragmentManager().popBackStack();
        }
        user = IsLogIn.getUser();
        Button addToCar = view.findViewById(R.id.single_product_add_to_cart);
        Button buyNow = view.findViewById(R.id.single_product_buy_now);
        if (user != null) {
            WaitAlertPopUp waitAlertPopUp = new WaitAlertPopUp(getContext());
            waitAlertPopUp.changeButtonState(WaitAlertPopUp.VISIBLE_STATE);
            addToCar.setOnClickListener(v -> {
                CartService service = new CartService(String.valueOf(user.getId()));
                service.addProductToCart(product.getId(), null, 1);
                waitAlertPopUp.setUp("Successfully Added To The Cart!", WaitAlertPopUp.SUCCESS);
                waitAlertPopUp.show();
            });
            buyNow.setOnClickListener(v -> {
                List<CartItemModel> cartItemModels = new ArrayList<>();
                cartItemModels.add(new CartItemModel(product, 1, 1));
                BuyingService service2 = new BuyingService(cartItemModels, getContext());
                service2.buyNow();
                CartService service = new CartService(String.valueOf(user.getId()));
                service.removeAll();
                notification();
            });
        } else {
            addToCar.setVisibility(View.GONE);
            buyNow.setVisibility(View.GONE);
        }
        Bundle args = new Bundle();
        args.putInt("limit", 8);
        args.putString("title", "Recommended Product");
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.recommended_product_container, ProductListFragment.class, args, "review_recommended_product").commit();

        return view;
    }

    private void notification() {
        FragmentActivity activity = getActivity();
        activity.requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
        notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(chanelId, "INFO", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(true);
            channel.setDescription("You purchased new product, To see Order Detail click here!!");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 0, 1000});
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
        Bundle bundle = new Bundle();
        bundle.putString("name", "Hello World");
//        Intent intent = new Intent(SelectLocationActivity.this, MessageActivity.class);
//        intent.putExtras(bundle);
//        intent.putExtra("name", "Hello World");
//            PendingIntent.FLAG_UPDATE_CURRENT
//            PendingIntent.FLAG_ONE_SHOT
//        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(activity.getApplicationContext(), chanelId).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Order Notification").setContentText("Your Order a new Product From EFoodSaver!!").setAutoCancel(true).build();
        notificationManager.notify(1, notification);
    }

    private void dataSetup() {
        TextView pName = view.findViewById(R.id.product_name);
        TextView pRating = view.findViewById(R.id.product_rating);
        TextView mfgDate = view.findViewById(R.id.mfg_date);
        TextView exgDate = view.findViewById(R.id.exg_date);
        TextView pQty = view.findViewById(R.id.qty);
        TextView pDiscount = view.findViewById(R.id.discount);
        TextView realPrice = view.findViewById(R.id.real_price);
        TextView offPrice = view.findViewById(R.id.price_with_off);
        TextView pDescription = view.findViewById(R.id.description);
        pName.setText(product.getName());
        mfgDate.setText(product.getManufacture_date());
        exgDate.setText(product.getExpire_date());
        pQty.setText(product.getQty() + " Available");
        pDiscount.setText(product.getDiscount() + "% OFF");
        pDescription.setText(product.getDescription());

        int sum = 0;
        int stars = 0;
        if (reviewList != null && reviewList.size() > 0) {
            for (Review review : reviewList) {
                stars += review.getStars();
                sum++;
            }
        }
        double avg = 0;
        if (sum > 0) {
            avg = sum / stars;
        }
        pRating.setText(String.valueOf(avg));

        realPrice.setText("LKR " + product.getPrice());
        double offPriceText = (product.getPrice() / 100) * product.getDiscount();
        offPrice.setText("/LKR " + offPriceText);

    }

    private void loadReview() {
        if (reviewList != null && reviewList.size() > 0) {
            ReviewAdapter adapter = new ReviewAdapter(pagination(reviewList), limit);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            reviewContainer.setAdapter(adapter);
            reviewContainer.setLayoutManager(linearLayoutManager);
            adapter.notifyDataSetChanged();
        } else {
            view.findViewById(R.id.review_title).setVisibility(View.GONE);
            view.findViewById(R.id.review_contaciner).setVisibility(View.GONE);
        }
    }

    public List<Review> pagination(List<Review> list) {
        return list;
    }

    private void imageSliderSetup(String id) {
        ImageSlider slider = view.findViewById(R.id.single_product_img_slider);
        List<SlideModel> modelList = new ArrayList<>();
        List<Product> productList = new ArrayList<>();
        ProductService service = new ProductService();
        new Thread(() -> {
            service.getProduct(productList, new DoProcess() {
                @Override
                public void process() {
                    storage.getReference("product/" + id).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                        @Override
                        public void onSuccess(ListResult listResult) {
                            List<StorageReference> referenceList = listResult.getItems();
                            for (StorageReference reference : referenceList) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        modelList.add(new SlideModel(uri.toString(), ScaleTypes.CENTER_INSIDE));
                                        if (count == referenceList.size()) {
                                            new Handler(Looper.myLooper()).post(() -> {
                                                slider.setImageList(modelList);
                                            });
                                        }
                                        count++;
                                    }
                                });
                            }
                        }
                    });
                    slider.setImageList(modelList);
                }
            });
        }).start();
    }
}