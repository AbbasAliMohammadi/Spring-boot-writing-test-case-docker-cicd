package com.example.test.Student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.example.test.domain.Student;
import com.example.test.repository.StudentRepository;
import com.example.test.service.StudentService;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class studentTests {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private StudentRepository studentRepositoryMock;

    @Mock
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("786@admin");
    
    @DynamicPropertySource
    static void overrideDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void testDatabaseConnection() {
        System.out.println("JDBC URL: " + postgres.getJdbcUrl());
        System.out.println("Username: " + postgres.getUsername());
        System.out.println("Password: " + postgres.getPassword());
        // This test just prints connection details, you can remove or expand it.
    }

    @Test
    public void testSaveStudent() throws Exception {
        Student studentToSave = new Student(null, "ALI", "ali@gmail.com", "1234567890");

        // Mocking repository (optional) - but you shouldn't mock repository if you want
        // real DB test.
        // when(studentRepositoryMock.save(any(Student.class))).thenReturn(studentToSave);

        String json = objectMapper.writeValueAsString(studentToSave);

        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("ALI"))
                .andExpect(jsonPath("$.email").value("ali@gmail.com"))
                .andExpect(jsonPath("$.phone").value("1234567890"));
    }

    @Test
    public void testGetAllStudents() throws Exception {
        mockMvc.perform(get("/api/students")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("ALI"))
                .andExpect(jsonPath("$[0].email").value("ali@gmail.com"))
                .andExpect(jsonPath("$[0].phone").value("1234567890"));
    }

    @Test
    public void testFindStudentById() throws Exception {
        Student studentToSave = new Student(1L, "ALI", "ali@gmail.com", "1234567890");
        Long id = 1L;
        when(studentRepositoryMock.findById(id)).thenReturn(Optional.of(studentToSave));
        mockMvc.perform(get("/api/students/" + id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("ALI"))
                .andExpect(jsonPath("$.email").value("ali@gmail.com"))
                .andExpect(jsonPath("$.phone").value("1234567890"));
    }

    @Test
    public void testGetAllStudentsService() throws Exception {
        List<Student> mockStudents = List.of(
                new Student(1L, "ALI", "ali@gmail.com", "1234567890"),
                new Student(2L, "Ahmad", "ahmad@gmail.com", "0987654321"));
        when(studentService.findAll()).thenReturn(mockStudents);

        // Act
        List<Student> students = studentService.findAll();

        // Assert
        assertEquals(2, students.size());
        assertEquals("ALI", students.get(0).getName());
        assertEquals("Ahmad", students.get(1).getName());
    }
}
