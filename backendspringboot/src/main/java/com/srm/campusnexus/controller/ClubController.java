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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.srm.campusnexus.dto.response.ApiResponse;
import com.srm.campusnexus.entity.Club;
import com.srm.campusnexus.service.ClubService;

@RestController
@RequestMapping("/api/clubs")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ClubController {

    @Autowired
    private ClubService clubService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Club>> createClub(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("category") String category,
            @RequestParam("discription") String discription,
            @RequestParam(value = "observation", required = false) String observation,
            @RequestParam(value = "president", required = false) String president,
            @RequestParam(value = "vicePresident", required = false) String vicePresident,
            @RequestParam(value = "whatup", required = false) String whatup,
            @RequestParam(value = "instagram", required = false) String instagram,
            @RequestParam(value = "linkedin", required = false) String linkedin,
            @RequestParam(value = "discord", required = false) String discord,
            @RequestParam(value = "achievement", required = false) String achievement,
            @RequestParam(value = "memberName", required = false) String memberName,
            @RequestParam(value = "facultyName", required = false) String facultyName,
            @RequestParam(value = "announcment", required = false) String announcment,
            Authentication authentication) {

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Build club data
            Club clubData = new Club();
            clubData.setTitle(title);
            clubData.setCategory(category);
            clubData.setDiscription(discription);
            clubData.setObservation(observation);
            clubData.setPresident(president);
            clubData.setVicePresident(vicePresident);
            clubData.setWhatup(whatup);
            clubData.setInstagram(instagram);
            clubData.setLinkedin(linkedin);
            clubData.setDiscord(discord);
            clubData.setAchievement(achievement);
            clubData.setMemberName(memberName);
            clubData.setFacultyName(facultyName);
            clubData.setAnnouncment(announcment);

            Club createdClub = clubService.createClub(clubData, file, userDetails.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponse.success("Club created successfully", createdClub));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllClubs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {

        try {
            Map<String, Object> result = clubService.getAllClubs(page, limit, category, search);

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
    @PreAuthorize("@securityExpressions.isClubOwnerOrAdmin(#id)")
    public ResponseEntity<ApiResponse<Club>> getClubById(@PathVariable Long id) {
        try {
            Club club = clubService.getClubById(id);
            return ResponseEntity.ok(
                    ApiResponse.success("Club retrieved successfully", club));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Club>> updateClub(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "discription", required = false) String discription,
            @RequestParam(value = "observation", required = false) String observation,
            @RequestParam(value = "president", required = false) String president,
            @RequestParam(value = "vicePresident", required = false) String vicePresident,
            @RequestParam(value = "whatup", required = false) String whatup,
            @RequestParam(value = "instagram", required = false) String instagram,
            @RequestParam(value = "linkedin", required = false) String linkedin,
            @RequestParam(value = "discord", required = false) String discord,
            @RequestParam(value = "achievement", required = false) String achievement,
            @RequestParam(value = "memberName", required = false) String memberName,
            @RequestParam(value = "facultyName", required = false) String facultyName,
            @RequestParam(value = "announcment", required = false) String announcment,
            Authentication authentication) {

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Build club data
            Club clubData = new Club();
            clubData.setTitle(title);
            clubData.setCategory(category);
            clubData.setDiscription(discription);
            clubData.setObservation(observation);
            clubData.setPresident(president);
            clubData.setVicePresident(vicePresident);
            clubData.setWhatup(whatup);
            clubData.setInstagram(instagram);
            clubData.setLinkedin(linkedin);
            clubData.setDiscord(discord);
            clubData.setAchievement(achievement);
            clubData.setMemberName(memberName);
            clubData.setFacultyName(facultyName);
            clubData.setAnnouncment(announcment);

            Club updatedClub = clubService.updateClub(id, clubData, file, userDetails.getUsername());

            return ResponseEntity.ok(
                    ApiResponse.success("Club updated successfully", updatedClub));
        } catch (RuntimeException e) {
            HttpStatus status = "Club not found".equals(e.getMessage())
                    ? HttpStatus.NOT_FOUND
                    : "You can only update clubs you created".equals(e.getMessage())
                            ? HttpStatus.FORBIDDEN
                            : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(
                    ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteClub(@PathVariable Long id, Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            clubService.deleteClub(id, userDetails.getUsername());

            return ResponseEntity.ok(
                    ApiResponse.success("Club deleted successfully"));
        } catch (RuntimeException e) {
            HttpStatus status = "Club not found".equals(e.getMessage())
                    ? HttpStatus.NOT_FOUND
                    : "You can only delete clubs you created".equals(e.getMessage())
                            ? HttpStatus.FORBIDDEN
                            : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(
                    ApiResponse.error(e.getMessage()));
        }
    }
}