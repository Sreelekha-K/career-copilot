package com.careercopilot.backend.dto;

import java.util.List;

public class JobAnalysisResponseDto {

    public int atsScore;
    public int jobMatchScore;
    public List<String> matchedSkills;
    public List<String> missingSkills;
    public List<String> improvementSuggestions;
    public List<String> recommendedKeywords;
    public String refinedResume;
    public List<LearningRoadmapItemDto> learningRoadmap;
    public List<String> technicalQuestions;
    public List<String> behavioralQuestions;

    public int getAtsScore() {
        return atsScore;
    }

    public void setAtsScore(int atsScore) {
        this.atsScore = atsScore;
    }

    public int getJobMatchScore() {
        return jobMatchScore;
    }

    public void setJobMatchScore(int jobMatchScore) {
        this.jobMatchScore = jobMatchScore;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public List<String> getImprovementSuggestions() {
        return improvementSuggestions;
    }

    public void setImprovementSuggestions(List<String> improvementSuggestions) {
        this.improvementSuggestions = improvementSuggestions;
    }

    public List<String> getRecommendedKeywords() {
        return recommendedKeywords;
    }

    public void setRecommendedKeywords(List<String> recommendedKeywords) {
        this.recommendedKeywords = recommendedKeywords;
    }

    public String getRefinedResume() {
        return refinedResume;
    }

    public void setRefinedResume(String refinedResume) {
        this.refinedResume = refinedResume;
    }

    public List<LearningRoadmapItemDto> getLearningRoadmap() {
        return learningRoadmap;
    }

    public void setLearningRoadmap(List<LearningRoadmapItemDto> learningRoadmap) {
        this.learningRoadmap = learningRoadmap;
    }

    public List<String> getTechnicalQuestions() {
        return technicalQuestions;
    }

    public void setTechnicalQuestions(List<String> technicalQuestions) {
        this.technicalQuestions = technicalQuestions;
    }

    public List<String> getBehavioralQuestions() {
        return behavioralQuestions;
    }

    public void setBehavioralQuestions(List<String> behavioralQuestions) {
        this.behavioralQuestions = behavioralQuestions;
    }

    public static class LearningRoadmapItemDto {

        public String skill;
        public String whyImportant;
        public List<String> learningSteps;

        public String getSkill() {
            return skill;
        }

        public void setSkill(String skill) {
            this.skill = skill;
        }

        public String getWhyImportant() {
            return whyImportant;
        }

        public void setWhyImportant(String whyImportant) {
            this.whyImportant = whyImportant;
        }

        public List<String> getLearningSteps() {
            return learningSteps;
        }

        public void setLearningSteps(List<String> learningSteps) {
            this.learningSteps = learningSteps;
        }
    }
}
