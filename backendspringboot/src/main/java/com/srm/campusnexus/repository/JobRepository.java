package com.srm.campusnexus.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.srm.campusnexus.entity.Job;
import com.srm.campusnexus.entity.User;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

        List<Job> findByType(Job.JobType type);

        List<Job> findByCampus(Job.Campus campus);

        List<Job> findByCreatedBy(User createdBy);

        Page<Job> findByCreatedBy(User createdBy, Pageable pageable);

        Page<Job> findByType(Job.JobType type, Pageable pageable);

        Page<Job> findByCampus(Job.Campus campus, Pageable pageable);

        @Query("SELECT j FROM Job j WHERE j.title LIKE %:keyword% OR j.company LIKE %:keyword% OR j.description LIKE %:keyword% OR j.location LIKE %:keyword%")
        List<Job> searchJobs(@Param("keyword") String keyword);

        @Query("SELECT j FROM Job j WHERE j.title LIKE %:keyword% OR j.company LIKE %:keyword% OR j.description LIKE %:keyword% OR j.location LIKE %:keyword%")
        Page<Job> searchJobs(@Param("keyword") String keyword, Pageable pageable);

        @Query("SELECT j FROM Job j WHERE " +
                        "(:type IS NULL OR j.type = :type) AND " +
                        "(:campus IS NULL OR j.campus = :campus) AND " +
                        "(:keyword IS NULL OR j.title LIKE %:keyword% OR j.company LIKE %:keyword% OR j.description LIKE %:keyword%)")
        Page<Job> findJobsWithFilters(@Param("type") Job.JobType type,
                        @Param("campus") Job.Campus campus,
                        @Param("keyword") String keyword,
                        Pageable pageable);

        @Query("SELECT j FROM Job j ORDER BY j.createdAt DESC")
        Page<Job> findAllOrderByCreatedAtDesc(Pageable pageable);

        long countByCreatedBy(User user);

        @Query("SELECT j FROM Job j WHERE j.createdBy = :user AND " +
                        "(:type IS NULL OR j.type = :type) AND " +
                        "(:campus IS NULL OR j.campus = :campus) AND " +
                        "(:keyword IS NULL OR j.title LIKE %:keyword% OR j.company LIKE %:keyword% OR j.description LIKE %:keyword%)")
        Page<Job> findByCreatedByAndOptionalFilters(@Param("user") User user,
                        @Param("type") Job.JobType type,
                        @Param("campus") Job.Campus campus,
                        @Param("keyword") String keyword,
                        Pageable pageable);
}