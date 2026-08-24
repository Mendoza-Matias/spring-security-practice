package com.mmendoza.registerofusers.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EmployeeController {

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
    @GetMapping("/employee/profile/{username}")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name") // Seguridad contextual inteligente [12]
    public ResponseEntity<Map<String, String>> getMyProfile(@PathVariable String username) {
        return ResponseEntity.ok(Map.of(
                "usuario", username,
                "correo", username + "@empresa.com",
                "puesto", "Empleado de planta",
                "detalles", "Información confidencial de tu contrato laboral."
        ));
    }
}
