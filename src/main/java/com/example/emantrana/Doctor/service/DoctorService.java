package com.example.emantrana.Doctor.service;

import com.example.emantrana.Doctor.dto.*;
import com.example.emantrana.jwt.JwtService;
import com.example.emantrana.models.*;
import com.example.emantrana.repository.DoctorRepo;
import com.example.emantrana.repository.PatientPrescriptionRepo;
import com.example.emantrana.repository.PatientRepo;
import com.example.emantrana.repository.Patient_DoctorPatientRepo;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;


@Service
public class DoctorService {
    private DoctorRepo doctorRepository;
    private JwtService jwtService;

    private PatientPrescriptionRepo patientPrescriptionRepo;
    private Patient_DoctorPatientRepo patient_doctorPatientRepo;
    private PatientRepo patientRepo;

    private JavaMailSender mailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public DoctorService(DoctorRepo doctorRepository,PatientRepo patientRepo,PatientPrescriptionRepo patientPrescriptionRepo,
                         Patient_DoctorPatientRepo patient_doctorPatientRepo,JwtService jwtService, JavaMailSender mailSender, PasswordEncoder passwordEncoder) {
        this.patientRepo = patientRepo;
        this.doctorRepository = doctorRepository;
        this.jwtService = jwtService;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
        this.patientPrescriptionRepo = patientPrescriptionRepo;
        this.patient_doctorPatientRepo = patient_doctorPatientRepo;
    }


    public  Doctor findDoctorByemail(String doctoremail) {

        return doctorRepository.findByEmail(doctoremail);
    }
    public String forgetPassword(ForgotPasswordDTO request) {
        Doctor doctor = doctorRepository.findByEmail(request.getEmail());
        if(doctor == null) {
            throw new RuntimeException("Doctor not found");
        }
        String token = RandomString.make(30);
        try{
          doctor.setResetToken(token);
            doctorRepository.save(doctor);
            String resetPasswordLink = "http://localhost:3000/doctor_reset_password?token=" + token;
            String toEmail = request.getEmail();
            String subject = "Here's the link to reset your password";
            String body = "To reset your password, click the link below:\n" + resetPasswordLink;
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setFrom("prafullpandey62000@gmail.com");
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error while sending email");
        }
        return "mail sent successfully";
    }

    public String resetPassword(ForgetPasswordRequestDTO request) {
        Doctor doctr = doctorRepository.findByResetToken(request.getToken());
        if(doctr == null) {
            throw new RuntimeException("Doctor not found");
        }
        doctr.setPassword(request.getPassword());
    //    doctor.setResetPasswordToken(null);
        doctorRepository.save(doctr);
        return "password updated successfully";
    }

//    public DoctorloginResponseDTO verifyDoctor(DoctorLoginDTO request) {
//        Doctor doctor = doctorRepository.findByEmail(request.getEmail());
//        if(doctor == null) {
//            throw new RuntimeException("Doctor not found");
//        }
//        if(!passwordEncoder.matches(request.getPassword(), doctor.getPassword())) {
//            throw new RuntimeException("Invalid password");
//        }
//        String token = jwtService.createDoctorJwt(doctor.getEmail(), doctor.getRoles());
//        DoctorloginResponseDTO response = new DoctorloginResponseDTO();
//        response.setDoctortoken(token);
//        return response;
//    }
    public DoctorloginResponseDTO verifyDoctor(DoctorLoginDTO request) {

        Doctor doctor = doctorRepository.findByEmail(request.getEmail());


        if(doctor == null) {
            throw new RuntimeException("Doctor not found");
        }
        if(!passwordEncoder.matches(request.getPassword(), doctor.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        if(passwordEncoder.matches(request.getPassword(), doctor.getPassword())) {
            System.out.println("password matched");
        }
        String token = jwtService.createDoctorJwt(doctor.getEmail(), doctor.getRoles());
        DoctorloginResponseDTO response = new DoctorloginResponseDTO();
        response.setDoctortoken(token);
        Long id = doctor.getId();
        response.setDoctorId(id);
        response.setDoctorEmail(doctor.getEmail());

        return response;
    }

    public String uploadPrescription(UploadPrescriptionDTO request) {
        Prescription Isprescription = patientPrescriptionRepo.findByPatientId(request.getPatientId());

        if(Isprescription != null) {

            patientPrescriptionRepo.delete(Isprescription);
        }
        Date date = new Date();
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        Optional<Doctor> doctor = doctorRepository.findById(request.getDoctorId());

        Prescription prescription = new Prescription();
        Optional<Patient> patient = patientRepo.findById(request.getPatientId());
        prescription.setPatient(patient.get());
        prescription.setDoctor(doctor.get());
        prescription.setDescription(request.getDescription());
        prescription.setMedicine(request.getMedicine());
        prescription.setDay(dayOfWeek.toString());
        prescription.setPreseciptionDate(date);
        patientPrescriptionRepo.save(prescription);
        return "prescription uploaded successfully";
    }


    public String updateHistory(UpdateHistroyAndGeneralQueueDTO request) {
        //  System.out.println(request.getPatientId());
        //    System.out.println(request.getDoctorId());
        Doctor doctor = doctorRepository.findById(request.getDoctorId()).get();
        Patient patient = patientRepo.findById(request.getPatientId()).get();
        doctor.setPatientCount(doctor.getPatientCount()+1);
        doctorRepository.save(doctor);

        Doctor_Patient doctor_patient = new Doctor_Patient();
        Date date = new Date();
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        doctor_patient.setDoctor(doctor);
        doctor_patient.setPatient(patient);
        doctor_patient.setDay(dayOfWeek.toString());
        doctor_patient.setPreseciptionDate(date);
        patient_doctorPatientRepo.save(doctor_patient);
        return "history updated successfully";
    }

    public List<HistoryResponseDTO> getHistory(Doctor doctor) {
        List<HistoryResponseDTO> historyResponseDTOS = new ArrayList<>();
        List<Doctor_Patient> doctor_patients = patient_doctorPatientRepo.findByDoctor(doctor);
        for(Doctor_Patient doctor_patient : doctor_patients) {
            HistoryResponseDTO historyResponseDTO = new HistoryResponseDTO();
            Patient patient = patientRepo.findById(doctor_patient.getPatient().getId()).get();
            historyResponseDTO.setFname(patient.getFname());
            historyResponseDTO.setLname(patient.getLname());
            historyResponseDTO.setPreseciptionDate(doctor_patient.getPreseciptionDate());
            historyResponseDTO.setDay(doctor_patient.getDay());
            historyResponseDTOS.add(historyResponseDTO);
        }
        return historyResponseDTOS;
    }

}

