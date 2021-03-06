
package io.intelehealth.client.activities.search_patient_activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import io.intelehealth.client.R;
import io.intelehealth.client.activities.patient_detail_activity.PatientDetailActivity;
import io.intelehealth.client.database.LocalRecordsDatabaseHelper;

/**
 * This class helps to search for a patient from list of existing patients.
 */

public class SearchPatientActivity extends AppCompatActivity {

    final String TAG = SearchPatientActivity.class.getSimpleName();

    LocalRecordsDatabaseHelper mDbHelper;
    SearchCursorAdapter mSearchAdapter;
    SearchView searchView;
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDbHelper = new LocalRecordsDatabaseHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_patient);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            doQuery(query);
        } else {
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            firstQuery();
        }

        // TODO: Clear Suggestions
        // SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
        // SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
        // suggestions.clearHistory();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XMLz
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setFocusable(true);
        searchView.requestFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("Hack", "in query text change");
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(SearchPatientActivity.this,
                        SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
                suggestions.clearHistory();
                doQuery(newText);
                return true;
            }
        });


        return true;
    }

    public void firstQuery() { // called in onCreate()
        ListView lvItems = (ListView) findViewById(R.id.listview_search);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String table = "patient";


        final Cursor searchCursor = db.rawQuery("SELECT * FROM " + table +
                " ORDER BY first_name ASC", null);
        try {
            // Setup cursor adapter and attach cursor adapter to the ListView
            mSearchAdapter = new SearchCursorAdapter(this, searchCursor, 0);
            if (mSearchAdapter.getCount() < 1) {
                noneFound(lvItems, query);
            } else if (searchCursor.moveToFirst()) {
                lvItems.setAdapter(mSearchAdapter);
                lvItems.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                                if (searchCursor.moveToPosition(position)) {
                                    Integer patientID = searchCursor.getInt(searchCursor.getColumnIndexOrThrow("_id"));
                                    Log.d(TAG, "" + patientID);
                                    String patientStatus = "returning";
                                    Intent intent = new Intent(SearchPatientActivity.this, PatientDetailActivity.class);
                                    intent.putExtra("patientID", patientID);
                                    intent.putExtra("status", patientStatus);
                                    intent.putExtra("tag", "");
                                    startActivity(intent);

                                }
                            }
                        });
            }


        } catch (Exception e) {
            Log.d("Search Activity", "Exception", e);

        }


    }

    /**
     * This method retrieves data from database and sends it via Intent to PatientDetailActivity.
     * This method can be used to search for details with a partial string also.
     *
     * @param query variable of type String
     * @return void
     */

    public void doQuery(String query) { // called in onCreate()
        String search = query.trim();
        ListView lvItems = (ListView) findViewById(R.id.listview_search);
        if (TextUtils.isEmpty(search)) {
            lvItems.setAdapter(null);
        } else {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            String table = "patient";


            final Cursor searchCursor = db.rawQuery("SELECT * FROM " + table +
                    " WHERE first_name LIKE " + "'" + search +
                    "%' OR last_name LIKE '" + search +
                    "%' OR openmrs_id LIKE '" + search +
                    "%' OR middle_name LIKE '" + search + "%' " +
                    "ORDER BY first_name ASC", null);
            try {
                // Setup cursor adapter and attach cursor adapter to the ListView
                mSearchAdapter = new SearchCursorAdapter(this, searchCursor, 0);
                if (mSearchAdapter.getCount() < 1) {
                    noneFound(lvItems, query);
                } else if (searchCursor.moveToFirst()) {
                    lvItems.setAdapter(mSearchAdapter);
                    lvItems.setOnItemClickListener(
                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                                    if (searchCursor.moveToPosition(position)) {
                                        Integer patientID = searchCursor.getInt(searchCursor.getColumnIndexOrThrow("_id"));
                                        Log.d(TAG, "" + patientID);
                                        String patientStatus = "returning";
                                        Intent intent = new Intent(SearchPatientActivity.this, PatientDetailActivity.class);
                                        intent.putExtra("patientID", patientID);
                                        intent.putExtra("status", patientStatus);
                                        intent.putExtra("tag", "");
                                        startActivity(intent);

                                    }
                                }
                            });
                }


            } catch (Exception e) {
                Log.d("Search Activity", "Exception", e);

            }

        }

    }


    /**
     * This method is called when no search result is found for patient.
     *
     * @param lvItems variable of type ListView
     * @param query   variable of type String
     */
    public void noneFound(ListView lvItems, String query) {
        ArrayAdapter<String> searchAdapter = new ArrayAdapter<>(this,
                R.layout.list_item_search,
                R.id.list_item_head, new ArrayList<String>());
        String errorMessage = getString(R.string.alert_none_found).replace("_", query);
        searchAdapter.add(errorMessage);
        lvItems.setAdapter(searchAdapter);
    }

}

