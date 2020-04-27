package com.springboot.restdemo.contoller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.restdemo.entity.Employee;
import com.springboot.restdemo.rest.EmployeeRestController;
import com.springboot.restdemo.service.EmployeeService;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
public class EmployeeRestControllerTest {

    private static final String API_EMPLOYEES = "/api/employees";
    private static final String APPLICATION_JSON = "application/json";

    @Value("${test.firstName}")
    private String firstName;

    @Value("${test.lastName}")
    private String lastName;

    @Value("${test.email}")
    private String email;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeRestController employeeRestController;

    private Employee testEmployee;
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(employeeRestController).build();

        testEmployee = new Employee(firstName, lastName, email);
    }

    @Test
    @DisplayName("Find All the employees")
    void testFindAll() throws Exception {
        List<Employee> employees = new ArrayList<Employee>();
        employees.add(testEmployee);
        Mockito.when(employeeService.findAll()).thenReturn(employees);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(API_EMPLOYEES))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].firstName").value(firstName)).andReturn();
    }

    @Test
    void testAddEmployee() throws Exception {
        testEmployee.setId(0);

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                assertTrue(args[0] instanceof Employee);
                assertNotNull(args[0]);
                Employee received = (Employee) args[0];
                assertThat(received.getEmail(), is(equalTo(testEmployee.getEmail())));
                return null;
            }
        }).when(employeeService).save(Mockito.any(Employee.class));

        String emString = new ObjectMapper().writeValueAsString(testEmployee);
        MvcResult mvcResult = this.mockMvc
                .perform(MockMvcRequestBuilders.post(API_EMPLOYEES).contentType(APPLICATION_JSON).content(emString))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(firstName)).andReturn();

        Mockito.verify(employeeService, Mockito.times(1)).save(Mockito.any(Employee.class));
    }
}
