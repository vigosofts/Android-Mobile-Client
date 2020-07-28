package app.intelehealth.client.models;

public class LanguagesModel
{


    String languageName;
    boolean isLanguageSelected;

    public LanguagesModel(String languageName,boolean isLanguageSelected) {
        this.languageName = languageName;
        this.isLanguageSelected = isLanguageSelected;
    }
    public String getLanguageName() {
        return languageName;
    }
    public boolean getLanguageSelected() {
        return isLanguageSelected;
    }
    public void setLanguageSelected(boolean languageSelected) {
        isLanguageSelected = languageSelected;
    }


}
