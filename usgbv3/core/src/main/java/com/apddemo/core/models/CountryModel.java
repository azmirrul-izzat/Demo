package com.apddemo.core.models;

import java.util.List;

public class CountryModel {
    private String countryTitle;
    private String countryCode;
    private String damPath;
    private List<CountryLanguageModel> languageList;

    public String getCountryTitle() {
        return countryTitle;
    }

    public void setCountryTitle(String countryTitle) {
        this.countryTitle = countryTitle;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public List<CountryLanguageModel> getLanguageList() {
        return languageList;
    }

    public void setLanguageList(List<CountryLanguageModel> languageList) {
        this.languageList = languageList;
    }

    public String getDamPath() {
        return damPath;
    }

    public void setDamPath(String damPath) {
        this.damPath = damPath;
    }
}
