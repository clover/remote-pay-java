package android.os;

import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.io.FileDescriptor;

public interface IBinder {
  int DUMP_TRANSACTION = 1598311760;
  int FIRST_CALL_TRANSACTION = 1;
  int FLAG_ONEWAY = 1;
  int INTERFACE_TRANSACTION = 1598968902;
  int LAST_CALL_TRANSACTION = 16777215;
  int LIKE_TRANSACTION = 1598835019;
  int PING_TRANSACTION = 1599098439;
  int TWEET_TRANSACTION = 1599362900;

  String getInterfaceDescriptor() throws RemoteException;

  boolean pingBinder();

  boolean isBinderAlive();

  IInterface queryLocalInterface(String var1);

  void dump(FileDescriptor var1, String[] var2) throws RemoteException;

  void dumpAsync(FileDescriptor var1, String[] var2) throws RemoteException;

  boolean transact(int var1, Parcel var2, Parcel var3, int var4) throws RemoteException;

  void linkToDeath(IBinder.DeathRecipient var1, int var2) throws RemoteException;

  boolean unlinkToDeath(IBinder.DeathRecipient var1, int var2);

  public interface DeathRecipient {
    void binderDied();
  }
}
