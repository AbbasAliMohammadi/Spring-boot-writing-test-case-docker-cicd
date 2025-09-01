package com.example.test.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.test.domain.Student;
import com.example.test.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Student getOne(Long id) {
       return studentRepository.findById(id).orElse(null);
    }
    
}
