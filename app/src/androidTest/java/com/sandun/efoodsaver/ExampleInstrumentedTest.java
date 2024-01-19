package com.sandun.efoodsaver;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sandun.efoodsaver.dto.Product;

import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.sandun.efoodsaver", appContext.getPackageName());

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.isComplete()) {
                    QuerySnapshot snapshot = task.getResult();
                    List<DocumentSnapshot> documentSnapshots = snapshot.getDocuments();
                    for (DocumentSnapshot doc : documentSnapshots) {
                        doc.getId();
                        Product product = doc.toObject(Product.class);
                        product.getName();

                    }
                } else {
                    System.out.println("Error!!");
                }
            }
        });


        firestore.collection("items").document("1").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                Product product =doc.toObject(Product.class);
                product.getName();
            }
        });
    }
}