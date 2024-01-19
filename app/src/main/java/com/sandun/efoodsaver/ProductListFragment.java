package com.sandun.efoodsaver;

import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sandun.efoodsaver.adapter.ProductAdapter;
import com.sandun.efoodsaver.dto.Product;
import com.sandun.efoodsaver.model.DoProcess;
import com.sandun.efoodsaver.model.ProductService;

import java.util.ArrayList;
import java.util.List;

public class ProductListFragment extends Fragment {
    private RecyclerView productsRecyclerView;
    private List<Product> productList;
    private String title;
    private String search;
    private View view;
    private int layout = 1;
    private int productLimit;
    private int orientationType = RecyclerView.HORIZONTAL;
    private int gridSize = 1;
    public static final int LINE = 1;
    public static final int GRID = 2;
    public static final int ORIENTATION_VERTICAL = RecyclerView.VERTICAL;
    public static final int ORIENTATION_HORIZONTAL = RecyclerView.HORIZONTAL;

    public static ProductListFragment newInstance() {
        ProductListFragment fragment = new ProductListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_list, container, false);
        setUpArguments(getArguments());

        productsRecyclerView = view.findViewById(R.id.horizontal_line_rc_view);
        productList = new ArrayList<>();
        ProductAdapter adapter = new ProductAdapter(productList, view.getContext(), productLimit, getParentFragment().getParentFragmentManager());
        if (layout == LINE) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(orientationType);
            productsRecyclerView.setLayoutManager(layoutManager);
        } else {
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), gridSize);
            layoutManager.setOrientation(orientationType);
            productsRecyclerView.clearOnScrollListeners();
            productsRecyclerView.setLayoutManager(layoutManager);
        }
        productsRecyclerView.setAdapter(adapter);
        ProductService productService = new ProductService();
        DoProcess process = new DoProcess() {
            @Override
            public void process() {
                adapter.notifyDataSetChanged();
                if (layout != LINE) {
                    int size = productList.size() / 2 + (productList.size() % 2 > 0 ? 1 : 0);
                    productsRecyclerView.setMinimumHeight(660 * size);
                }
            }
        };
        if (search.isEmpty()) {
            productService.getProduct(productList, process);
        } else {
            productService.getProduct(productList, process, search);
        }
        return view;
    }

    private void setUpArguments(Bundle bundle) {
        if (bundle != null) {
            TextView titleView = view.findViewById(R.id.product_list_title);
            title = bundle.getString("title", "Title");
            if (title.equals("Title") || title.isEmpty()) {
                titleView.setVisibility(View.GONE);
            } else {
                titleView.setText(title);
            }
            search = bundle.getString("search", "");
            productLimit = bundle.getInt("limit", 8);
            orientationType = bundle.getInt("orientation", ORIENTATION_HORIZONTAL);
            layout = bundle.getInt("layout", LINE);
            gridSize = bundle.getInt("gridSize", 1);
        }
    }
}