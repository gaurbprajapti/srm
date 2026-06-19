package com.srm.campusnexus.dto.response;

import java.time.LocalDateTime;

import com.srm.campusnexus.entity.Club;

public class ClubResponse {
    private Long id;
    private String title;
    private String discription;
    private String observation;
    private String category;
    private String president;
    private String vicePresident;
    private String whatup;
    private String instagram;
    private String linkedin;
    private String discord;
    private String cover;
    private String achievement;
    private String memberName;
    private String facultyName;
    private String announcment;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor from Club entity
    public ClubResponse(Club club) {
        this.id = club.getId();
        this.title = club.getTitle();
        this.discription = club.getDiscription();
        this.observation = club.getObservation();
        this.category = club.getCategory();
        this.president = club.getPresident();
        this.vicePresident = club.getVicePresident();
        this.whatup = club.getWhatup();
        this.instagram = club.getInstagram();
        this.linkedin = club.getLinkedin();
        this.discord = club.getDiscord();
        this.cover = club.getCover();
        this.achievement = club.getAchievement();
        this.memberName = club.getMemberName();
        this.facultyName = club.getFacultyName();
        this.announcment = club.getAnnouncment();
        this.createdAt = club.getCreatedAt();
        this.updatedAt = club.getUpdatedAt();

        // Safely get username without triggering lazy loading
        try {
            if (club.getCreatedBy() != null) {
                this.createdByUsername = club.getCreatedBy().getUsername();
            }
        } catch (Exception e) {
            // If lazy loading fails, set to null
            this.createdByUsername = null;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPresident() {
        return president;
    }

    public void setPresident(String president) {
        this.president = president;
    }

    public String getVicePresident() {
        return vicePresident;
    }

    public void setVicePresident(String vicePresident) {
        this.vicePresident = vicePresident;
    }

    public String getWhatup() {
        return whatup;
    }

    public void setWhatup(String whatup) {
        this.whatup = whatup;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getDiscord() {
        return discord;
    }

    public void setDiscord(String discord) {
        this.discord = discord;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getAchievement() {
        return achievement;
    }

    public void setAchievement(String achievement) {
        this.achievement = achievement;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getAnnouncment() {
        return announcment;
    }

    public void setAnnouncment(String announcment) {
        this.announcment = announcment;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}