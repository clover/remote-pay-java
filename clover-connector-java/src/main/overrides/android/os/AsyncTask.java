package android.os;


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class AsyncTask<Params, Progress, Result> {
  Executor executor = Executors.newSingleThreadExecutor();
  public void execute(final Params... args) {
    executor.execute(new Runnable() {
      @Override public void run() {
        onPreExecute();
        executor.execute(new Runnable(){
          @Override public void run() {
            final Result result = doInBackground(args);
            executor.execute(new Runnable() {
              @Override public void run() {
                onPostExecute(result);
              }
            });
          }
        });

      }
    });
  }

  protected void onPreExecute(){}
  protected abstract Result doInBackground(Params... params);
  protected void onPostExecute(Result result){}
}
