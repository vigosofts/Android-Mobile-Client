package edu.jhu.bme.cbid.healthassistantsclient.objects;

import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amal Afroz Alam on 28, April, 2016.
 * Contact me: contact@amal.io
 */
public class PhysicalExam extends Node {

    private ArrayList<String> selection;
    private List<Node> selectedNodes;
    private int totalExams;
    private List<String> pageTitles;

    public PhysicalExam(JSONObject jsonObject, ArrayList<String> selection) {
        super(jsonObject);
        this.selection = selection;
        this.selectedNodes = matchSelections();
        this.totalExams = calculateTotal();
        this.pageTitles = determineTitles();
    }


    private List<Node> matchSelections() {
        List<Node> newOptionsList = new ArrayList<>();
        List<String> foundLocations = new ArrayList<>();
        //Add the general ones into here first
        newOptionsList.add(getOption(0));
        foundLocations.add(newOptionsList.get(0).text());

        if(!selection.isEmpty()){
            for (int i = 0; i < selection.size(); i++) {
                String current = selection.get(i);
                Log.d("Exam current: ", current);
                String[] split = current.split(":");
                String location = split[0];
                Log.d("Exam location: ", location);
                String exam = split[1];
                Log.d("Exam exam: ", exam);
                Node locationNodeRef = getOptionByName(location);
                Log.d("Exam locRef", locationNodeRef.text());
                Node examNodeRef = locationNodeRef.getOptionByName(exam);
                Log.d("Exam examRef", examNodeRef.text());
                if (foundLocations.contains(location)) {
                    Log.d("Exam if", "location in foundLocations");
                    int locationIndex = foundLocations.indexOf(location);
                    Node foundLocationNode = newOptionsList.get(locationIndex);
                    foundLocationNode.addOptions(new Node(examNodeRef));
                } else {
                    Log.d("Exam if", "not found");
                    foundLocations.add(location);
                    Node locationNode = new Node(locationNodeRef);
                    locationNode.removeOptionsList();
                    locationNode.addOptions(new Node(examNodeRef));
                    newOptionsList.add(locationNode);
                }

            }
        }
        return newOptionsList;
    }

    public List<Node> getSelectedNodes() {
        return selectedNodes;
    }

    private int calculateTotal() {
        int examTotal = 0;
        for (Node node : selectedNodes) {
            for (Node node1 : node.getOptionsList()) {
                examTotal++;
            }
        }

        return examTotal;
    }

    private List<String> determineTitles(){
        List<String> titles = new ArrayList<>();

        for (Node node : selectedNodes) {
            for (Node subNode : node.getOptionsList()) {
                titles.add(node.text() + " : " + subNode.text());
            }
        }

        return titles;
    }

    public int getTotalNumberofExams(){
        return totalExams;
    }

    public List<String> getAllTitles(){
        return pageTitles;
    }

    public String getTitle(int index){
        return pageTitles.get(index);
    }

    public Node getExamNode(int index){

        Node lvlTwoNode = null;

        String title = getTitle(index);
        String[] split = title.split(" : ");
        String levelOne = split[0];
        String levelTwo = split[1];

        for (Node selectedNode : selectedNodes) {
            if (selectedNode.text().equals(levelOne)){
                for (Node node : selectedNode.getOptionsList()) {
                    if(node.text().equals(levelTwo)){
                        lvlTwoNode = node;
                    }
                }
            }
        }

        return lvlTwoNode;

    }

    @Override
    public String formLanguage() {
        List<String> stringsList = new ArrayList<>();
        List<Node> mOptions = selectedNodes;
        for (int i = 0; i < mOptions.size(); i++) {
            if (mOptions.get(i).isSelected()) {
                stringsList.add(mOptions.get(i).language());
                if (!mOptions.get(i).isTerminal()) {
                    stringsList.add(mOptions.get(i).formLanguage());
                }
            }
        }

        String languageSeparator = ", ";
        String mLanguage = "";
        for (int i = 0; i < stringsList.size(); i++) {
            if (i == 0) {
                if (!stringsList.get(i).isEmpty()) {
                    mLanguage = mLanguage.concat(stringsList.get(i));
                }
            } else {
                if (!stringsList.get(i).isEmpty()) {
                    mLanguage = mLanguage.concat(languageSeparator + stringsList.get(i));
                }
            }
        }
        Log.d("Form language", mLanguage);
        return mLanguage;
    }

    @Override
    public String generateLanguage() {
        String raw = this.formLanguage();
        String formatted;
        if (Character.toString(raw.charAt(0)).equals(",")) {
            formatted = raw.substring(2);
        } else {
            formatted = raw;
        }
        return formatted;
    }

}