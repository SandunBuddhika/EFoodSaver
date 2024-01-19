package com.sandun.efoodsaver;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;
import com.sandun.efoodsaver.adapter.CategoryAdapter;
import com.sandun.efoodsaver.model.CartService;
import com.sandun.efoodsaver.model.IsLogIn;
import com.sandun.efoodsaver.model.WaitAlertPopUp;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private View view;
    private ArrayList<String> category;
    private RecyclerView categoryView;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        categoryView = view.findViewById(R.id.category);
        if (IsLogIn.user != null) {
            TextView textView = view.findViewById(R.id.textView2);
            textView.setVisibility(View.VISIBLE);
            textView.setText("Welcome Back " + IsLogIn.user.getFName());
            view.findViewById(R.id.view).setVisibility(View.VISIBLE);
        }

        category = new ArrayList<>();
        category.add("All");
        category.add("Bread");
        category.add("Butter");
        category.add("Cake");
        category.add("Vegetables");

        CategoryAdapter categoryAdapter = new CategoryAdapter(category, view.getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        categoryView.setLayoutManager(layoutManager);
        categoryView.setAdapter(categoryAdapter);
        categoryAdapter.notifyDataSetChanged();

        Bundle arguments = new Bundle();
        arguments.putString("title", "Recommended Products");
        arguments.putInt("limit", 8);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().add(R.id.home_card_container, ProductListFragment.class, arguments).setReorderingAllowed(true).addToBackStack("recommended_products").commit();

        Bundle arguments2 = new Bundle();
        arguments2.putString("title", "Best Product");
        arguments2.putInt("orientation", ProductListFragment.ORIENTATION_VERTICAL);
        arguments2.putInt("layout", ProductListFragment.GRID);
        arguments2.putInt("gridSize", 2);
        arguments2.putInt("limit", 6);

        fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().add(R.id.home_card_container, ProductListFragment.class, arguments2).setReorderingAllowed(true).addToBackStack("best_products").commit();


        TextView textView = view.findViewById(R.id.select_your_location);
        textView.setOnClickListener(v -> {
            FragmentActivity activity = getActivity();
            activity.startActivity(new Intent(activity, SelectLocationActivity.class));
        });

        return view;
    }
}