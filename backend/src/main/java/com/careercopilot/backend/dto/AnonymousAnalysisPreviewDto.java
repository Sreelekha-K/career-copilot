package com.careercopilot.backend.dto;

import java.util.List;

public class AnonymousAnalysisPreviewDto {

    private String professionalSummary;
    private List<String> improvedSkills;
    private List<String> improvedExperienceBullets;
    private List<String> atsKeywords;
    private List<String> quickSuggestions;

    public String getProfessionalSummary() {
        return professionalSummary;
    }

    public void setProfessionalSummary(String professionalSummary) {
        this.professionalSummary = professionalSummary;
    }

    public List<String> getImprovedSkills() {
        return improvedSkills;
    }

    public void setImprovedSkills(List<String> improvedSkills) {
        this.improvedSkills = improvedSkills;
    }

    public List<String> getImprovedExperienceBullets() {
        return improvedExperienceBullets;
    }

    public void setImprovedExperienceBullets(List<String> improvedExperienceBullets) {
        this.improvedExperienceBullets = improvedExperienceBullets;
    }

    public List<String> getAtsKeywords() {
        return atsKeywords;
    }

    public void setAtsKeywords(List<String> atsKeywords) {
        this.atsKeywords = atsKeywords;
    }

    public List<String> getQuickSuggestions() {
        return quickSuggestions;
    }

    public void setQuickSuggestions(List<String> quickSuggestions) {
        this.quickSuggestions = quickSuggestions;
    }
}
