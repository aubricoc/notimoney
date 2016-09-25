package cat.aubricoc.notimoney;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
        Query query = database.createAllDocumentsQuery();
        try {
            QueryEnumerator queryEnumerator = query.run();
            while (queryEnumerator.hasNext()) {
                QueryRow row = queryEnumerator.next();
                Document document = row.getDocument();
                String day = (String) document.getProperty("day");
                if (today.equals(day)) {
                    return true;
                }
            }
            return false;
        } catch (CouchbaseLiteException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Rate> getAllRates(Context context) {
        Database database = getDatabase(context);
        Query query = database.createAllDocumentsQuery();
        try {
            List<Rate> list = new ArrayList<>();
            QueryEnumerator queryEnumerator = query.run();
            while (queryEnumerator.hasNext()) {
                QueryRow row = queryEnumerator.next();
                Document document = row.getDocument();
                String day = (String) document.getProperty("day");
                Double rate = (Double) document.getProperty("rate");
                list.add(new Rate(DATE_FORMAT.parse(day), rate));
            }
            return list;
        } catch (CouchbaseLiteException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void clearRates(Context context) {
        Database database = getDatabase(context);
        try {
            database.delete();
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
