package org.valdi.bmazon.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.valdi.bmazon.R;
import org.valdi.bmazon.fragments.product.FaqAdapter;
import org.valdi.bmazon.fragments.product.FaqAddFragment;
import org.valdi.bmazon.fragments.product.ReviewAdapter;
import org.valdi.bmazon.fragments.product.ReviewAddFragment;
import org.valdi.bmazon.model.AuthProvider;
import org.valdi.bmazon.model.product.Product;
import org.valdi.bmazon.model.product.ProductFaq;
import org.valdi.bmazon.model.product.ProductImage;
import org.valdi.bmazon.model.product.ProductRatings;
import org.valdi.bmazon.model.product.ProductReview;
import org.valdi.bmazon.network.NetworkUtil;
import org.valdi.bmazon.utils.Util;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductFragment extends Fragment {
    private static final String TAG = ProductFragment.class.getSimpleName();
    private static final String ARG_ID = "product_id";
    private CompositeSubscription subscriptions;
    private FaqAdapter faqsAdapter;
    private ReviewAdapter reviewsAdapter;
    private int id;

    private CarouselView images;
    private TextView title;
    private TextView seller;
    private AppCompatRatingBar rating;
    private TextView description;
    private TextView price;
    private TextView currentPrice;
    private TextView noFaqs;
    private TextView noReviews;

    public ProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id product id
     * @return A new instance of fragment ProductFragment.
     */
    public static ProductFragment newInstance(final int id) {
        final ProductFragment fragment = new ProductFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.subscriptions = new CompositeSubscription();
        this.getParentFragmentManager().setFragmentResultListener(
                FaqAddFragment.KEY,
                this,
                (requestKey, result) -> {
                    final String question = result.getString(FaqAddFragment.QUESTION_PARAM);
                    this.sendFaq(question);
                }
        );
        this.getParentFragmentManager().setFragmentResultListener(
                ReviewAddFragment.KEY,
                this,
                (requestKey, result) -> {
                    final String title = result.getString(ReviewAddFragment.TITLE_PARAM);
                    final String description = result.getString(ReviewAddFragment.DESCRIPTION_PARAM);
                    final int rating = result.getInt(ReviewAddFragment.RATING_PARAM);
                    this.sendReview(title, description, rating);
                }
        );
        if (getArguments() != null) {
            this.id = getArguments().getInt(ARG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final MaterialToolbar toolbar = requireActivity().findViewById(R.id.app_bar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setTitle(R.string.nav_product);
        final NavigationView navigation = requireActivity().findViewById(R.id.navigation);
        navigation.setCheckedItem(R.id.nav_home);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_product, container, false);
        // Product data
        this.title = view.findViewById(R.id.product_title);
        this.seller = view.findViewById(R.id.product_business);
        this.rating = view.findViewById(R.id.product_rating);
        this.description = view.findViewById(R.id.product_description);
        this.price = view.findViewById(R.id.product_price);
        this.currentPrice = view.findViewById(R.id.product_current_price);
        this.noFaqs = view.findViewById(R.id.product_no_faqs);
        this.noReviews = view.findViewById(R.id.product_no_reviews);
        // Product images
        this.images = view.findViewById(R.id.product_images);
        // Add to cart
        final Button addCart = view.findViewById(R.id.product_add_to_cart);
        addCart.setOnClickListener(v -> {
            addToCart();
        });
        // Product faqs
        this.faqsAdapter = new FaqAdapter(requireActivity(), this.subscriptions);
        final RecyclerView faqs = view.findViewById(R.id.product_faqs);
        faqs.setAdapter(this.faqsAdapter);
        final Button addFaq = view.findViewById(R.id.product_add_faq);
        addFaq.setOnClickListener(v -> {
            if (Util.isLogged(requireActivity())) {
                final FaqAddFragment dialog = FaqAddFragment.newInstance();
                dialog.show(getParentFragmentManager(), FaqAddFragment.TAG);
            } else {
                Util.showNotLoggedIn(requireContext());
            }
        });
        // Product reviews
        this.reviewsAdapter = new ReviewAdapter(requireActivity(), this.subscriptions);
        final RecyclerView reviews = view.findViewById(R.id.product_reviews);
        reviews.setAdapter(this.reviewsAdapter);
        final Button addReview = view.findViewById(R.id.product_add_review);
        addReview.setOnClickListener(v -> {
            if (Util.isLogged(requireActivity())) {
                final ReviewAddFragment dialog = ReviewAddFragment.newInstance();
                dialog.show(getParentFragmentManager(), ReviewAddFragment.TAG);
            } else {
                Util.showNotLoggedIn(requireContext());
            }
        });
        // Load all
        this.loadProduct();
        this.loadImages();
        this.loadFaqs();
        this.loadRatings();
        this.loadReviews();
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.subscriptions.unsubscribe();
    }

    private void loadProduct() {
        this.subscriptions.add(NetworkUtil
                .getRetrofit()
                .getProduct(this.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleProduct, this::handleError)
        );
    }

    private void loadImages() {
        this.subscriptions.add(NetworkUtil
                .getRetrofit()
                .getProductImages(this.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleImages, this::handleError)
        );
    }

    private void loadRatings() {
        this.subscriptions.add(NetworkUtil
                .getRetrofit()
                .getProductRatings(this.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleRatings, this::handleError)
        );
    }

    private void loadFaqs() {
        this.subscriptions.add(NetworkUtil
                .getRetrofit()
                .getProductFaqs(this.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleFaqs, this::handleError)
        );
    }

    private void loadReviews() {
        this.subscriptions.add(NetworkUtil
                .getRetrofit()
                .getProductReviews(this.id, ProductReview.Order.helpful)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleReviews, this::handleError)
        );
    }

    private void handleProduct(final Product product) {
        this.title.setText(product.getTitle());
        this.seller.setText(product.getBusinessName());
        this.description.setText(product.getDescription());
        Util.formatPriceFields(product.getPrice(), product.getDiscount(), this.price, this.currentPrice);
    }

    private void handleImages(final List<ProductImage> images) {
        this.images.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView view) {
                // TODO remove default product image
                Util.setProductImage(view, images.get(position).getPath());
                //Util.setProductImage(view, "50b197dcba2ac28aef23c9406235a69f");
            }
        });
        this.images.setPageCount(images.size());
    }

    private void handleRatings(final ProductRatings ratings) {
        this.rating.setRating(Double.valueOf(ratings.getAverage()).floatValue());
    }

    private void handleFaqs(final List<ProductFaq> faqs) {
        if (faqs.size() > 0) {
            this.noFaqs.setVisibility(ImageView.GONE);
        }
        this.faqsAdapter.addAll(faqs);
    }

    private void handleReviews(final List<ProductReview> reviews) {
        if (reviews.size() > 0) {
            this.noReviews.setVisibility(ImageView.GONE);
        }
        this.reviewsAdapter.addAll(reviews);
    }

    private void handleError(final Throwable error) {
        Util.showNetworkError(requireContext(), TAG, error);
    }

    private void addToCart() {
        final JsonObject request = new JsonObject();
        request.addProperty("product", this.id);
        request.addProperty("amount", 1);
        this.subscriptions.add(NetworkUtil
                .getRetrofit((AuthProvider) requireActivity())
                .postCart(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleAddCart, this::handleError)
        );
    }

    private void handleAddCart(final Void products) {
        Toast.makeText(requireContext(), R.string.cart_added_product, Toast.LENGTH_SHORT).show();
    }

    private void sendReview(final String title, final String description, final int rating) {
        final MediaType type = MediaType.parse("multipart/form-data");
        // TODO make photo
        //MultipartBody.Part image = MultipartBody.Part.createFormData("image");
        this.subscriptions.add(NetworkUtil
                .getRetrofit((AuthProvider) requireActivity())
                .postReview(
                        this.id,
                        RequestBody.create(type, title),
                        RequestBody.create(type, description),
                        RequestBody.create(type, String.valueOf(rating)),
                        null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleReviewAdd, this::handleError)
        );
    }

    private void handleReviewAdd(final JsonObject response) {
        // TODO add review to reviews
    }

    private void sendFaq(final String question) {
        final JsonObject json = new JsonObject();
        json.addProperty("question", question);
        this.subscriptions.add(NetworkUtil
                .getRetrofit((AuthProvider) requireActivity())
                .postFaq(this.id, json)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleFaqAdd, this::handleError)
        );
    }

    private void handleFaqAdd(final JsonObject response) {
        // TODO add faq to faqs
    }
}