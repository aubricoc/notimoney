package cat.aubricoc.notimoney;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;

public class CurrencyService {

    public static void getRate(Context context) {
        Log.i("notimoney", "Get rate");
        if (CouchbaseService.haveTodayRate(context)) {
            Log.i("notimoney", "Today have rate");
            return;
        }
        new GetCurrency(context).execute("EUR", "COP");
    }

    static class GetCurrency extends AsyncTask<String, Void, Double> {

        private Context context;

        GetCurrency(Context context) {
            this.context = context;
        }

        @Override
        protected Double doInBackground(String... strings) {
            String url = "https://openexchangerates.org/api/latest.json?app_id=05201aec1d724ba69fd8d7dc807c8142";
            try {
                URL u = new URL(url);
                InputStream inputStream = u.openStream();
                StringBuilder sb = new StringBuilder();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line);
                }
                JSONObject jsonObject = new JSONObject(sb.toString());
                JSONObject rates = jsonObject.getJSONObject("rates");
                double from = rates.getDouble(strings[0]);
                double to = rates.getDouble(strings[1]);

                return to / from;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(Double result) {

            CouchbaseService.saveRate(context, result);
            Log.i("notimoney", "Rate saved");

            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context).setContentText("El peso esta a " + result)
                            .setContentTitle("Que pases un bien día amor!").setSmallIcon(android.R.drawable.stat_sys_warning);

            Notification notification = mBuilder.build();

            mNotificationManager.notify(new Random().nextInt(), notification);

        }
    }
}
