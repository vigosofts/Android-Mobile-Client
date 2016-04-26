package edu.jhu.bme.cbid.healthassistantsclient;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.jhu.bme.cbid.healthassistantsclient.objects.Knowledge;
import edu.jhu.bme.cbid.healthassistantsclient.objects.Node;


public class QuestionNodeActivity extends AppCompatActivity {

    final String LOG_TAG = "Question Node Activity";

    Integer patientID = null;
    Knowledge mKnowledge;
    ExpandableListView questionListView;
    String mFileName = "generic.json";
    int complaintNumber = 0;
    HashMap<String, String> complaintDetails;
    ArrayList<String> complaints;
    List<Node> complaintsNodes;
    Node currentNode;
    NodeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Bundle bundle = getIntent().getExtras();
//        patientID = bundle.getInt("patientID");
//        complaints = bundle.getStringArrayList("complaints");

        complaints = new ArrayList<>();
        complaints.add("Difficulty in Breathing");

        mKnowledge = new Knowledge(HelperMethods.encodeJSON(this, mFileName));
        complaintsNodes = new ArrayList<>();
        for (int i = 0; i < complaints.size(); i++) {
            complaintsNodes.add(mKnowledge.getComplaint(complaints.get(i)));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_node);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        questionListView = (ExpandableListView) findViewById(R.id.complaint_question_expandable_list_view);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (complaintNumber < complaints.size() - 1) {
                    complaintNumber++;
                    setupQuestions(complaintNumber);
                }
            }
        });

        setupQuestions(complaintNumber);

        questionListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                currentNode.getOption(groupPosition).toggleSelected();
                currentNode.getOption(groupPosition).getOption(childPosition).toggleSelected();
                adapter.notifyDataSetChanged();

//                try {
//                    Thread.sleep(250);
//                    if (groupPosition < adapter.getGroupCount() - 1) {
//                        questionListView.collapseGroup(groupPosition);
//                        questionListView.expandGroup(groupPosition + 1);
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                return false;
            }
        });


    }

    private void setupQuestions(int complaintIndex) {
        currentNode = mKnowledge.getComplaint(complaints.get(complaintIndex));
        adapter = new NodeAdapter(this, currentNode, getTitle().toString());
        questionListView.setAdapter(adapter);
        questionListView.setChoiceMode(ExpandableListView.CHOICE_MODE_MULTIPLE);
        questionListView.expandGroup(0);
        ;
        setTitle(currentNode.text());
    }

}
