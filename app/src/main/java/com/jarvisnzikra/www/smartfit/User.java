package com.jarvisnzikra.www.smartfit;

public class User {

    private int id;
    private String username,password,email,name,weight,height,gender,dob,mobile_no;

    public User(int id,String username,String password,String email,String name,String weight,String height,String gender,String dob,String mobile_no) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.weight = weight;
        this.height = height;
        this.dob = dob;
        this.mobile_no = mobile_no;
        this.gender = gender;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getWeight() {
        return weight;
    }

    public String getHeight() {
        return height;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public String getMobile_no() {
        return mobile_no;
    }
}