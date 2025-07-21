package data_generation.data_generation.DataGeneration.entity;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class User {
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postcode;
    private String phone;
    private String dob;
    private String email;
    private String password;
    private String role;
    private boolean enabled;
    private int failedLoginAttempts;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}

