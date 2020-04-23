package com.springboot.restdemo.contoller;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeRestControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private EmployeeService employeeService;
    
    @InjectMocks
    private EmployeeRestController employeeRestController;
    
    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(employeeRestController)
                .build();
    }
    
    @Test
    @DisplayName("Find All the employess")
    void testFindAll() throws Exception {
        List<Employee> employees = new ArrayList<Employee>();
        employees.add(new Employee("Ned", "Flanders", "nflanders@flanders.com"));
        Mockito.when(employeeService.findAll()).thenReturn(employees);
        
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/employees"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].firstName").value("Ned"))
                .andReturn();
    }
    
    @Test
    void testAddEmployee() throws Exception {
        String newEmployeeString = "{" + 
                "\"id\": 7," + 
                "\"firstName\": \"Ned\"," + 
                "\"lastName\": \"Flanders\"," + 
                "\"email\": \"nflaners@flaners.com\"" + 
                "}";
        Employee e =new Employee("Ned", "Flanders", "nflanders@flanders.com");
        e.setId(0);
        String emString = new ObjectMapper().writeValueAsString(e);
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/employees").contentType("application/json;").content(emString ))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Ned"))
                .andReturn();
        
        Mockito.verify(employeeService,Mockito.times(1)).save(e);
    }
}
