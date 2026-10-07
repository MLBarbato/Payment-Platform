package com.matheus.paymentplatform.customer.service;

import com.matheus.paymentplatform.customer.domain.Customer;
import com.matheus.paymentplatform.customer.dto.CustomerRequest;
import com.matheus.paymentplatform.customer.dto.CustomerResponse;
import com.matheus.paymentplatform.customer.dto.CustomerUpdateRequest;
import com.matheus.paymentplatform.customer.exception.CustomerNotFoundException;
import com.matheus.paymentplatform.customer.exception.ResourceAlreadyExistsException;
import com.matheus.paymentplatform.customer.mapper.CustomerMapper;
import com.matheus.paymentplatform.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    CustomerService service;

    @Mock
    CustomerRepository repository;
    @Mock
    CustomerMapper mapper;

    private CustomerRequest request;
    private Customer customer;
    private Customer customerSaved;
    private CustomerResponse response;

    @BeforeEach
     void setUp(){
        request = new CustomerRequest(
                "Matheus",
                "84837620051",
                "matheus@email.com"
        );

        customer = new Customer(
                "Matheus",
                "84837620051",
                "matheus@email.com"
        );

        customerSaved = new Customer(
                "Matheus",
                "84837620051",
                "matheus@email.com"
        );

        response = new CustomerResponse(
                1L,
                "Matheus",
                "84837620051",
                "matheus@email.com",
                customer.getStatus(),
                customer.getCreatedAt()
        );
    }

    @Test
    void shouldCreateCustomer(){

        when(repository.existsByCpf(request.cpf())).thenReturn(false);
        when(repository.existsByEmail(request.email())).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(customer);
        when(repository.save(customer)).thenReturn(customerSaved);
        when(mapper.toResponse(customerSaved)).thenReturn(response);

        CustomerResponse result = service.create(request);
        assertEquals(response,result);

        verify(repository).existsByCpf(request.cpf());
        verify(repository).existsByEmail(request.email());
        verify(mapper).toEntity(request);
        verify(repository).save(customer);
        verify(mapper).toResponse(customerSaved);
    }

    @Test
    void shouldThrowExceptionWhenCpfAlreadyExists(){
        when(repository.existsByCpf(request.cpf())).thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.create(request));
        verify(repository).existsByCpf(request.cpf());
        verify(repository,never()).existsByEmail(request.email());
        verify(repository,never()).save(any());
        verify(mapper,never()).toEntity(request);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists(){
        when(repository.existsByCpf(request.cpf())).thenReturn(false);
        when(repository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.create(request)
        );

        verify(repository).existsByCpf(request.cpf());
        verify(repository).existsByEmail(request.email());
        verify(mapper,never()).toEntity(request);
        verify(repository,never()).save(any());
    }

    @Test
    void shouldFindCustomerById(){
        when(repository.findById(1L)).thenReturn(Optional.of(customer));
        when(mapper.toResponse(customer)).thenReturn(response);

        CustomerResponse result = service.findById(1L);
        assertEquals(response, result);

        verify(repository).findById(1L);
        verify(mapper).toResponse(customer);
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound(){
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> service.findById(1L)
                );

        verify(repository).findById(1L);
    }

    @Test
    void shouldFindAllCustomers(){
        Customer customer2 = new Customer(
                "Test",
                "46943147005",
                "test@email.com"
        );

        CustomerResponse response2 = new CustomerResponse(
                2L,
                "Test",
                "46943147005",
                "test@email.com",
                customer2.getStatus(),
                customer2.getCreatedAt()
        );

        List<Customer> list = new ArrayList<>();
        list.add(customer);
        list.add(customer2);

        when(repository.findAll()).thenReturn(list);

        when(mapper.toResponse(customer)).thenReturn(response);
        when(mapper.toResponse(customer2)).thenReturn(response2);

        List<CustomerResponse> listExpected = List.of(response, response2);

        List<CustomerResponse> listResult = service.findAll();

        assertEquals(listExpected, listResult);

        verify(repository).findAll();
        verify(mapper).toResponse(customer);
        verify(mapper).toResponse(customer2);
    }

    @Test
    void shouldFindAllCustomersWhenListIsEmpty(){
        List<Customer> list = new ArrayList<>();

        when(repository.findAll()).thenReturn(list);
        List<CustomerResponse> result = service.findAll();

        assertTrue(result.isEmpty());
        verify(repository).findAll();
    }

    @Test
    void shouldUpdateCustomer(){
        CustomerUpdateRequest requestUpdate = new CustomerUpdateRequest(
                "Matheus Leonel",
                "matheus.leonel@email.com"
        );

        CustomerResponse responseUpdated = new CustomerResponse(
                1L,
                "Matheus Leonel",
                customer.getCpf(),
                "matheus.leonel@email.com",
                customer.getStatus(),
                customer.getCreatedAt()
        );

        when(repository.findById(1L)).thenReturn(Optional.of(customer));
        when(repository.existsByEmail(requestUpdate.email())).thenReturn(false);
        when(repository.save(customer)).thenReturn(customer);
        when(mapper.toResponse(customer)).thenReturn(responseUpdated);

        CustomerResponse customerResult = service.update(1L,requestUpdate);

        assertEquals(responseUpdated, customerResult);
        assertEquals(responseUpdated.name(),customer.getName());
        assertEquals(responseUpdated.email(),customer.getEmail());

        verify(repository).findById(1L);
        verify(repository).existsByEmail(requestUpdate.email());
        verify(repository).save(customer);
        verify(mapper).toResponse(customer);
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFoundOnUpdate(){
        CustomerUpdateRequest requestUpdate = new CustomerUpdateRequest(
                "Matheus Leonel",
                "matheus.leonel@email.com"
        );
        when(repository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(
                CustomerNotFoundException.class,
                () -> service.update(10L,requestUpdate)
        );

        verify(repository).findById(10L);
        verify(repository,never()).existsByEmail("matheus.leonel@email.com");
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExistsOnUpdate(){
        CustomerUpdateRequest requestUpdate = new CustomerUpdateRequest(
                "Matheus Leonel",
                "matheus.leoonel@email.com"
        );

        when(repository.findById(1L)).thenReturn(Optional.of(customer));
        when(repository.existsByEmail(requestUpdate.email())).thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.update(1L,requestUpdate)
        );

        verify(repository).findById(1L);
        verify(repository).existsByEmail(requestUpdate.email());
        verify(repository,never()).save(customer);
    }

    @Test
    void shouldUpdateCustomerWithSameEmail(){
        CustomerUpdateRequest requestUpdate = new CustomerUpdateRequest(
                "Matheus Leonel",
                "matheus@email.com"
        );
        CustomerResponse responseUpdated = new CustomerResponse(
                1L,
                "Matheus Leonel",
                "84837620051",
                "matheus@email.com",
                customer.getStatus(),
                customer.getCreatedAt()
        );

        when(repository.findById(1L)).thenReturn(Optional.of(customer));
        when(repository.save(customer)).thenReturn(customer);
        when(mapper.toResponse(customer)).thenReturn(responseUpdated);

        CustomerResponse result = service.update(1L,requestUpdate);
        assertEquals(responseUpdated,result);
        assertEquals(requestUpdate.name(), customer.getName());
        assertEquals(requestUpdate.email(), customer.getEmail());

        verify(repository,never()).existsByEmail(requestUpdate.email());
        verify(repository).findById(1L);
        verify(repository).save(customer);
        verify(mapper).toResponse(customer);
    }

}
