package com.srm.campusnexus.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.srm.campusnexus.dto.request.ChangePasswordRequest;
import com.srm.campusnexus.dto.request.LoginRequest;
import com.srm.campusnexus.dto.request.RegisterRequest;
import com.srm.campusnexus.dto.response.AuthResponse;
import com.srm.campusnexus.dto.response.UserInfoResponse;
import com.srm.campusnexus.entity.User;
import com.srm.campusnexus.repository.UserRepository;
import com.srm.campusnexus.security.JwtTokenProvider;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setMobileNumber(request.getMobileNumber());
        user.setIsAdmin(false); // Default to regular user

        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = jwtTokenProvider.generateJwtToken(savedUser.getUsername());

        return AuthResponse.success("User registered successfully", token, new UserInfoResponse(savedUser));
    }

    public AuthResponse login(LoginRequest request) {
        // Validate input
        if ((request.getEmail() == null && request.getUsername() == null) || request.getPassword() == null) {
            throw new RuntimeException("Email/username and password are required");
        }

        // Find user by email or username
        User user = null;
        if (request.getEmail() != null) {
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
            if (userOpt.isPresent()) {
                user = userOpt.get();
            }
        } else if (request.getUsername() != null) {
            Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
            if (userOpt.isPresent()) {
                user = userOpt.get();
            }
        }

        if (user == null) {
            throw new RuntimeException("Invalid credentials");
        }

        // Authenticate
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword()));

            // Generate JWT token
            String token = jwtTokenProvider.generateJwtToken(authentication);

            String welcomeMessage = String.format("Welcome back, %s!",
                    user.getFirstName() != null ? user.getFirstName() : user.getUsername());

            return AuthResponse.success(welcomeMessage, token, new UserInfoResponse(user));
        } catch (Exception e) {
            throw new RuntimeException("Invalid credentials");
        }
    }

    public UserInfoResponse getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserInfoResponse(user);
    }

    public UserInfoResponse updateProfile(String username, User updateData) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update only allowed fields
        if (updateData.getFirstName() != null)
            user.setFirstName(updateData.getFirstName());
        if (updateData.getLastName() != null)
            user.setLastName(updateData.getLastName());
        if (updateData.getEmail() != null) {
            // Check if email is already taken by another user
            Optional<User> existingUser = userRepository.findByEmail(updateData.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(updateData.getEmail());
        }
        if (updateData.getMobileNumber() != null)
            user.setMobileNumber(updateData.getMobileNumber());
        if (updateData.getPortfolio() != null)
            user.setPortfolio(updateData.getPortfolio());
        if (updateData.getAddress() != null)
            user.setAddress(updateData.getAddress());
        if (updateData.getCarrierObjective() != null)
            user.setCarrierObjective(updateData.getCarrierObjective());
        if (updateData.getEducation() != null)
            user.setEducation(updateData.getEducation());
        if (updateData.getSkills() != null)
            user.setSkills(updateData.getSkills());
        if (updateData.getExperience() != null)
            user.setExperience(updateData.getExperience());
        if (updateData.getProjects() != null)
            user.setProjects(updateData.getProjects());

        User updatedUser = userRepository.save(user);
        return new UserInfoResponse(updatedUser);
    }

    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public String refreshToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jwtTokenProvider.generateJwtToken(user.getUsername());
    }

    // Admin methods
    public List<UserInfoResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserInfoResponse::new)
                .collect(Collectors.toList());
    }

    public void deleteUser(Long id, String currentUsername) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Prevent admin from deleting themselves
        if (userToDelete.getId().equals(currentUser.getId())) {
            throw new RuntimeException("You cannot delete your own account");
        }

        userRepository.deleteById(id);
    }
}