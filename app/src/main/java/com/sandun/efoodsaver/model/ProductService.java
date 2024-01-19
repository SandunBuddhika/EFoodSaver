package com.sandun.efoodsaver.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sandun.efoodsaver.dto.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private static final String TAG = Product.class.getName();
    private FirebaseFirestore firestore;
    private List<Product> products;
    private Product product;

    public ProductService() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void getProduct(List<Product> list, DoProcess process) {
        Log.i(TAG, "Started...");
        new Thread(() -> {
            products = list;
            firestore.collection("product").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.i(TAG, "Complete...");
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                        for (DocumentSnapshot doc : documentSnapshots) {
                            Product product = doc.toObject(Product.class);
                            product.setId(doc.getId());
                            products.add(product);
                        }
                        if (process != null) {
                            process.process();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG, "ERROR: " + e.getMessage());
                }
            });

        }).start();
    }

    public void getProduct(List<Product> list, DoProcess process, String search) {
        Log.i(TAG, "Started...");
        System.out.println(search + " PS");
        new Thread(() -> {
            products = list;
            CollectionReference reference = firestore.collection("product");
            List<Task<QuerySnapshot>> tasks = new ArrayList<>();
            tasks.add(reference.whereEqualTo("name", search).get());
            tasks.add(reference.whereEqualTo("category", search).get());
            tasks.add(reference.whereEqualTo("type", search).get());

            Task<List<QuerySnapshot>> listTask = Tasks.whenAllSuccess(tasks);
            listTask.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
                @Override
                public void onSuccess(List<QuerySnapshot> querySnapshots) {
                    if (!querySnapshots.isEmpty()) {
                        for (QuerySnapshot snapshot : querySnapshots) {
                            List<DocumentSnapshot> documentSnapshots = snapshot.getDocuments();
                            for (DocumentSnapshot doc : documentSnapshots) {
                                Product product = doc.toObject(Product.class);
                                product.setId(doc.getId());
                                products.add(product);
                            }
                        }
                        if (process != null) {
                            process.process();
                        }
                    }
                }
            });
        }).start();
    }

    public List<Product> getAllProduct() {
        return null;
    }


}
