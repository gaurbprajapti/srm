package com.srm.campusnexus.security;

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

@Component("securityExpressions")
public class SecurityExpressions {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Check if the current user is the owner of the job or is an admin
     */
    public boolean isJobOwnerOrAdmin(Long jobId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null)
            return false;

        // Admin can access anything
        if (user.getIsAdmin())
            return true;

        // Check if user owns the job
        Job job = jobRepository.findById(jobId).orElse(null);
        return job != null && job.getCreatedBy().getId().equals(user.getId());
    }

    /**
     * Check if the current user is the owner of the club or is an admin
     */
    public boolean isClubOwnerOrAdmin(Long clubId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null)
            return false;

        // Admin can access anything
        if (user.getIsAdmin())
            return true;

        // Check if user owns the club
        Club club = clubRepository.findById(clubId).orElse(null);
        return club != null && club.getCreatedBy().getId().equals(user.getId());
    }

    /**
     * Check if current user is admin
     */
    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username).orElse(null);
        return user != null && user.getIsAdmin();
    }

    /**
     * Get current authenticated user
     */
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username).orElse(null);
    }
}