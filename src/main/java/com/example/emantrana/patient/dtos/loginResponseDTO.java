package com.example.emantrana.patient.dtos;

import lombok.Data;

@Data
public class loginResponseDTO {
    private String token;
    boolean status=true;
    Long patientId;
    private String Fname;
    private String Lname;
    private String dateOfBirth;
}
