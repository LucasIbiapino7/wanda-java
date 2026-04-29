package com.cosmo.wanda_web.controller;

import com.cosmo.wanda_web.dto.classroom.*;
import com.cosmo.wanda_web.entities.ClassroomStatus;
import com.cosmo.wanda_web.services.ClassroomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/classroom")
public class ClassroomController {

    @Autowired
    private ClassroomService classroomService;

    @PostMapping
    @PreAuthorize("@authz.isInstructorOrAdmin(authentication)")
    public ResponseEntity<ClassroomResponseDTO> create(@RequestBody @Valid ClassroomCreateDTO dto) {
        ClassroomResponseDTO response = classroomService.create(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@authz.isInstructorOrAdmin(authentication)")
    public ResponseEntity<ClassroomResponseDTO> update(@PathVariable Long id, @RequestBody @Valid ClassroomUpdateDTO dto) {
        ClassroomResponseDTO response = classroomService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/archive")
    @PreAuthorize("@authz.isInstructorOrAdmin(authentication)")
    public ResponseEntity<Void> archive(@PathVariable Long id) {
        classroomService.archive(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("@authz.isInstructorOrAdmin(authentication)")
    public ResponseEntity<Page<ClassroomResponseDTO>> findAllByInstructor(@RequestParam(required = false) ClassroomStatus status, Pageable pageable) {
        Page<ClassroomResponseDTO> response = classroomService.findAllByInstructor(status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student")
    public ResponseEntity<Page<ClassroomResponseDTO>> findAllByStudent(
            @RequestParam(required = false) ClassroomStatus status,
            Pageable pageable) {
        Page<ClassroomResponseDTO> response = classroomService.findAllByStudent(status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassroomResponseDTO> findById(@PathVariable Long id) {
        ClassroomResponseDTO response = classroomService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/access-code")
    @PreAuthorize("@authz.isInstructorOrAdmin(authentication)")
    public ResponseEntity<ClassroomResponseDTO> regenerateAccessCode(@PathVariable Long id) {
        ClassroomResponseDTO response = classroomService.regenerateAccessCode(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/students")
    @PreAuthorize("@authz.isInstructorOrAdmin(authentication)")
    public ResponseEntity<Void> addStudentByEmail(@PathVariable Long id, @RequestBody @Valid ClassroomAddStudentDTO dto) {
        classroomService.addStudentByEmail(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/students/{studentId}")
    @PreAuthorize("@authz.isInstructorOrAdmin(authentication)")
    public ResponseEntity<Void> removeStudent(@PathVariable Long id, @PathVariable Long studentId) {
        classroomService.removeStudent(id, studentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/join")
    public ResponseEntity<ClassroomResponseDTO> joinByCode(@RequestBody @Valid ClassroomJoinDTO dto) {
        ClassroomResponseDTO response = classroomService.joinByCode(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<Page<ClassroomMemberDTO>> findMembers(@PathVariable Long id, Pageable pageable) {
        Page<ClassroomMemberDTO> response = classroomService.findMembers(id, pageable);
        return ResponseEntity.ok(response);
    }
}