//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package android.graphics;

import android.graphics.Path;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Region implements Parcelable {
  public static final Creator<Region> CREATOR = null;

  public Region() {
    throw new RuntimeException("Stub!");
  }

  public Region(Region region) {
    throw new RuntimeException("Stub!");
  }

  public Region(Rect r) {
    throw new RuntimeException("Stub!");
  }

  public Region(int left, int top, int right, int bottom) {
    throw new RuntimeException("Stub!");
  }

  public void setEmpty() {
    throw new RuntimeException("Stub!");
  }

  public boolean set(Region region) {
    throw new RuntimeException("Stub!");
  }

  public boolean set(Rect r) {
    throw new RuntimeException("Stub!");
  }

  public boolean set(int left, int top, int right, int bottom) {
    throw new RuntimeException("Stub!");
  }

  public boolean setPath(Path path, Region clip) {
    throw new RuntimeException("Stub!");
  }

  public native boolean isEmpty();

  public native boolean isRect();

  public native boolean isComplex();

  public Rect getBounds() {
    throw new RuntimeException("Stub!");
  }

  public boolean getBounds(Rect r) {
    throw new RuntimeException("Stub!");
  }

  public Path getBoundaryPath() {
    throw new RuntimeException("Stub!");
  }

  public boolean getBoundaryPath(Path path) {
    throw new RuntimeException("Stub!");
  }

  public native boolean contains(int var1, int var2);

  public boolean quickContains(Rect r) {
    throw new RuntimeException("Stub!");
  }

  public native boolean quickContains(int var1, int var2, int var3, int var4);

  public boolean quickReject(Rect r) {
    throw new RuntimeException("Stub!");
  }

  public native boolean quickReject(int var1, int var2, int var3, int var4);

  public native boolean quickReject(Region var1);

  public void translate(int dx, int dy) {
    throw new RuntimeException("Stub!");
  }

  public native void translate(int var1, int var2, Region var3);

  public final boolean union(Rect r) {
    throw new RuntimeException("Stub!");
  }

  public boolean op(Rect r, Region.Op op) {
    throw new RuntimeException("Stub!");
  }

  public boolean op(int left, int top, int right, int bottom, Region.Op op) {
    throw new RuntimeException("Stub!");
  }

  public boolean op(Region region, Region.Op op) {
    throw new RuntimeException("Stub!");
  }

  public boolean op(Rect rect, Region region, Region.Op op) {
    throw new RuntimeException("Stub!");
  }

  public boolean op(Region region1, Region region2, Region.Op op) {
    throw new RuntimeException("Stub!");
  }

  public String toString() {
    throw new RuntimeException("Stub!");
  }

  public int describeContents() {
    throw new RuntimeException("Stub!");
  }

  public void writeToParcel(Parcel p, int flags) {
    throw new RuntimeException("Stub!");
  }

  public boolean equals(Object obj) {
    throw new RuntimeException("Stub!");
  }

  protected void finalize() throws Throwable {
    throw new RuntimeException("Stub!");
  }

  public static enum Op {
    DIFFERENCE,
    INTERSECT,
    REPLACE,
    REVERSE_DIFFERENCE,
    UNION,
    XOR;

    private Op() {
    }
  }
}
