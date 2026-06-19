package com.srm.campusnexus.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.srm.campusnexus.dto.response.ClubResponse;
import com.srm.campusnexus.entity.Club;
import com.srm.campusnexus.entity.User;
import com.srm.campusnexus.repository.ClubRepository;
import com.srm.campusnexus.repository.UserRepository;

@Service
public class ClubService {

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    private String saveFile(MultipartFile file) {
        // Return null if no file is uploaded (making it optional)
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName != null && originalFileName.contains(".")
                    ? originalFileName.substring(originalFileName.lastIndexOf("."))
                    : "";
            String fileName = System.currentTimeMillis() + UUID.randomUUID().toString() + fileExtension;

            // Save file
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
    }

    public Club createClub(Club clubData, MultipartFile file, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Save file only if provided (optional)
        String fileName = saveFile(file);

        Club club = new Club();
        club.setTitle(clubData.getTitle());
        club.setDiscription(clubData.getDiscription());
        club.setCategory(clubData.getCategory());
        club.setObservation(clubData.getObservation());
        club.setPresident(clubData.getPresident());
        club.setVicePresident(clubData.getVicePresident());
        club.setWhatup(clubData.getWhatup());
        club.setInstagram(clubData.getInstagram());
        club.setLinkedin(clubData.getLinkedin());
        club.setDiscord(clubData.getDiscord());
        club.setAchievement(clubData.getAchievement() != null ? clubData.getAchievement() : "[]");
        club.setMemberName(clubData.getMemberName() != null ? clubData.getMemberName() : "[]");
        club.setFacultyName(clubData.getFacultyName() != null ? clubData.getFacultyName() : "[]");
        club.setAnnouncment(clubData.getAnnouncment() != null ? clubData.getAnnouncment() : "[]");
        club.setCover(fileName); // Will be null if no file uploaded
        club.setCreatedBy(user);

        return clubRepository.save(club);
    }

    public Club updateClub(Long id, Club clubData, MultipartFile file, String username) {
        Club existingClub = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user owns the club or is admin
        if (!existingClub.getCreatedBy().getId().equals(user.getId()) && !user.getIsAdmin()) {
            throw new RuntimeException("You can only update clubs you created");
        }

        // Handle optional file upload - keep existing cover if no new file provided
        String fileName = existingClub.getCover(); // Keep existing cover by default
        if (file != null && !file.isEmpty()) {
            fileName = saveFile(file); // saveFile now handles optional files gracefully
        }

        // Update fields
        if (clubData.getTitle() != null)
            existingClub.setTitle(clubData.getTitle());
        if (clubData.getDiscription() != null)
            existingClub.setDiscription(clubData.getDiscription());
        if (clubData.getCategory() != null)
            existingClub.setCategory(clubData.getCategory());
        if (clubData.getObservation() != null)
            existingClub.setObservation(clubData.getObservation());
        if (clubData.getPresident() != null)
            existingClub.setPresident(clubData.getPresident());
        if (clubData.getVicePresident() != null)
            existingClub.setVicePresident(clubData.getVicePresident());
        if (clubData.getWhatup() != null)
            existingClub.setWhatup(clubData.getWhatup());
        if (clubData.getInstagram() != null)
            existingClub.setInstagram(clubData.getInstagram());
        if (clubData.getLinkedin() != null)
            existingClub.setLinkedin(clubData.getLinkedin());
        if (clubData.getDiscord() != null)
            existingClub.setDiscord(clubData.getDiscord());
        if (clubData.getAchievement() != null)
            existingClub.setAchievement(clubData.getAchievement());
        if (clubData.getMemberName() != null)
            existingClub.setMemberName(clubData.getMemberName());
        if (clubData.getFacultyName() != null)
            existingClub.setFacultyName(clubData.getFacultyName());
        if (clubData.getAnnouncment() != null)
            existingClub.setAnnouncment(clubData.getAnnouncment());

        existingClub.setCover(fileName);
        existingClub.setUpdatedBy(user);

        return clubRepository.save(existingClub);
    }

    public Club getClubById(Long id) {
        return clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found"));
    }

    public Map<String, Object> getAllClubs(int page, int limit, String category, String search) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());

        Page<Club> clubPage;
        if (category != null || search != null) {
            clubPage = clubRepository.findClubsWithFilters(category, search, pageable);
        } else {
            clubPage = clubRepository.findAll(pageable);
        }

        // Convert Club entities to ClubResponse DTOs to avoid Hibernate serialization
        // issues
        List<ClubResponse> clubResponses = clubPage.getContent().stream()
                .map(ClubResponse::new)
                .collect(Collectors.toList());

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("current", page);
        pagination.put("pages", clubPage.getTotalPages());
        pagination.put("total", clubPage.getTotalElements());
        pagination.put("hasNext", clubPage.hasNext());
        pagination.put("hasPrev", clubPage.hasPrevious());

        Map<String, Object> result = new HashMap<>();
        result.put("data", clubResponses); // Use DTOs instead of entities
        result.put("pagination", pagination);

        return result;
    }

    public void deleteClub(Long id, String username) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user owns the club or is admin
        if (!club.getCreatedBy().getId().equals(user.getId()) && !user.getIsAdmin()) {
            throw new RuntimeException("You can only delete clubs you created");
        }

        clubRepository.delete(club);
    }

    public List<Club> getClubsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return clubRepository.findByCreatedByOrderByCreatedAtDesc(user);
    }

    public Map<String, Object> searchClubs(String query, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        // Use the existing searchClubs method from repository
        Page<Club> clubPage = clubRepository.searchClubs(query, pageable);

        // Convert to DTOs
        List<ClubResponse> clubResponses = clubPage.getContent().stream()
                .map(ClubResponse::new)
                .collect(Collectors.toList());

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("current", page);
        pagination.put("pages", clubPage.getTotalPages());
        pagination.put("total", clubPage.getTotalElements());
        pagination.put("hasNext", clubPage.hasNext());
        pagination.put("hasPrev", clubPage.hasPrevious());

        Map<String, Object> result = new HashMap<>();
        result.put("data", clubResponses);
        result.put("pagination", pagination);

        return result;
    }
}