package com.some.orderservice.services;

import com.some.commonlib.model.UserPrincipal;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public abstract class UserService {

    public abstract UUID getUserId();

    public abstract String getUSerEmail();
}
