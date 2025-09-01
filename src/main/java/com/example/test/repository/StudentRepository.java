package com.example.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.test.domain.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
    
}
