package android.os;

import android.os.BaseBundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class PersistableBundle extends BaseBundle implements Cloneable, Parcelable {
  public static final Creator<PersistableBundle> CREATOR = null;
  public static final PersistableBundle EMPTY = null;

  public PersistableBundle() {
    throw new RuntimeException("Stub!");
  }

  public PersistableBundle(int capacity) {
    throw new RuntimeException("Stub!");
  }

  public PersistableBundle(PersistableBundle b) {
    throw new RuntimeException("Stub!");
  }

  public Object clone() {
    throw new RuntimeException("Stub!");
  }

  public void putPersistableBundle(String key, PersistableBundle value) {
    throw new RuntimeException("Stub!");
  }

  public PersistableBundle getPersistableBundle(String key) {
    throw new RuntimeException("Stub!");
  }

  public int describeContents() {
    throw new RuntimeException("Stub!");
  }

  public void writeToParcel(Parcel parcel, int flags) {
    throw new RuntimeException("Stub!");
  }

  public synchronized String toString() {
    throw new RuntimeException("Stub!");
  }
}
