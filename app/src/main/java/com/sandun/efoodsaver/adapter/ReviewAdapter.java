package com.sandun.efoodsaver.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sandun.efoodsaver.R;
import com.sandun.efoodsaver.dto.Review;
import com.sandun.efoodsaver.dto.User;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private final List<Review> reviewList;
    private final FirebaseFirestore firestore;
    private int limit;

    public ReviewAdapter(List<Review> reviewList, int limit) {
        this.reviewList = reviewList;
        firestore = FirebaseFirestore.getInstance();
        if (limit != 0) {
            this.limit = limit;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.review_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review currentReview = reviewList.get(position);
        firestore.collection("user").document(String.valueOf(currentReview.getuId())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);
                    if (user != null) {
                        holder.username.setText(user.getfName());
                    }
                }
            }
        });
        holder.comment.setText(currentReview.getMessage());
//        need add this
//        holder.userImg.setText();

    }

    @Override
    public int getItemCount() {
        if (reviewList.size() < limit) {
            return reviewList.size();
        } else {
            return limit;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImg;
        TextView username;
        TextView comment;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.userImg = view.findViewById(R.id.review_user_image);
            this.username = view.findViewById(R.id.review_username);
            this.comment = view.findViewById(R.id.review_comment);
        }
    }
}
