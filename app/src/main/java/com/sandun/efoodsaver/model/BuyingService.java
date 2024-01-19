package com.sandun.efoodsaver.model;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sandun.efoodsaver.dto.CartItemModel;
import com.sandun.efoodsaver.dto.CartProduct;
import com.sandun.efoodsaver.dto.InvoiceDTO;
import com.sandun.efoodsaver.dto.Product;
import com.sandun.efoodsaver.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BuyingService {
    private static final String TAG = BuyingService.class.getName();
    private List<CartItemModel> productList;
    private double total;
    private FirebaseFirestore firestore;
    private Context context;
    private User user;

    public BuyingService(List<CartItemModel> cartItemModel, Context context) {
        this.context = context;
        for (CartItemModel itemModel : cartItemModel) {
            Product p = itemModel.getProduct();
            if (p != null) {
                total += (p.getPrice() / 100) * p.getDiscount();
            }
        }
        this.productList = cartItemModel;
        firestore = FirebaseFirestore.getInstance();
        user = IsLogIn.getUser();
    }

    public void buyNow() {
        WaitAlertPopUp waitAlertPopUp = new WaitAlertPopUp(context);
        waitAlertPopUp.changeButtonState(WaitAlertPopUp.VISIBLE_STATE);
        if (productList != null && user != null && !productList.isEmpty()) {

            InvoiceDTO invoiceDTO = new InvoiceDTO(String.valueOf(user.getId()), total);
            firestore.collection("invoice").add(invoiceDTO).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    if (documentReference != null) {
                        CountDownLatch countDownLatch = new CountDownLatch(productList.size());
                        for (CartItemModel itemModel : productList) {
                            CartProduct cartProduct = new CartProduct(itemModel.getCount(), firestore.collection("product").document(itemModel.getProduct().getId()));
                            documentReference.collection("productList").document(itemModel.getProduct().getId()).set(cartProduct).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    countDownLatch.countDown();
                                }
                            });
                        }
                        successAlert(waitAlertPopUp);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    errorAlert(waitAlertPopUp);
                }
            });
        } else {
            errorAlert(waitAlertPopUp);
        }
    }

    private void errorAlert(WaitAlertPopUp waitAlertPopUp) {
        waitAlertPopUp.setUp("Something Went Wrong Try Again", WaitAlertPopUp.ERROR);
        waitAlertPopUp.customButtonSetUp((dialog, alertPopUp) -> {
            dialog.dismiss();
        });
        waitAlertPopUp.show();
    }

    private void successAlert(WaitAlertPopUp waitAlertPopUp) {
        waitAlertPopUp.setUp("Thank Your For Purchasing With EFoodSaver!", WaitAlertPopUp.SUCCESS);
        waitAlertPopUp.customButtonSetUp((dialog, alertPopUp) -> {
            dialog.dismiss();
        });
        waitAlertPopUp.show();
    }
}
