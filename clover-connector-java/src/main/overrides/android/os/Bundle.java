//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package android.os;

import android.os.BaseBundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.os.Parcelable.Creator;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseArray;
import java.io.Serializable;
import java.util.ArrayList;

public final class Bundle extends BaseBundle implements Cloneable, Parcelable {
  public static final Creator<Bundle> CREATOR = null;
  public static final Bundle EMPTY = null;

  public Bundle() {
    
  }

  public Bundle(ClassLoader loader) {
    
  }

  public Bundle(int capacity) {
    
  }

  public Bundle(Bundle b) {
    
  }

  public Bundle(PersistableBundle b) {
    
  }

  public void setClassLoader(ClassLoader loader) {
    
  }

  public ClassLoader getClassLoader() {
    return null;
  }

  public Object clone() {
    return null;
  }

  public void clear() {
    
  }

  public void putAll(Bundle bundle) {
    
  }

  public boolean hasFileDescriptors() {
    return false;
  }

  public void putBoolean(String key, boolean value) {
    
  }

  public void putByte(String key, byte value) {
    
  }

  public void putChar(String key, char value) {
    
  }

  public void putShort(String key, short value) {
    
  }

  public void putFloat(String key, float value) {
    
  }

  public void putCharSequence(String key, CharSequence value) {
    
  }

  public void putParcelable(String key, Parcelable value) {
    
  }

  public void putSize(String key, Size value) {
    
  }

  public void putSizeF(String key, SizeF value) {
    
  }

  public void putParcelableArray(String key, Parcelable[] value) {
    
  }

  public void putParcelableArrayList(String key, ArrayList<? extends Parcelable> value) {
    
  }

  public void putSparseParcelableArray(String key, SparseArray<? extends Parcelable> value) {
    
  }

  public void putIntegerArrayList(String key, ArrayList<Integer> value) {
    
  }

  public void putStringArrayList(String key, ArrayList<String> value) {
    
  }

  public void putCharSequenceArrayList(String key, ArrayList<CharSequence> value) {
    
  }

  public void putSerializable(String key, Serializable value) {
    
  }

  public void putBooleanArray(String key, boolean[] value) {
    
  }

  public void putByteArray(String key, byte[] value) {
    
  }

  public void putShortArray(String key, short[] value) {
    
  }

  public void putCharArray(String key, char[] value) {
    
  }

  public void putFloatArray(String key, float[] value) {
    
  }

  public void putCharSequenceArray(String key, CharSequence[] value) {
    
  }

  public void putBundle(String key, Bundle value) {
    
  }

  public void putBinder(String key, IBinder value) {
    
  }

  public boolean getBoolean(String key) {
    return false;
  }

  public boolean getBoolean(String key, boolean defaultValue) {
    return false;
  }

  public byte getByte(String key) {
    return 0;
  }

  public Byte getByte(String key, byte defaultValue) {
    return null;
  }

  public char getChar(String key) {
    return 0;
  }

  public char getChar(String key, char defaultValue) {
    return 0;
  }

  public short getShort(String key) {
    return 0;
  }

  public short getShort(String key, short defaultValue) {
    return 0;
  }

  public float getFloat(String key) {
    return 0;
  }

  public float getFloat(String key, float defaultValue) {
    return 0;
  }

  public CharSequence getCharSequence(String key) {
    return null;
  }

  public CharSequence getCharSequence(String key, CharSequence defaultValue) {
    return null;
  }

  public Size getSize(String key) {
    return null;
  }

  public SizeF getSizeF(String key) {
    return null;
  }

  public Bundle getBundle(String key) {
    return null;
  }

  public <T extends Parcelable> T getParcelable(String key) {
    return null;
  }

  public Parcelable[] getParcelableArray(String key) {
    return null;
  }

  public <T extends Parcelable> ArrayList<T> getParcelableArrayList(String key) {
    return null;
  }

  public <T extends Parcelable> SparseArray<T> getSparseParcelableArray(String key) {
    return null;
  }

  public Serializable getSerializable(String key) {
    return null;
  }

  public ArrayList<Integer> getIntegerArrayList(String key) {
    return null;
  }

  public ArrayList<String> getStringArrayList(String key) {
    return null;
  }

  public ArrayList<CharSequence> getCharSequenceArrayList(String key) {
    return null;
  }

  public boolean[] getBooleanArray(String key) {
    return null;
  }

  public byte[] getByteArray(String key) {
    return null;
  }

  public short[] getShortArray(String key) {
    return null;
  }

  public char[] getCharArray(String key) {
    return null;
  }

  public float[] getFloatArray(String key) {
    return null;
  }

  public CharSequence[] getCharSequenceArray(String key) {
    return null;
  }

  public IBinder getBinder(String key) {
    return null;
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel parcel, int flags) {
    
  }

  public void readFromParcel(Parcel parcel) {
    
  }

  public synchronized String toString() {
    return null;
  }
}
