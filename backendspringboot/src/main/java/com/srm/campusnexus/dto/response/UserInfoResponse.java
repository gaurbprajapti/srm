package com.srm.campusnexus.dto.response;

import com.srm.campusnexus.entity.User;

public class UserInfoResponse {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String portfolio;
    private String address;
    private String carrierObjective;
    private Boolean isAdmin;
    private String education;
    private String skills;
    private String experience;
    private String projects;

    // Constructors
    public UserInfoResponse() {
    }

    public UserInfoResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.mobileNumber = user.getMobileNumber();
        this.portfolio = user.getPortfolio();
        this.address = user.getAddress();
        this.carrierObjective = user.getCarrierObjective();
        this.isAdmin = user.getIsAdmin();
        this.education = user.getEducation();
        this.skills = user.getSkills();
        this.experience = user.getExperience();
        this.projects = user.getProjects();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCarrierObjective() {
        return carrierObjective;
    }

    public void setCarrierObjective(String carrierObjective) {
        this.carrierObjective = carrierObjective;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getProjects() {
        return projects;
    }

    public void setProjects(String projects) {
        this.projects = projects;
    }
}