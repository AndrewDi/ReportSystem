package com.andrew.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class RuleCheckServices {

    @Autowired
    private JdbcTemplate jdbcTemplate;


}
