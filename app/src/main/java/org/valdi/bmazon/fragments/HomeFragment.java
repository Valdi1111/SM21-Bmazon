package org.valdi.bmazon.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import org.valdi.bmazon.R;
import org.valdi.bmazon.fragments.home.HomeCategoryFilter;
import org.valdi.bmazon.fragments.home.HomePriceFilter;
import org.valdi.bmazon.fragments.home.ProductCardAdapter;
import org.valdi.bmazon.model.Category;
import org.valdi.bmazon.model.ShopProduct;
import org.valdi.bmazon.network.NetworkUtil;
import org.valdi.bmazon.utils.Util;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private static final String KEY_CATEGORIES = "categories";
    private static final String KEY_CATEGORY = "filter_category";
    private static final String KEY_MAX_PRICE = "filter_max_price";
    private static final String KEY_MIN_PRICE = "filter_min_price";
    private static final String KEY_SEARCH = "filter_search";
    private CompositeSubscription subscriptions;
    private ProductCardAdapter adapter;
    private List<Category> categories;
    private boolean canLoadMore = true;
    private int filterCategory = 0;
    private double filterMaxPrice = -1;
    private double filterMinPrice = -1;
    private String filterSearch = "";
    private final int limit = 12;
    private int offset = 0;

    private DrawerLayout drawer;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.subscriptions = new CompositeSubscription();
        this.getParentFragmentManager().setFragmentResultListener(
                HomePriceFilter.KEY,
                this,
                (requestKey, result) -> {
                    this.filterMaxPrice = result.getDouble(HomePriceFilter.MAX_PRICE_PARAM);
                    this.filterMinPrice = result.getDouble(HomePriceFilter.MIN_PRICE_PARAM);
                    this.reloadProducts();
                }
        );
        this.getParentFragmentManager().setFragmentResultListener(
                HomeCategoryFilter.KEY,
                this,
                (requestKey, result) -> {
                    this.filterCategory = result.getInt(HomeCategoryFilter.CATEGORY_PARAM);
                    this.reloadProducts();
                }
        );
        if (savedInstanceState != null) {
            this.categories = savedInstanceState.getParcelableArrayList(KEY_CATEGORIES);
            this.filterCategory = savedInstanceState.getInt(KEY_CATEGORY);
            this.filterMaxPrice = savedInstanceState.getDouble(KEY_MAX_PRICE);
            this.filterMinPrice = savedInstanceState.getDouble(KEY_MIN_PRICE);
            this.filterSearch = savedInstanceState.getString(KEY_SEARCH);
        } else {
            this.categories = new ArrayList<>();
            this.loadCategories();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final MaterialToolbar toolbar = requireActivity().findViewById(R.id.app_bar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);
        toolbar.setTitle(R.string.nav_home);
        this.drawer = requireActivity().findViewById(R.id.navigation_drawer);
        final NavigationView navigation = requireActivity().findViewById(R.id.navigation);
        navigation.setCheckedItem(R.id.nav_home);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        this.adapter = new ProductCardAdapter(requireActivity(), getParentFragmentManager());
        final RecyclerView products = view.findViewById(R.id.products_list);
        products.setAdapter(this.adapter);
        products.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView view, int dx, int dy) {
                super.onScrolled(view, dx, dy);
                if (dy != 0 && canLoadMore && !products.canScrollVertically(1)) {
                    loadProducts();
                }
            }
        });
        this.reloadProducts();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.app_bar_home_menu, menu);
        final MenuItem item = menu.findItem(R.id.home_search);
        final SearchView view = (SearchView) item.getActionView();
        view.setOnCloseListener(() -> {
            filterSearch = "";
            reloadProducts();
            return false;
        });
        view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterSearch = query;
                reloadProducts();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        if (this.filterSearch != null && !this.filterSearch.isEmpty()) {
            view.post(() -> {
                item.expandActionView();
                view.setQuery(this.filterSearch, false);
                view.clearFocus();
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (this.drawer != null) {
                this.drawer.open();
            }
            return true;
        }
        if (item.getItemId() == R.id.home_filter) {
            final PopupMenu menu = new PopupMenu(requireActivity(), requireActivity().findViewById(R.id.home_filter));
            menu.inflate(R.menu.app_bar_home_filter);
            menu.setOnMenuItemClickListener(this::onMenuItemClick);
            menu.show();
            return true;
        }
        if (item.getItemId() == R.id.home_search) {
            // handle search
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean onMenuItemClick(final MenuItem item) {
        if (item.getItemId() == R.id.home_filter_categories) {
            final List<String> categories = new ArrayList<>();
            for (final Category category : this.categories) {
                categories.add(category.getName());
            }
            HomeCategoryFilter.newInstance(
                    categories,
                    filterCategory
            ).show(getParentFragmentManager(), HomeCategoryFilter.TAG);
            return true;
        }
        if (item.getItemId() == R.id.home_filter_price) {
            HomePriceFilter.newInstance(
                    filterMinPrice,
                    filterMaxPrice
            ).show(getParentFragmentManager(), HomePriceFilter.TAG);
            return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_CATEGORIES, new ArrayList<>(this.categories));
        outState.putInt(KEY_CATEGORY, this.filterCategory);
        outState.putDouble(KEY_MAX_PRICE, this.filterMaxPrice);
        outState.putDouble(KEY_MIN_PRICE, this.filterMinPrice);
        outState.putString(KEY_SEARCH, this.filterSearch);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.subscriptions.unsubscribe();
    }

    private void reloadProducts() {
        this.canLoadMore = true;
        this.offset = 0;
        this.adapter.clear();
        this.loadProducts();
    }

    private void loadProducts() {
        Integer categoryId = null;
        if (this.filterCategory > 0) {
            categoryId = this.categories.get(this.filterCategory).getId();
        }
        Double maxPrice = null;
        if (this.filterMaxPrice >= 0) {
            maxPrice = this.filterMaxPrice;
        }
        Double minPrice = null;
        if (this.filterMinPrice >= 0) {
            minPrice = this.filterMinPrice;
        }
        String search = null;
        if (this.filterSearch != null && !this.filterSearch.isEmpty()) {
            search = this.filterSearch;
        }
        this.subscriptions.add(NetworkUtil
                .getRetrofit()
                .getProducts(categoryId, maxPrice, minPrice, search, this.limit, this.offset)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleProducts, this::handleError)
        );
    }

    private void loadCategories() {
        this.subscriptions.add(NetworkUtil
                .getRetrofit()
                .getCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleCategories, this::handleError)
        );
    }

    private void handleProducts(final List<ShopProduct> products) {
        this.adapter.addAll(products);
        this.offset += this.limit;
        if (products.size() < this.limit) {
            this.canLoadMore = false;
        }
    }

    private void handleCategories(final List<Category> categories) {
        this.categories.add(new Category(-1, getString(R.string.all_categories)));
        this.categories.addAll(categories);
    }

    private void handleError(final Throwable error) {
        Util.showNetworkError(requireContext(), TAG, error);
    }
}