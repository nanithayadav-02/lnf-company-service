package com.technofacts.lnf.company.repository;

import com.technofacts.lnf.company.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {

    @Query("SELECT i FROM Image i WHERE i.company.id = :id")
    Image findByCompanyId(@Param("id") UUID id);

}
