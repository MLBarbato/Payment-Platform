package com.matheus.paymentplatform.customer.controller;

import com.matheus.paymentplatform.customer.domain.CustomerStatus;
import com.matheus.paymentplatform.customer.dto.CustomerRequest;
import com.matheus.paymentplatform.customer.dto.CustomerResponse;
import com.matheus.paymentplatform.customer.exception.CustomerNotFoundException;
import com.matheus.paymentplatform.customer.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CustomerService service;

    @Test
    void shouldCreateCustomer() throws Exception{
        String requestJson = """
        {
            "name": "Matheus",
            "cpf": "84837620051",
            "email": "matheus@email.com"
        }
        """;

        CustomerResponse response = new CustomerResponse(
                1L,
                "Matheus",
                "84837620051",
                "matheus@email.com",
                CustomerStatus.ACTIVE,
                Instant.now()
        );

        when(service.create(any(CustomerRequest.class))).thenReturn(response);

        mockMvc.perform(
                post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Matheus"))
                .andExpect(jsonPath("$.cpf").value("84837620051"))
                .andExpect(jsonPath("$.email").value("matheus@email.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(service).create(any(CustomerRequest.class));
    }

    @Test
    void shouldFindCustumerById() throws Exception {
        CustomerResponse response = new CustomerResponse(
                1L,
                "Matheus",
                "84837620051",
                "matheus@email.com",
                CustomerStatus.ACTIVE,
                Instant.now()
        );

        when(service.findById(1L)).thenReturn(response);

        mockMvc.perform(
                get("/api/v1/customers/1")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Matheus"))
                .andExpect(jsonPath("$.cpf").value("84837620051"))
                .andExpect(jsonPath("$.email").value("matheus@email.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(service).findById(1L);
    }

    @Test
    void shouldReturnCustomerNotFound() throws Exception{
        when(service.findById(101L))
                .thenThrow(new CustomerNotFoundException(101L));

        mockMvc.perform(
                get("/api/v1/customers/101")
        )
                .andExpect(status().isNotFound());

        verify(service).findById(101L);
    }

    @Test
    void shouldFindAllCustomers() throws Exception {
        CustomerResponse response = new CustomerResponse(
                1L,
                "Matheus",
                "84837620051",
                "matheus@email.com",
                CustomerStatus.ACTIVE,
                Instant.now()
        );
        CustomerResponse response2 = new CustomerResponse(
                2L,
                "Customer2",
                "17246005080",
                "customer2@email.com",
                CustomerStatus.ACTIVE,
                Instant.now()
        );

        when(service.findAll()).thenReturn(List.of(response,response2));

        mockMvc.perform(
                        get("/api/v1/customers")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray()) // verifica se é mesmo uma lista
                .andExpect(jsonPath("$.length()").value(2)) // verifica se o tamanho é realmente 2
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Matheus"))
                .andExpect(jsonPath("$[0].cpf").value("84837620051"))
                .andExpect(jsonPath("$[0].email").value("matheus@email.com"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Customer2"))
                .andExpect(jsonPath("$[1].cpf").value("17246005080"))
                .andExpect(jsonPath("$[1].email").value("customer2@email.com"))
                .andExpect(jsonPath("$[1].status").value("ACTIVE"));

        verify(service).findAll();
    }

    @Test
    void shouldReturnEmptyCustomerList() throws Exception{

        when(service.findAll()).thenReturn(List.of());

        mockMvc.perform(
                        get("/api/v1/customers")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray()) // verifica se é mesmo uma lista
                .andExpect(jsonPath("$.length()").value(0));

        verify(service).findAll();
    }






    


}
