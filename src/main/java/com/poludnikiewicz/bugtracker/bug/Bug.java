package com.poludnikiewicz.bugtracker.bug;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="bug")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @NotBlank(message = "Please provide the summary of issue")
    private String summary;

    @NotBlank(message = "Please specify the project")
    private String project;

    @NotBlank(message = "Please provide the description of issue")
    private String description;

    @CreationTimestamp
    private LocalDateTime creationDate;

    @UpdateTimestamp
    private LocalDateTime lastChangeAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String uniqueCode;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Enumerated(EnumType.STRING)
    private BugStatus status;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="application_user_id")
    private ApplicationUser assignedStaffMember;
   // @Column
   // @JsonProperty(access = JsonProperty.Access.READ_ONLY)
   // private List<Bug> duplicates;//List of bugs that have been marked a duplicate of the bug currently being viewed.
    @NotBlank(message = "Please specify the OS on which the issue occured")
    private String OperatingSystem; //This is the operating system against which the bug was reported.

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String usernameOfReporterOfBug; //person who filed this bug

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Enumerated(EnumType.STRING)
    private BugPriority priority;

    //TODO: comments and comments tag features


}
