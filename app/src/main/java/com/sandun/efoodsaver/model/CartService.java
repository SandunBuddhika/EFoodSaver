package com.sandun.efoodsaver.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.sandun.efoodsaver.dto.CartDTO;
import com.sandun.efoodsaver.dto.CartItemModel;
import com.sandun.efoodsaver.dto.CartProduct;
import com.sandun.efoodsaver.dto.Product;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CartService {
    private static final String TAG = CartService.class.getName();
    private FirebaseFirestore firestore;
    private String uId;

    public CartService(String uId) {
        this.uId = uId;
        firestore = FirebaseFirestore.getInstance();
    }

    public void removeAll() {
        ;
        firestore.collection("cart").document(uId).collection("productList").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot snapshot = task.getResult();
                    if (!snapshot.isEmpty()) {
                        List<Task<Void>> tasks = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : snapshot.getDocuments()) {
                            Task<Void> voidTask = documentSnapshot.getReference().delete();
                            tasks.add(voidTask);
                        }
                        Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                            @Override
                            public void onSuccess(List<Object> objects) {
                                firestore.collection("cart").document(uId).delete();
                            }
                        });
                    }
                }
            }
        });
    }

    public void getUserCartProduct(EventListener<QuerySnapshot> process) {
        firestore.collection("cart").document(uId).collection("productList").addSnapshotListener(process);
    }

    public void removeProduct(String pId) {
        firestore.collection("cart").document(uId).collection("productList").document(pId).delete();
    }

    public void changeItemCount(int count, String pid) {
        firestore.collection("cart").document(uId).collection("productList").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot snapshot = task.getResult();
                    if (!snapshot.isEmpty()) {
                        List<DocumentSnapshot> products = snapshot.getDocuments();
                        if (products.size() > 0) {
                            String modifyProductId = "-1";
                            CartProduct modifyProduct = null;
                            for (DocumentSnapshot cartProduct : products) {
                                CartProduct cProduct = cartProduct.toObject(CartProduct.class);
                                if (cProduct.getProduct().getId().equals(pid)) {
                                    cProduct.setCount(count);
                                    modifyProductId = cartProduct.getId();
                                    modifyProduct = cProduct;
                                    break;
                                }
                            }
                            if (modifyProduct != null && !modifyProductId.equals("-1")) {
                                firestore.collection("cart").document(uId).collection("productList").document(modifyProductId).set(modifyProduct);
                            }
                        }
                    }
                }
            }
        });
    }

    public void addProductToCart(String pId, DoProcess process, int count) {
        firestore.collection("product").document(pId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        firestore.collection("cart").document(uId).collection("productList").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                CartProduct product = null;
                                String docId = null;
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    List<DocumentSnapshot> cp = queryDocumentSnapshots.getDocuments();
                                    if (cp != null) {
                                        for (DocumentSnapshot doc : cp) {
                                            CartProduct cartProduct = doc.toObject(CartProduct.class);
                                            if (cartProduct != null) {
                                                if (cartProduct.getProduct().getId().equals(snapshot.getReference().getId())) {
                                                    cartProduct.setCount(cartProduct.getCount() + 1);
                                                    docId = doc.getId();
                                                    product = cartProduct;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                if (product == null) {
                                    product = new CartProduct(count, snapshot.getReference());
                                }
                                processOfAddCart(snapshot, process, product, docId, count);
                            }
                        });
                    } else {
                        Log.d(TAG, "Product Document With ID " + pId + " Does Not Exist.");
                    }
                } else {
                    Log.e(TAG, "Error Getting Product Document", task.getException());
                }
            }
        });
    }

    private void processOfAddCart(DocumentSnapshot snapshot, DoProcess process, CartProduct item, String docId, int count) {
        if (docId == null) {
            docId = snapshot.getId();
        }
        firestore.collection("cart").document(uId).collection("productList").document(docId).set(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (process != null) {
                    process.process();
                }
            }
        });
    }
}
