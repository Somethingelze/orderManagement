package com.some.inventoryservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class OrderIdNotFoundException extends RuntimeException {

    public OrderIdNotFoundException(String message) {
        super(message);
    }

}
