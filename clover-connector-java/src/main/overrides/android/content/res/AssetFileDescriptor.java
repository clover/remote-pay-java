//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package android.content.res;

import android.os.Bundle;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AssetFileDescriptor implements Parcelable, Closeable {
  public static final Creator<AssetFileDescriptor> CREATOR = null;
  public static final long UNKNOWN_LENGTH = -1L;

  public AssetFileDescriptor(ParcelFileDescriptor fd, long startOffset, long length) {
    throw new RuntimeException("Stub!");
  }

  public AssetFileDescriptor(ParcelFileDescriptor fd, long startOffset, long length, Bundle extras) {
    throw new RuntimeException("Stub!");
  }

  public ParcelFileDescriptor getParcelFileDescriptor() {
    throw new RuntimeException("Stub!");
  }

  public FileDescriptor getFileDescriptor() {
    throw new RuntimeException("Stub!");
  }

  public long getStartOffset() {
    throw new RuntimeException("Stub!");
  }

  public Bundle getExtras() {
    throw new RuntimeException("Stub!");
  }

  public long getLength() {
    throw new RuntimeException("Stub!");
  }

  public long getDeclaredLength() {
    throw new RuntimeException("Stub!");
  }

  public void close() throws IOException {
    throw new RuntimeException("Stub!");
  }

  public FileInputStream createInputStream() throws IOException {
    throw new RuntimeException("Stub!");
  }

  public FileOutputStream createOutputStream() throws IOException {
    throw new RuntimeException("Stub!");
  }

  public String toString() {
    throw new RuntimeException("Stub!");
  }

  public int describeContents() {
    throw new RuntimeException("Stub!");
  }

  public void writeToParcel(Parcel out, int flags) {
    throw new RuntimeException("Stub!");
  }

  public static class AutoCloseOutputStream extends android.os.ParcelFileDescriptor.AutoCloseOutputStream {
    public AutoCloseOutputStream(AssetFileDescriptor fd) throws IOException {
      super((ParcelFileDescriptor)null);
      throw new RuntimeException("Stub!");
    }

    public void write(byte[] buffer, int offset, int count) throws IOException {
      throw new RuntimeException("Stub!");
    }

    public void write(byte[] buffer) throws IOException {
      throw new RuntimeException("Stub!");
    }

    public void write(int oneByte) throws IOException {
      throw new RuntimeException("Stub!");
    }
  }

  public static class AutoCloseInputStream extends android.os.ParcelFileDescriptor.AutoCloseInputStream {
    public AutoCloseInputStream(AssetFileDescriptor fd) throws IOException {
      super((ParcelFileDescriptor)null);
      throw new RuntimeException("Stub!");
    }

    public int available() throws IOException {
      throw new RuntimeException("Stub!");
    }

    public int read() throws IOException {
      throw new RuntimeException("Stub!");
    }

    public int read(byte[] buffer, int offset, int count) throws IOException {
      throw new RuntimeException("Stub!");
    }

    public int read(byte[] buffer) throws IOException {
      throw new RuntimeException("Stub!");
    }

    public long skip(long count) throws IOException {
      throw new RuntimeException("Stub!");
    }

    public void mark(int readlimit) {
      throw new RuntimeException("Stub!");
    }

    public boolean markSupported() {
      throw new RuntimeException("Stub!");
    }

    public synchronized void reset() throws IOException {
      throw new RuntimeException("Stub!");
    }
  }
}
