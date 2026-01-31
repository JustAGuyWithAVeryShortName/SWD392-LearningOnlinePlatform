package com.hsp302.shared_english_e_learning_path.services;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.CreateModuleRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.DeleteModulesRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.UpdateModuleRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.ModuleResponse;
import com.hsp302.shared_english_e_learning_path.domain.entities.Course;
import com.hsp302.shared_english_e_learning_path.domain.entities.Module;
import com.hsp302.shared_english_e_learning_path.domain.enums.CourseStatus;
import com.hsp302.shared_english_e_learning_path.mappers.ModuleMapper;
import com.hsp302.shared_english_e_learning_path.repositories.CourseRepository;
import com.hsp302.shared_english_e_learning_path.repositories.ModuleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final ModuleMapper moduleMapper;
    private final CourseRepository courseRepository;

    public ModuleResponse createModule(CreateModuleRequest request) {
        Module module = moduleMapper.toModel(request);
        module.setModuleID(UUID.randomUUID());
        module.setStatus(CourseStatus.AVAILABLE);
        UUID courseID = request.getCourseID();
        Course course = courseRepository.findById(courseID)
                .orElseThrow(() -> new EntityNotFoundException("Course does not exist with ID: " + courseID));
        module.setCourse(course);
        moduleRepository.save(module);
        return moduleMapper.toDto(module);
    }

    public List<ModuleResponse> getAllModulesForCourse(UUID courseID) {
        List<Module> modules = getAllModulesByCourseID(courseID, CourseStatus.AVAILABLE);
        return modules.stream()
                .map(module -> moduleMapper.toDto(module))
                .toList();
    }

    public List<Module> getAllModulesByCourseID(UUID courseID, CourseStatus status) {
        return moduleRepository.findByCourseCourseIDAndStatus(courseID, status);
    }

    public Module getModelEntity(UUID moduleID) {
        return moduleRepository.findById(moduleID)
                .orElseThrow(() -> new EntityNotFoundException("Module does not exist with ID:" + moduleID));
    }

    public ModuleResponse getModel(UUID moduleID) {
        Module module = getModelEntity(moduleID);
        return moduleMapper.toDto(module);
    }

    public ModuleResponse updateModule(UUID moduleID, UpdateModuleRequest request) {
        Module module = getModelEntity(moduleID);
        module.setModuleName(request.getModuleName());
        moduleRepository.save(module);
        return moduleMapper.toDto(module);
    }

    public List<ModuleResponse> updateModulesStatus(UUID courseID, DeleteModulesRequest request) {
        List<UUID> existingModuleIDs = getAllModulesByCourseID(courseID, CourseStatus.AVAILABLE).stream()
                .map(Module::getModuleID).toList();
        List<UUID> requestedModuleIDs = request.getModuleIds();
        List<Module> modules = new ArrayList<>();
        for (UUID id : requestedModuleIDs) {
            if (existingModuleIDs.contains(id)) {
                Module module = getModelEntity(id);
                module.setStatus(request.getStatus());
                moduleRepository.save(module);
                modules.add(module);
            }
        }
        return modules.stream().map(module -> moduleMapper.toDto(module)).toList();
    }
}
