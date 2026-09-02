package com.mmendoza.registerofusers.controller;

import com.mmendoza.registerofusers.dto.user.UserProfileResponse;
import com.mmendoza.registerofusers.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // 1. Endpoint público
    @GetMapping("/public/info")
    public ResponseEntity<String> getPublicInfo() {
        return ResponseEntity.ok("Información pública de la empresa.");
    }

    // 2. Funcionalidad Admin: Ver todos los empleados
    @GetMapping("/admin/employees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, String>>> getAllEmployees() {
        List<Map<String, String>> employees = List.of(
                Map.of("id", "1", "nombre", "Juan Pérez", "puesto", "Desarrollador"),
                Map.of("id", "2", "nombre", "María López", "puesto", "Diseñadora")
        );
        return ResponseEntity.ok(employees);
    }

    // 3. Funcionalidad Empleado: Ver únicamente su perfil personal
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<UserProfileResponse> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(employeeService.getProfile(authentication));
    }
}
