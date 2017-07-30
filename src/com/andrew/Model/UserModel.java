package com.andrew.Model;

import org.springframework.beans.factory.annotation.Autowired;

public class UserModel {

    @Autowired
    public String UserName;

    @Autowired
    public String Passwd;

    @Autowired
    public int Userid;
}
