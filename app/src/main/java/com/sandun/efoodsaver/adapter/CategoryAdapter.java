package com.sandun.efoodsaver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sandun.efoodsaver.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private static final String TAG = CategoryAdapter.class.getName();
    private ArrayList<String> category;
    private Context context;

    public CategoryAdapter(ArrayList<String> category, Context context) {
        this.category = category;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.category_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        String category = this.category.get(position);
        holder.cName.setText(category);
        holder.cImage.setImageDrawable(context.getDrawable(R.drawable.bread_icon));
    }

    @Override
    public int getItemCount() {
        return category.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cName;
        ImageView cImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cName = itemView.findViewById(R.id.category_name);
            this.cImage = itemView.findViewById(R.id.category_img);
        }
    }
}
