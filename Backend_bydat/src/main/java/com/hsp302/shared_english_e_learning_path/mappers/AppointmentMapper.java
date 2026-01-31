package com.hsp302.shared_english_e_learning_path.mappers;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.CreateAppointmentRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.UpdateAppointmentRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.AppointmentResponse;
import com.hsp302.shared_english_e_learning_path.domain.entities.Appointment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    Appointment toEntity(CreateAppointmentRequest request);
    Appointment toEntity(UpdateAppointmentRequest request);
    Appointment toEntity(AppointmentResponse response);
    AppointmentResponse toDto(Appointment appointment);
}
