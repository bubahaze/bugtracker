package com.poludnikiewicz.bugtracker.bug;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="bug")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column
    private Long id;
    @Column
    private String summary;
    @Column
    private String project;
    @Column
    private String description;
    @Column
    @CreationTimestamp
    private LocalDateTime creationDate;
    @Column
    @JsonIgnore
    private String uniqueCode;
    @Column
    @JsonIgnore
    private BugStatus status; //TODO:  make it automatically REPORTED upon creating Bug by user -
    //TODO: see Registration controller & appusers
//    @Column
//    @JsonIgnore
//    private StaffMember assignedStaffMember; //should StaffMember extend User class ???

    //TODO: field enum priority?




}
