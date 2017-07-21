package android.os;

public interface Parcelable {
  int CONTENTS_FILE_DESCRIPTOR = 1;
  int PARCELABLE_WRITE_RETURN_VALUE = 1;

  int describeContents();

  void writeToParcel(Parcel var1, int var2);

  public interface ClassLoaderCreator<T> extends Parcelable.Creator<T> {
    T createFromParcel(Parcel var1, ClassLoader var2);
  }

  public interface Creator<T> {
    T createFromParcel(Parcel var1);

    T[] newArray(int var1);
  }
}
