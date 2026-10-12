
package com.studentmanagement.controller;

import com.studentmanagement.dto.StudentRequest;
import com.studentmanagement.dto.StudentUpdateRequest;
import com.studentmanagement.entity.Student;
import com.studentmanagement.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Student addStudent(
            @Valid @RequestBody StudentRequest request) {

        Student student = new Student();
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setCourse(request.getCourse());
        student.setDepartment(request.getDepartment());
        student.setAge(request.getAge());

        return studentService.addStudent(student);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<Student> getStudentById(
            @PathVariable Long id,
            Authentication authentication) {

        Student student =
                studentService.getStudentById(id, authentication);

        return ResponseEntity.ok(student);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Student> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentUpdateRequest request) {

        Student student = new Student();
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setCourse(request.getCourse());
        student.setDepartment(request.getDepartment());
        student.setAge(request.getAge());

        Student updatedStudent =
                studentService.updateStudent(id, student);

        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteStudent(
            @PathVariable Long id) {

        studentService.deleteStudent(id);

        return ResponseEntity.ok("Student deleted successfully");
    }


    @GetMapping("/my-profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Student> getMyProfile(
            Authentication authentication) {

        Student student = studentService.getMyProfile(
                authentication.getName()
        );

        return ResponseEntity.ok(student);
    }

}
