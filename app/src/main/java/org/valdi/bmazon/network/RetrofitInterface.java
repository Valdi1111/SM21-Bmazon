package org.valdi.bmazon.network;

import com.google.gson.JsonObject;

import org.valdi.bmazon.model.BuyerData;
import org.valdi.bmazon.model.CartProduct;
import org.valdi.bmazon.model.Category;
import org.valdi.bmazon.model.LoggedUser;
import org.valdi.bmazon.model.Notification;
import org.valdi.bmazon.model.Payment;
import org.valdi.bmazon.model.Shipment;
import org.valdi.bmazon.model.order.Order;
import org.valdi.bmazon.model.order.OrderProduct;
import org.valdi.bmazon.model.order.OrderState;
import org.valdi.bmazon.model.product.Product;
import org.valdi.bmazon.model.ShopProduct;
import org.valdi.bmazon.model.product.ProductFaq;
import org.valdi.bmazon.model.product.ProductImage;
import org.valdi.bmazon.model.product.ProductRatings;
import org.valdi.bmazon.model.product.ProductReview;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface RetrofitInterface {

    // User

    @POST("auth/signin")
    Observable<LoginResponse> login(
            @Body LoginRequest request
    );

    @GET("buyer/profile")
    Observable<LoggedUser> getUserData();

    @GET("buyer/shipments")
    Observable<List<Shipment>> getShipments();

    @GET("buyer/payments")
    Observable<List<Payment>> getPayments();

    @Multipart
    @PUT("user/data/avatar")
    Observable<Void> postUserAvatar(
            @Part MultipartBody.Part image
    );

    @POST("buyer/profile")
    Observable<Void> postBuyerData(
            @Body() BuyerData request
    );

    // Notification

    @GET("user/notifications")
    Observable<List<Notification>> getNotifications();

    @GET("user/notifications/amount")
    Observable<JsonObject> getNotificationsAmount();

    @POST("user/notifications/{id}/read")
    Observable<Void> postNotificationRead(
            @Path("id") int id
    );

    // Home

    @GET("categories")
    Observable<List<Category>> getCategories();

    @GET("products")
    Observable<List<ShopProduct>> getProducts(
            @Query("category") Integer category,
            @Query("max_price") Double maxPrice,
            @Query("min_price") Double minPrice,
            @Query("search") String search,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset
    );

    // Product

    @GET("products/{id}")
    Observable<Product> getProduct(
            @Path("id") int id
    );

    @GET("products/{id}/images")
    Observable<List<ProductImage>> getProductImages(
            @Path("id") int id
    );

    @GET("products/{id}/faqs")
    Observable<List<ProductFaq>> getProductFaqs(
            @Path("id") int id
    );

    @GET("products/{id}/ratings")
    Observable<ProductRatings> getProductRatings(
            @Path("id") int id
    );

    @GET("products/{id}/reviews")
    Observable<List<ProductReview>> getProductReviews(
            @Path("id") int id,
            @Query("order_by") ProductReview.Order order
    );

    // Product faqs

    @POST("buyer/products/{id}/faqs")
    Observable<JsonObject> postFaq(
            @Path("id") int id,
            @Body() JsonObject request
    );

    @GET("buyer/faqs/{id}/upvote")
    Observable<JsonObject> getFaqUpvote(
            @Path("id") int id
    );

    @POST("buyer/faqs/{id}/upvote")
    Observable<Void> postFaqUpvote(
            @Path("id") int id,
            @Body JsonObject request
    );

    // Product reviews

    @Multipart
    @POST("buyer/products/{id}/reviews")
    Observable<JsonObject> postReview(
            @Path("id") int id,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("rating") RequestBody rating,
            @Part MultipartBody.Part image
    );

    @GET("buyer/reviews/{id}/helpful")
    Observable<JsonObject> getReviewHelpful(
            @Path("id") int id
    );

    @POST("buyer/reviews/{id}/helpful")
    Observable<Void> postReviewHelpful(
            @Path("id") int id
    );

    @DELETE("buyer/reviews/{id}/helpful")
    Observable<Void> deleteReviewHelpful(
            @Path("id") int id
    );

    // Cart Products

    @GET("buyer/cart")
    Observable<List<CartProduct>> getCart();

    @POST("buyer/cart")
    Observable<Void> postCart(
            @Body JsonObject request
    );

    @PUT("buyer/cart")
    Observable<Void> putCart(
            @Body JsonObject request
    );

    @DELETE("buyer/cart/{id}")
    Observable<Void> deleteCart(
            @Path("id") int id
    );

    // Checkout

    @POST("buyer/checkout")
    Observable<Void> postCheckout(
            @Body JsonObject request
    );

    // Order

    @GET("buyer/orders")
    Observable<List<Order>> getOrders();

    @GET("buyer/orders/{id}/products")
    Observable<List<OrderProduct>> getOrderProducts(
            @Path("id") int id
    );

    @GET("buyer/orders/{id}/states")
    Observable<List<OrderState>> getOrderStates(
            @Path("id") int id
    );

}
