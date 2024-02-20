package com.technofacts.lnf.company.repository;

import com.technofacts.lnf.company.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

}
