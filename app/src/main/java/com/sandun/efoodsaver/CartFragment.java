package com.sandun.efoodsaver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.sandun.efoodsaver.adapter.CartItemAdapter;
import com.sandun.efoodsaver.dto.CartDTO;
import com.sandun.efoodsaver.dto.CartItemModel;
import com.sandun.efoodsaver.dto.CartProduct;
import com.sandun.efoodsaver.dto.Product;
import com.sandun.efoodsaver.entities.User;
import com.sandun.efoodsaver.model.BuyingService;
import com.sandun.efoodsaver.model.CartService;
import com.sandun.efoodsaver.model.DoProcess;
import com.sandun.efoodsaver.model.IsLogIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment {
    private View view;
    private List<CartItemModel> cartItem;
    private RecyclerView cartItemView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cart, container, false);
        if (IsLogIn.user == null) {
            IsLogIn isLogIn = new IsLogIn((HomeActivity) getActivity());
            isLogIn.toSignIn();
        }
        cartItem = new ArrayList<>();
        BuyingService service = new BuyingService(cartItem, getContext());
        Button buyNowBtn = view.findViewById(R.id.cart_buy_now);
        Context context = getActivity();
        buyNowBtn.setOnClickListener(v -> {
            service.buyNow();
        });
        loadCartItem();
        return view;
    }

    private void loadCartItem() {
        User user = IsLogIn.getUser();
        if (user != null) {
            CartService service = new CartService(String.valueOf(user.getId()));
            service.getUserCartProduct(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    System.out.println("asdas");
                    List<DocumentChange> products = value.getDocumentChanges();
                    if (products != null && products.size() > 0) {
                        CartItemAdapter adapter = new CartItemAdapter(getContext(), cartItem, getActivity().getSupportFragmentManager());
                        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        cartItemView = view.findViewById(R.id.cart_item_view);
                        cartItemView.setAdapter(adapter);
                        cartItemView.setLayoutManager(layoutManager);
                        int index = 0;
                        for (DocumentChange change : products) {
                            int finalIndex = index;
                            CartProduct item = change.getDocument().toObject(CartProduct.class);
                            switch (change.getType()) {
                                case ADDED: {
                                    item.getProduct().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                if (documentSnapshot.exists()) {
                                                    Product product = documentSnapshot.toObject(Product.class);
                                                    if (product != null) {
                                                        product.setId(documentSnapshot.getId());
                                                        cartItem.add(new CartItemModel(product, item.getCount(), finalIndex));
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                        }
                                    });
                                    break;
                                }
                                case MODIFIED: {
                                    CartItemModel old = cartItem.stream().filter(i -> i.getProduct().getId().equals(item.getProduct().getId())).findFirst().orElse(null);
                                    if (old != null) {
                                        old.setCount(item.getCount());
                                        adapter.notifyDataSetChanged();
                                    }
                                    break;
                                }
                                case REMOVED: {
                                    CartItemModel old = cartItem.stream().filter(i -> i.getProduct().getId().equals(item.getProduct().getId())).findFirst().orElse(null);
                                    cartItem.remove(old);
                                    adapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                            index++;
                        }
                    } else {
                        view.findViewById(R.id.cart_item_view).setVisibility(View.GONE);
                        view.findViewById(R.id.empty_cart_text).setVisibility(View.VISIBLE);
                    }

                }
            });
        } else {
            getChildFragmentManager().popBackStack();
        }
    }
}