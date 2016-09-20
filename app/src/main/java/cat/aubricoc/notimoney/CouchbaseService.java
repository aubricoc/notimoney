package cat.aubricoc.notimoney;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CouchbaseService {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

    public static void saveRate(Context context, Double rate) {
        Map<String, Object> data = new HashMap<>();
        data.put("rate", rate);
        data.put("day", DATE_FORMAT.format(new Date()));

        Database database = getDatabase(context);
        Document document = database.createDocument();

        try {
            document.putProperties(data);
        } catch (CouchbaseLiteException e) {
            try {
                document.delete();
            } catch (CouchbaseLiteException e1) {
                throw new RuntimeException(e1);
            }
            throw new RuntimeException(e);
        }
    }

    public static boolean haveTodayRate(Context context) {
        final String today = DATE_FORMAT.format(new Date());
        Database database = getDatabase(context);
        View view = database.getView("today");
        view.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                if (today.equals(document.get("day"))) {
                    emitter.emit(document.get("day"), document.get("rate"));
                }
            }
        }, "1");
        Query query = view.createQuery();
        try {
            QueryEnumerator queryEnumerator = query.run();
            return queryEnumerator.hasNext();
        } catch (CouchbaseLiteException e) {
            throw new RuntimeException(e);
        }
    }

    private static Database getDatabase(Context context) {
        try {
            Manager manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
            return manager.getDatabase("rates");
        } catch (IOException | CouchbaseLiteException e) {
            throw new RuntimeException(e);
        }
    }
}
