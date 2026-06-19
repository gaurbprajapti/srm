package com.srm.campusnexus.security;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.srm.campusnexus.entity.Club;
import com.srm.campusnexus.entity.Job;
import com.srm.campusnexus.entity.User;
import com.srm.campusnexus.repository.ClubRepository;
import com.srm.campusnexus.repository.JobRepository;
import com.srm.campusnexus.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ResourceOwnershipFilter extends OncePerRequestFilter {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UserRepository userRepository;

    // Patterns for protected endpoints
    private static final Pattern JOB_PATTERN = Pattern.compile("/api/jobs/(\\d+)");
    private static final Pattern CLUB_PATTERN = Pattern.compile("/api/clubs/(\\d+)");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // Only apply to GET requests for individual resources
        if ("GET".equals(method)) {
            if (!validateResourceAccess(requestURI, response)) {
                return; // Response already set
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean validateResourceAccess(String requestURI, HttpServletResponse response) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return true; // Let Spring Security handle this
        }

        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);

        if (currentUser == null) {
            return true;
        }

        // Check job access
        Matcher jobMatcher = JOB_PATTERN.matcher(requestURI);
        if (jobMatcher.matches()) {
            Long jobId = Long.parseLong(jobMatcher.group(1));
            if (!canAccessJob(currentUser, jobId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Access denied: You can only view your own jobs\"}");
                return false;
            }
        }

        // Check club access
        Matcher clubMatcher = CLUB_PATTERN.matcher(requestURI);
        if (clubMatcher.matches()) {
            Long clubId = Long.parseLong(clubMatcher.group(1));
            if (!canAccessClub(currentUser, clubId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Access denied: You can only view your own clubs\"}");
                return false;
            }
        }

        return true;
    }

    private boolean canAccessJob(User user, Long jobId) {
        if (user.getIsAdmin())
            return true;

        Job job = jobRepository.findById(jobId).orElse(null);
        return job != null && job.getCreatedBy().getId().equals(user.getId());
    }

    private boolean canAccessClub(User user, Long clubId) {
        if (user.getIsAdmin())
            return true;

        Club club = clubRepository.findById(clubId).orElse(null);
        return club != null && club.getCreatedBy().getId().equals(user.getId());
    }
}