package com.srm.campusnexus.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.srm.campusnexus.dto.response.ApiResponse;
import com.srm.campusnexus.entity.Job;
import com.srm.campusnexus.service.JobService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*", maxAge = 3600)
public class JobController {

    @Autowired
    private JobService jobService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Job>> createJob(@Valid @RequestBody Job job, Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Job createdJob = jobService.createJob(job, userDetails.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponse.success("Job created successfully", createdJob));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllJobs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String campus,
            @RequestParam(required = false) String search) {

        try {
            // Convert string parameters to enums
            Job.JobType jobType = null;
            if (type != null) {
                try {
                    jobType = Job.JobType.valueOf(type.toUpperCase().replace("-", "_"));
                } catch (IllegalArgumentException e) {
                    // Invalid job type, ignore
                }
            }

            Job.Campus campusType = null;
            if (campus != null) {
                try {
                    campusType = Job.Campus.valueOf(campus.toUpperCase().replace("-", "_"));
                } catch (IllegalArgumentException e) {
                    // Invalid campus type, ignore
                }
            }

            Map<String, Object> result = jobService.getAllJobs(page, limit, jobType, campusType, search);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.get("data"));
            response.put("pagination", result.get("pagination"));

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityExpressions.isJobOwnerOrAdmin(#id)")
    public ResponseEntity<ApiResponse<Job>> getJobById(@PathVariable Long id) {
        try {
            Job job = jobService.getJobById(id);
            return ResponseEntity.ok(
                    ApiResponse.success("Job retrieved successfully", job));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Job>> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody Job job,
            Authentication authentication) {

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Job updatedJob = jobService.updateJob(id, job, userDetails.getUsername());

            return ResponseEntity.ok(
                    ApiResponse.success("Job updated successfully", updatedJob));
        } catch (RuntimeException e) {
            HttpStatus status = "Job not found".equals(e.getMessage())
                    ? HttpStatus.NOT_FOUND
                    : "You can only update jobs you created".equals(e.getMessage())
                            ? HttpStatus.FORBIDDEN
                            : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(
                    ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable Long id, Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            jobService.deleteJob(id, userDetails.getUsername());

            return ResponseEntity.ok(
                    ApiResponse.success("Job deleted successfully"));
        } catch (RuntimeException e) {
            HttpStatus status = "Job not found".equals(e.getMessage())
                    ? HttpStatus.NOT_FOUND
                    : "You can only delete jobs you created".equals(e.getMessage())
                            ? HttpStatus.FORBIDDEN
                            : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(
                    ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my-jobs")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getMyJobs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Map<String, Object> result = jobService.getMyJobs(userDetails.getUsername(), page, limit);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.get("data"));
            response.put("pagination", result.get("pagination"));

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}