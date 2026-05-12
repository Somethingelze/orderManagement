package com.some.orderservice.services.impl;

import com.some.commonlib.model.UserPrincipal;
import com.some.orderservice.services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public UUID getUserId() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return userPrincipal.id();
    }

    @Override
    public String getUSerEmail()    {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return userPrincipal.email();
    }
}
