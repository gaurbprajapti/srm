package com.srm.campusnexus.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.srm.campusnexus.entity.Club;
import com.srm.campusnexus.entity.User;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {

    List<Club> findByCategory(String category);

    List<Club> findByCreatedBy(User createdBy);

    Page<Club> findByCategory(String category, Pageable pageable);

    @Query("SELECT c FROM Club c WHERE c.title LIKE %:keyword% OR c.discription LIKE %:keyword% OR c.category LIKE %:keyword%")
    List<Club> searchClubs(@Param("keyword") String keyword);

    @Query("SELECT c FROM Club c WHERE c.title LIKE %:keyword% OR c.discription LIKE %:keyword% OR c.category LIKE %:keyword%")
    Page<Club> searchClubs(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT DISTINCT c.category FROM Club c ORDER BY c.category")
    List<String> findAllCategories();

    @Query("SELECT c FROM Club c WHERE (:category IS NULL OR c.category = :category) AND (:search IS NULL OR c.title LIKE %:search% OR c.discription LIKE %:search%)")
    Page<Club> findClubsWithFilters(@Param("category") String category, @Param("search") String search,
            Pageable pageable);

    long countByCreatedBy(User user);

    // Method for finding clubs by creator ordered by creation date
    List<Club> findByCreatedByOrderByCreatedAtDesc(User createdBy);

    // Method for searching clubs by title or description
    Page<Club> findByTitleContainingIgnoreCaseOrDiscriptionContainingIgnoreCase(
            String title, String description, Pageable pageable);
}