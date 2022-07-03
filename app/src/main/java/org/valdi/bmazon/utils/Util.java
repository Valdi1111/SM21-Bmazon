package org.valdi.bmazon.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.valdi.bmazon.R;
import org.valdi.bmazon.model.AuthProvider;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

public class Util {
    private static final DateFormat DATE_FORMAT_INPUT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

    public static void setAvatar(final ImageView view, final String id) {
        Picasso.get().load(Constants.AVATARS_URL + id).into(view);
    }

    public static void setProductImage(final ImageView view, final String id) {
        Picasso.get().load(Constants.PRODUCTS_IMAGE_URL + id).into(view);
    }

    public static void setReviewImage(final ImageView view, final String id) {
        if (id == null || id.isEmpty()) {
            view.setVisibility(ImageView.GONE);
            return;
        }
        Picasso.get().load(Constants.REVIEWS_IMAGE_URL + id).into(view);
        view.setVisibility(ImageView.VISIBLE);
    }

    public static double discount(final double price, final double discount) {
        BigDecimal bd = BigDecimal.valueOf(price * (100D - discount) / 100D);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String formatDate(final String value, final SimpleDateFormat formatter) {
        try {
            final Date date = DATE_FORMAT_INPUT.parse(value);
            if (date == null) {
                return "";
            }
            return formatter.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String formatCurrency(final double value) {
        final NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        format.setCurrency(Currency.getInstance("EUR"));
        return format.format(value);
    }

    public static void formatPriceFields(final double price, final double discount, final TextView priceView, final TextView currentPriceView) {
        priceView.setText(Util.formatCurrency(price));
        if (discount > 0) {
            priceView.setPaintFlags(priceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            currentPriceView.setText(Util.formatCurrency(discount(price, discount)));
        } else {
            priceView.setPaintFlags(priceView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            currentPriceView.setText("");
        }
    }

    public static void formatPriceField(final double price, final double discount, final TextView priceView) {
        if (discount > 0) {
            priceView.setText(Util.formatCurrency(discount(price, discount)));
        } else {
            priceView.setText(Util.formatCurrency(price));
        }
    }

    public static double getPositiveDoubleOr(final String value, final double defaultValue) {
        try {
            double min = Double.parseDouble(value);
            if (min < 0) {
                return defaultValue;
            }
            return min;
        } catch (Exception ignored) {
        }
        return defaultValue;
    }

    public static boolean isLogged(final Context context) {
        return context instanceof AuthProvider && ((AuthProvider) context).isLogged();
    }

    public static void showNetworkError(final Context context, final String tag, final Throwable error) {
        Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
        Log.w(tag, "Error loading data: " + error.getMessage(), error);
    }

    public static void showNotLoggedIn(final Context context) {
        Toast.makeText(context, R.string.not_logged_in, Toast.LENGTH_SHORT).show();
    }

    public static boolean isNetworkConnected(final Activity activity) {
        final ConnectivityManager manager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
}
