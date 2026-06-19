package com.srm.campusnexus.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.srm.campusnexus.dto.response.JobResponse;
import com.srm.campusnexus.entity.Job;
import com.srm.campusnexus.entity.User;
import com.srm.campusnexus.repository.JobRepository;
import com.srm.campusnexus.repository.UserRepository;

@Service
@Transactional
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    public Job createJob(Job jobData, String username) {
        // Validate required fields
        if (jobData.getTitle() == null || jobData.getCompany() == null) {
            throw new RuntimeException("Title and company are required");
        }

        // Get the user creating the job
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create job
        Job job = new Job();
        job.setTitle(jobData.getTitle());
        job.setCompany(jobData.getCompany());
        job.setLocation(jobData.getLocation());
        job.setSalary(jobData.getSalary());
        job.setDescription(jobData.getDescription());
        job.setRequirements(jobData.getRequirements() != null ? jobData.getRequirements() : "[]");
        job.setEligibility(jobData.getEligibility());
        job.setLinkedin(jobData.getLinkedin());
        job.setCompanyWebsite(jobData.getCompanyWebsite());
        job.setType(jobData.getType() != null ? jobData.getType() : Job.JobType.FULL_TIME);
        job.setCampus(jobData.getCampus() != null ? jobData.getCampus() : Job.Campus.OFF_CAMPUS);
        job.setPostedDate(LocalDateTime.now());
        job.setCreatedBy(user);

        return jobRepository.save(job);
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }

    public Map<String, Object> getAllJobs(int page, int limit, Job.JobType type, Job.Campus campus, String search) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());

        Page<Job> jobPage;
        if (type != null || campus != null || search != null) {
            jobPage = jobRepository.findJobsWithFilters(type, campus, search, pageable);
        } else {
            jobPage = jobRepository.findAll(pageable);
        }

        // Convert Job entities to JobResponse DTOs to avoid Hibernate serialization
        // issues
        List<JobResponse> jobResponses = jobPage.getContent().stream()
                .map(JobResponse::new)
                .collect(Collectors.toList());

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("current", page);
        pagination.put("pages", jobPage.getTotalPages());
        pagination.put("total", jobPage.getTotalElements());
        pagination.put("hasNext", jobPage.hasNext());
        pagination.put("hasPrev", jobPage.hasPrevious());

        Map<String, Object> result = new HashMap<>();
        result.put("data", jobResponses); // Use DTOs instead of entities
        result.put("pagination", pagination);

        return result;
    }

    /**
     * Get jobs filtered by current user (for secure multi-tenant access)
     */
    public Map<String, Object> getAllJobsForUser(String username, int page, int limit, Job.JobType type,
            Job.Campus campus, String search) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());

        Page<Job> jobPage;

        // Admin can see all jobs, regular users only see their own
        if (user.getIsAdmin()) {
            if (type != null || campus != null || search != null) {
                jobPage = jobRepository.findJobsWithFilters(type, campus, search, pageable);
            } else {
                jobPage = jobRepository.findAll(pageable);
            }
        } else {
            // Regular users only see their own jobs
            jobPage = jobRepository.findByCreatedByAndOptionalFilters(user, type, campus, search, pageable);
        }

        // Convert Job entities to JobResponse DTOs
        List<JobResponse> jobResponses = jobPage.getContent().stream()
                .map(JobResponse::new)
                .collect(Collectors.toList());

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("current", page);
        pagination.put("pages", jobPage.getTotalPages());
        pagination.put("total", jobPage.getTotalElements());
        pagination.put("hasNext", jobPage.hasNext());
        pagination.put("hasPrev", jobPage.hasPrevious());

        Map<String, Object> result = new HashMap<>();
        result.put("data", jobResponses);
        result.put("pagination", pagination);

        return result;
    }

    public Job updateJob(Long id, Job jobData, String username) {
        Job existingJob = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user owns the job or is admin
        if (!existingJob.getCreatedBy().getId().equals(user.getId()) && !user.getIsAdmin()) {
            throw new RuntimeException("You can only update jobs you created");
        }

        // Update job fields
        if (jobData.getTitle() != null)
            existingJob.setTitle(jobData.getTitle());
        if (jobData.getCompany() != null)
            existingJob.setCompany(jobData.getCompany());
        if (jobData.getLocation() != null)
            existingJob.setLocation(jobData.getLocation());
        if (jobData.getSalary() != null)
            existingJob.setSalary(jobData.getSalary());
        if (jobData.getDescription() != null)
            existingJob.setDescription(jobData.getDescription());
        if (jobData.getRequirements() != null)
            existingJob.setRequirements(jobData.getRequirements());
        if (jobData.getEligibility() != null)
            existingJob.setEligibility(jobData.getEligibility());
        if (jobData.getLinkedin() != null)
            existingJob.setLinkedin(jobData.getLinkedin());
        if (jobData.getCompanyWebsite() != null)
            existingJob.setCompanyWebsite(jobData.getCompanyWebsite());
        if (jobData.getType() != null)
            existingJob.setType(jobData.getType());
        if (jobData.getCampus() != null)
            existingJob.setCampus(jobData.getCampus());

        existingJob.setUpdatedBy(user);

        return jobRepository.save(existingJob);
    }

    public void deleteJob(Long id, String username) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user owns the job or is admin
        if (!job.getCreatedBy().getId().equals(user.getId()) && !user.getIsAdmin()) {
            throw new RuntimeException("You can only delete jobs you created");
        }

        jobRepository.deleteById(id);
    }

    public Map<String, Object> getMyJobs(String username, int page, int limit) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        Page<Job> jobPage = jobRepository.findByCreatedBy(user, pageable);

        // Convert to DTOs
        List<JobResponse> jobResponses = jobPage.getContent().stream()
                .map(JobResponse::new)
                .collect(Collectors.toList());

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("current", page);
        pagination.put("pages", jobPage.getTotalPages());
        pagination.put("total", jobPage.getTotalElements());
        pagination.put("hasNext", jobPage.hasNext());
        pagination.put("hasPrev", jobPage.hasPrevious());

        Map<String, Object> result = new HashMap<>();
        result.put("data", jobResponses);
        result.put("pagination", pagination);

        return result;
    }

    public Map<String, Object> searchJobs(String query, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        // Use the existing search method from repository
        Page<Job> jobPage = jobRepository.findJobsWithFilters(null, null, query, pageable);

        // Convert to DTOs
        List<JobResponse> jobResponses = jobPage.getContent().stream()
                .map(JobResponse::new)
                .collect(Collectors.toList());

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("current", page);
        pagination.put("pages", jobPage.getTotalPages());
        pagination.put("total", jobPage.getTotalElements());
        pagination.put("hasNext", jobPage.hasNext());
        pagination.put("hasPrev", jobPage.hasPrevious());

        Map<String, Object> result = new HashMap<>();
        result.put("data", jobResponses);
        result.put("pagination", pagination);

        return result;
    }
}