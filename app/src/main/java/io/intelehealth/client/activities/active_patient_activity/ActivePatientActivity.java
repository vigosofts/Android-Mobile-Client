
package io.intelehealth.client.activities.active_patient_activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.intelehealth.client.R;
import io.intelehealth.client.activities.active_patient_activity.active_patient_adapter.ActivePatientAdapter;
import io.intelehealth.client.activities.home_activity.HomeActivity;
import io.intelehealth.client.database.LocalRecordsDatabaseHelper;
import io.intelehealth.client.objects.ActivePatientModel;
import io.intelehealth.client.services.ClientService;
import io.intelehealth.client.activities.active_patient_activity.active_patient_adapter.ActivePatientAdapter;

/**
 * This class retrieves information about patients with visits on current date and sets data to a RecyclerView.
 */
public class ActivePatientActivity extends AppCompatActivity {

    private SQLiteDatabase db;

    final String TAG = ActivePatientActivity.class.getSimpleName();
    Toolbar mToolbar;

    RecyclerView mActivePatientList;
    ActivePatientAdapter mActivePatientAdapter;

    LocalRecordsDatabaseHelper mDbHelper;
    AlertDialog.Builder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_patient);
        setTitle(R.string.title_activity_active_patient);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mActivePatientList = (RecyclerView) findViewById(R.id.today_patient_recycler_view);
        setSupportActionBar(mToolbar);

        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.ic_sort_white_24dp);
        mToolbar.setOverflowIcon(drawable);

        mDbHelper = new LocalRecordsDatabaseHelper(this);

        db = mDbHelper.getWritableDatabase();
        doQuery();
    }

    /**
     * This method retrieves visit details about patient for a particular date.
     *
     * @return void
     */
    private void doQuery() {
        List<ActivePatientModel> activePatientList = new ArrayList<>();
        Date cDate = new Date();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        String query =
                "SELECT visit._id, visit.patient_id, visit.start_datetime, visit.end_datetime," +
                        "visit.openmrs_visit_uuid, patient.first_name, patient.middle_name, patient.last_name, " +
                        "patient.date_of_birth,patient.openmrs_id,patient.phone_number FROM visit, patient WHERE visit.patient_id = patient._id " +
                        "AND visit.end_datetime IS NULL " +
                        "OR visit.end_datetime = '' " +
                        "ORDER BY visit.start_datetime ASC";
        //  "SELECT * FROM visit, patient WHERE visit.patient_id = patient._id AND visit.start_datetime LIKE '" + currentDate + "T%'";
        Log.i(TAG, query);
        final Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    activePatientList.add(new ActivePatientModel(
                            cursor.getInt(cursor.getColumnIndexOrThrow("_id")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("patient_id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("start_datetime")),
                            cursor.getString(cursor.getColumnIndexOrThrow("end_datetime")),
                            cursor.getString(cursor.getColumnIndexOrThrow("openmrs_visit_uuid")),
                            cursor.getString(cursor.getColumnIndexOrThrow("openmrs_id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("first_name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("middle_name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("last_name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("date_of_birth")),
                            cursor.getString(cursor.getColumnIndexOrThrow("phone_number"))
                    ));
                } while (cursor.moveToNext());
            }
        }
        cursor.close();

        if (!activePatientList.isEmpty()) {
            for (ActivePatientModel activePatientModel : activePatientList)
                Log.i(TAG, activePatientModel.getFirst_name() + " " + activePatientModel.getLast_name());

            mActivePatientAdapter = new ActivePatientAdapter(activePatientList, ActivePatientActivity.this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActivePatientActivity.this);
            mActivePatientList.setLayoutManager(linearLayoutManager);
            mActivePatientList.addItemDecoration(new
                    DividerItemDecoration(this,
                    DividerItemDecoration.VERTICAL));
            mActivePatientList.setAdapter(mActivePatientAdapter);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_today_patient, menu);
        inflater.inflate(R.menu.today_filter, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.summary_endAllVisit:
                endAllVisit();

            case R.id.action_filter:
                //alert box.
                displaySingleSelectionDialog();    //function call

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void displaySingleSelectionDialog() {

       /* View checkBoxView = View.inflate(this, R.layout.checkbox_view, null);
        CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Save to shared preferences
            }
        });
        checkBox.setText("Text to the right of the check box.");*/

        final String[] creator_names = {"Creator 1", "Creator 2", "Creator 3"};
        boolean[] checkedItems = {false, false, false};
        // ngo_numbers = getResources().getStringArray(R.array.ngo_numbers);
        dialogBuilder = new AlertDialog.Builder(ActivePatientActivity.this);
        dialogBuilder.setTitle("Filter by Creator");

        dialogBuilder.setMultiChoiceItems(creator_names, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {

            }
        });

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //display filter query code on list menu
            }
        });

        dialogBuilder.setNegativeButton("Cancel", null);
        //dialogBuilder.setView(checkBoxView);
        //dialogBuilder.setIcon(R.drawable.ic_sort_white_24dp);
        //  dialogBuilder.setItems(creator_names, new DialogInterface.OnClickListener() {
        // @Override
        //  public void onClick(DialogInterface dialog, int which) {
        // the user clicked on colors[which]
               /* final String a = "tel:"+"9769025715";
                final String b = "tel:"+"7304154312";

                if("Team 1".equals(ngo_numbers[which]))
                {
                    Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse(a));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(i);
                }

                else if("Team 2".equals(ngo_numbers[which]))
                {
                    Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse(b));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(i);
                }*/
        // }
        // });
        dialogBuilder.show();

    }

    private void endAllVisit() {

        int failedUploads = 0;

        String query = "SELECT visit.patient_id, visit.end_datetime, visit.openmrs_visit_uuid," +
                "patient.first_name, patient.middle_name, patient.last_name FROM visit, patient WHERE" +
                " visit.patient_id = patient._id AND visit.end_datetime IS NULL OR visit.end_datetime = ''";

        final Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    boolean result = endVisit(
                            cursor.getString(cursor.getColumnIndexOrThrow("patient_id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("first_name")) + " " +
                                    cursor.getString(cursor.getColumnIndexOrThrow("last_name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("openmrs_visit_uuid"))
                    );
                    if(!result) failedUploads++;
                } while (cursor.moveToNext());
            }
        }
        cursor.close();

        if (failedUploads == 0) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
        else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Unable to end "+failedUploads +
                    " visits.Please upload visit before attempting to end the visit.");
            alertDialogBuilder.setNeutralButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

    }

    private boolean endVisit(String patientID, String patientName, String visitUUID) {

        if (visitUUID == null) {
            return false;
        } else {
            Intent serviceIntent = new Intent(this, ClientService.class);
            serviceIntent.putExtra("serviceCall", "endVisit");
            serviceIntent.putExtra("patientID", patientID);
            serviceIntent.putExtra("visitUUID", visitUUID);
            serviceIntent.putExtra("name", patientName);
            startService(serviceIntent);
            return true;
        }

    }
}

