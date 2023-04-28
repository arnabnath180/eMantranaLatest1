package com.example.emantrana.Doctor.dto;

import lombok.Data;

import java.util.Date;
@Data
public class HistoryResponseDTO {
    private String Fname;
    private String Lname;
    private Date preseciptionDate;
    private String day;
}
