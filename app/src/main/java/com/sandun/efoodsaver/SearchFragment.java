package com.sandun.efoodsaver;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class SearchFragment extends Fragment {

    private View view;
    private String text;
    private FragmentManager fragmentManager;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String search) {
        SearchFragment fragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("search", search);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        fragmentManager = getChildFragmentManager();
        Bundle bundle = getArguments();
        if (bundle != null) {
            search(bundle.getString("search", ""));
        } else {
            search("");
        }

        EditText searchTextField = view.findViewById(R.id.search_field);
        CardView cardView = view.findViewById(R.id.seach_btn);
        cardView.setOnClickListener(v -> {
            String searchText = searchTextField.getText().toString();
            System.out.println(searchText);
            search(searchText);
        });
        return view;
    }

    public void search(String search) {
        Bundle bundle = new Bundle();
        if (search != null && !search.isEmpty()) {
            bundle.putString("search", search);
        }
        bundle.putInt("orientation", ProductListFragment.ORIENTATION_VERTICAL);
        bundle.putInt("layout", ProductListFragment.GRID);
        bundle.putInt("gridSize", 2);
        bundle.putInt("limit", 6);
        fragmentManager.beginTransaction().replace(R.id.search_fragment_container, ProductListFragment.class, bundle).setReorderingAllowed(true).addToBackStack("search_result_list").commit();
    }
}