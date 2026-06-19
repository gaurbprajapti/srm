package com.srm.campusnexus.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.srm.campusnexus.entity.Club;
import com.srm.campusnexus.entity.Job;
import com.srm.campusnexus.entity.User;
import com.srm.campusnexus.repository.ClubRepository;
import com.srm.campusnexus.repository.JobRepository;
import com.srm.campusnexus.repository.UserRepository;

@Aspect
@Component
public class ResourceOwnershipAspect {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Intercept job retrieval methods to check ownership
     */
    @Before("execution(* com.srm.campusnexus.service.JobService.getJobById(Long)) && args(jobId)")
    public void checkJobOwnership(JoinPoint joinPoint, Long jobId) {
        validateJobAccess(jobId);
    }

    /**
     * Intercept club retrieval methods to check ownership
     */
    @Before("execution(* com.srm.campusnexus.service.ClubService.getClubById(Long)) && args(clubId)")
    public void checkClubOwnership(JoinPoint joinPoint, Long clubId) {
        validateClubAccess(clubId);
    }

    private void validateJobAccess(Long jobId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Authentication required");
        }

        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Admin can access anything
        if (user.getIsAdmin())
            return;

        // Check ownership
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied: You can only view your own jobs");
        }
    }

    private void validateClubAccess(Long clubId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Authentication required");
        }

        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Admin can access anything
        if (user.getIsAdmin())
            return;

        // Check ownership
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found"));

        if (!club.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied: You can only view your own clubs");
        }
    }
}