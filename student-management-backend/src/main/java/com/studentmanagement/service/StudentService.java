
package com.studentmanagement.service;

import com.studentmanagement.entity.Role;
import com.studentmanagement.entity.Student;
import com.studentmanagement.entity.User;
import com.studentmanagement.exception.StudentAlreadyExistsException;
import com.studentmanagement.exception.StudentNotFoundException;
import com.studentmanagement.repository.StudentRepository;
import com.studentmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private InvitationService invitationService;

    @Transactional
    public Student addStudent(Student student) {

        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new StudentAlreadyExistsException(
                    "Student already exists with email: "
                            + student.getEmail()
            );
        }

        if (userRepository.existsByEmail(student.getEmail())) {
            throw new IllegalArgumentException(
                    "A user account already exists with email: "
                            + student.getEmail()
            );
        }

        User user = new User();
        user.setName(student.getName());
        user.setEmail(student.getEmail());
        user.setRole(Role.STUDENT);

        // Unknown random password; the account remains disabled
        // until the invitation recipient sets their own password.
        user.setPassword(
                passwordEncoder.encode(UUID.randomUUID().toString())
        );
        user.setPasswordSetupComplete(false);

        User savedUser = userRepository.save(user);

        student.setUser(savedUser);

        Student savedStudent = studentRepository.save(student);

        // Create the expiring invitation and send the email.
        invitationService.createAndSendInvitation(savedUser);

        return savedStudent;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(
            Long id,
            Authentication authentication) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new StudentNotFoundException("Student not found"));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return student;
        }

        if (student.getUser() == null
                || !student.getUser().getEmail()
                .equals(authentication.getName())) {

            throw new org.springframework.security.access.AccessDeniedException(
                    "You can only access your own data"
            );
        }

        return student;
    }

    public Student updateStudent(Long id, Student updatedStudent) {

        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() ->
                        new StudentNotFoundException("Student not found"));

        existingStudent.setName(updatedStudent.getName());
        existingStudent.setEmail(updatedStudent.getEmail());
        existingStudent.setPhone(updatedStudent.getPhone());
        existingStudent.setCourse(updatedStudent.getCourse());
        existingStudent.setDepartment(updatedStudent.getDepartment());
        existingStudent.setAge(updatedStudent.getAge());

        return studentRepository.save(existingStudent);
    }

    public void deleteStudent(Long id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new StudentNotFoundException("Student not found"));

        studentRepository.delete(student);
    }

    public Student getMyProfile(String email) {

        return studentRepository.findByUserEmail(email)
                .orElseThrow(() ->
                        new StudentNotFoundException(
                                "Student profile not found"
                        )
                );
    }

}
