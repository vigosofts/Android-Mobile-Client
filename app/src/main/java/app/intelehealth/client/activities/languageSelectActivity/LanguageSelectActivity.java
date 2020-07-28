package app.intelehealth.client.activities.languageSelectActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;

import app.intelehealth.client.R;
import app.intelehealth.client.activities.IntroActivity.IntroActivity;
import app.intelehealth.client.activities.searchPatientActivity.SearchPatientActivity;
import app.intelehealth.client.activities.searchPatientActivity.SearchPatientAdapter;
import app.intelehealth.client.models.LanguagesModel;
import app.intelehealth.client.utilities.Logger;

public class LanguageSelectActivity extends AppCompatActivity {

    ArrayList<LanguagesModel> LANGUAGES = new ArrayList<>();
    RecyclerView language_rv;
    Button select_language_btn;
    private SelectLanguageAdapter selectLanguageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_select);
        initUiWidgets();
        setWidgetsListner();
        setupLanguages();
        setupLanguageRecyclerView();
    }

    private void setWidgetsListner() {

        select_language_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LanguageSelectActivity.this, IntroActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupLanguages() {
        LANGUAGES.add(new LanguagesModel("English",false));
        LANGUAGES.add(new LanguagesModel("Hindi",false));
        LANGUAGES.add(new LanguagesModel("Marathi",false));
        LANGUAGES.add(new LanguagesModel("Odiya",false));
        LANGUAGES.add(new LanguagesModel("Cebuano",false));
    }
    private void initUiWidgets() {
        //setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.title_activity_select_language));
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTheme);
        toolbar.setTitleTextColor(Color.WHITE);
        language_rv = findViewById(R.id.language_rv);
        select_language_btn = findViewById(R.id.select_language_btn);
    }
    private void setupLanguageRecyclerView()
    {
        try {
            selectLanguageAdapter = new SelectLanguageAdapter(LANGUAGES, LanguageSelectActivity.this,LanguageSelectActivity.this);
            RecyclerView.LayoutManager reLayoutManager = new LinearLayoutManager(getApplicationContext());
            language_rv.setLayoutManager(reLayoutManager);
            language_rv.setAdapter(selectLanguageAdapter);
            selectLanguageAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Logger.logE("doquery", "doquery", e);
        }
    }
    public void refreshLanguages(String language, boolean status)
    {
        for (int i = 0; i < LANGUAGES.size(); i++)
        {
            if(LANGUAGES.get(i).getLanguageName().equals(language))
                LANGUAGES.get(i).setLanguageSelected(status);
            else
                LANGUAGES.get(i).setLanguageSelected(false);
        }
        select_language_btn.setVisibility(View.VISIBLE);
        select_language_btn.setText("Select - "+language);
        setupLanguageRecyclerView();
    }
}