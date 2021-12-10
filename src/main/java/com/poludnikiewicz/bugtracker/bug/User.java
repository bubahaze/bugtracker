//package com.poludnikiewicz.bugtracker.bug;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.Pattern;
//import javax.validation.constraints.Size;
//
//// NOT NEEDED AT ALL? USERS ARE BEING SAVED IN APPLICATION_USER TABLE AND THIS ONE IS EMPTY
//@Entity
//@Table(name="users")
//@AllArgsConstructor
//@NoArgsConstructor
//@Data
//public class User {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column
//    @JsonIgnore
//    private Long id;
//
//    @Column
//    private String username;
//
//    @Column
//    @Size(min = 8)
//    private String password;
//
//    @Column
//    @Pattern(regexp = ".+@.+\\..+", message="Please provide a valid email address")
//    @NotBlank
//    private String email;
//
//    @Column
//    @JsonIgnore
//    private String role;
//}
