package com.sjoholm.olof.walmartlab;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    public final String productId;
    public final String productName;
    public final String shortDescription;
    public final String longDescription;
    public final String price;
    public final String reviewRating;
    public final String reviewCount;
    public final String productImage;
    public final boolean inStock;

    public Product(String productId, String productName, String shortDescription,
                   String longDescription, String price, String reviewRating, String reviewCount,
                   String productImage, boolean inStock) {
        this.productId = productId;
        this.productName = productName;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.price = price;
        this.reviewRating = reviewRating;
        this.reviewCount = reviewCount;
        this.inStock = inStock;
        this.productImage = productImage;
    }

    protected Product(Parcel in) {
        productId = in.readString();
        productName = in.readString();
        shortDescription = in.readString();
        longDescription = in.readString();
        price = in.readString();
        reviewRating = in.readString();
        reviewCount = in.readString();
        productImage = in.readString();
        inStock = in.readByte() != 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeString(productName);
        dest.writeString(shortDescription);
        dest.writeString(longDescription);
        dest.writeString(price);
        dest.writeString(reviewRating);
        dest.writeString(reviewCount);
        dest.writeString(productImage);
        dest.writeByte((byte) (inStock ? 1 : 0));
    }
}