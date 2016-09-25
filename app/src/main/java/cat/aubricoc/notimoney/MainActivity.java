package cat.aubricoc.notimoney;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ComponentName receiver = new ComponentName(this, AlarmBroadcastReceiver.class);
        PackageManager pm = getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        AlarmService.configure(this);

        clearNotifications();
        showRates();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        clearNotifications();
        showRates();
    }

    private void clearNotifications() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private void showRates() {
        List<Rate> rates = CouchbaseService.getAllRates(this);
        Collections.sort(rates);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rates));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                CouchbaseService.clearRates(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
