package com.matheus.paymentplatform.customer.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CpfValidatorTest {

    private final CpfValidator validator = new CpfValidator();

    @Test
    void shouldReturnTrueForValidCpf(){
        String cpf = "01127476041";
        boolean result = validator.isValid(cpf, null);

        assertTrue(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "94105810010",
            "123456789",
            "123456789012"
    })
    void shouldReturnFalseForInvalidCpf(String cpf) {
        boolean result = validator.isValid(cpf, null);

        assertFalse(result);
    }




}
