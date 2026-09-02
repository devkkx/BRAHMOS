package com.brahmosbhawan.repository;

import com.brahmosbhawan.entity.ApprovedStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApprovedStudentRepository extends JpaRepository<ApprovedStudent, Long> {

    Optional<ApprovedStudent> findByEmail(String email);

    Optional<ApprovedStudent> findByStudentId(String studentId);

    // Rule: Name is Case-Insensitive (Upper/Lower acceptable), Email is STRICT LOWERCASE
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM ApprovedStudent a WHERE LOWER(TRIM(a.name)) = LOWER(TRIM(:name)) AND LOWER(TRIM(a.email)) = LOWER(TRIM(:email))")
    boolean isPreApprovedByNameAndEmail(@Param("name") String name, @Param("email") String email);

    void deleteAllInBatch();
}
