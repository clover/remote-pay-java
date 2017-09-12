package android.os;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AsyncTask<Params, Progress, Result> {
  public void execute(final Params... args) {
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.execute(new Runnable() {
      @Override public void run() {
        onPreExecute();
        executor.execute(new Runnable(){
          @Override public void run() {
            final Result result = doInBackground(args);
            executor.execute(new Runnable() {
              @Override public void run() {
                onPostExecute(result);
                executor.shutdown();
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
