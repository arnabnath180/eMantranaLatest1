package com.example.emantrana.repository;

import com.example.emantrana.models.Doctor;
import com.example.emantrana.models.Doctor_Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface Patient_DoctorPatientRepo extends JpaRepository<Doctor_Patient, Long> {
    List<Doctor_Patient> findByDoctor(Doctor doctor);
    Doctor_Patient findByDayAndPreseciptionDate(String day, Date preseciptionDate);
}
// // Patient : Doctor_Patient = 1 : N
