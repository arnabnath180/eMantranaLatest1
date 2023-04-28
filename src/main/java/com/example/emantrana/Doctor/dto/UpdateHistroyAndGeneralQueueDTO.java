package com.example.emantrana.Doctor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateHistroyAndGeneralQueueDTO {
    Long patientId;
    Long doctorId;
    public UpdateHistroyAndGeneralQueueDTO(
            @JsonProperty("patientId")Long patientId,
            @JsonProperty("doctorId")Long doctorId
    ) {

        this.patientId = patientId;
        this.doctorId =  doctorId;
    }
}
