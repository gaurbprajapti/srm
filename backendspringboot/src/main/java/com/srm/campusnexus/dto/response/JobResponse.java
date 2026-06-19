package com.srm.campusnexus.dto.response;

import java.time.LocalDateTime;

import com.srm.campusnexus.entity.Job;

public class JobResponse {
    private Long id;
    private String title;
    private String company;
    private String location;
    private String salary;
    private String description;
    private LocalDateTime postedDate;
    private String requirements;
    private String eligibility;
    private String linkedin;
    private String companyWebsite;
    private String type;
    private String campus;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor from Job entity
    public JobResponse(Job job) {
        this.id = job.getId();
        this.title = job.getTitle();
        this.company = job.getCompany();
        this.location = job.getLocation();
        this.salary = job.getSalary();
        this.description = job.getDescription();
        this.postedDate = job.getPostedDate();
        this.requirements = job.getRequirements();
        this.eligibility = job.getEligibility();
        this.linkedin = job.getLinkedin();
        this.companyWebsite = job.getCompanyWebsite();
        this.type = job.getType() != null ? job.getType().getValue() : null;
        this.campus = job.getCampus() != null ? job.getCampus().getValue() : null;
        this.createdAt = job.getCreatedAt();
        this.updatedAt = job.getUpdatedAt();

        // Safely get username without triggering lazy loading
        try {
            if (job.getCreatedBy() != null) {
                this.createdByUsername = job.getCreatedBy().getUsername();
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(LocalDateTime postedDate) {
        this.postedDate = postedDate;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getEligibility() {
        return eligibility;
    }

    public void setEligibility(String eligibility) {
        this.eligibility = eligibility;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public void setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
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