package com.example.emantrana.repository;

import com.example.emantrana.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepo extends JpaRepository<Admin,Long> {
    Admin findByEmail(String email);
}
