package com.poludnikiewicz.bugtracker.bug;

import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    private Long id;

    private String summary;

    private String project;

    private String description;

    @CreationTimestamp
    private LocalDateTime creationDate;

    @UpdateTimestamp
    private LocalDateTime lastChangeAt;

    private String uniqueCode;

    @Enumerated(EnumType.STRING)
    private BugStatus status;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="application_user_id")
    private ApplicationUser assignedStaffMember;

   // private List<Bug> duplicates;
    // List of bugs that have been marked a duplicate of the bug currently being viewed.

    private String opSystemWhereBugOccured;

    private String usernameOfReporterOfBug;

    @Enumerated(EnumType.STRING)
    private BugPriority priority;

    //TODO: comments and comments tag features


    public Bug(String summary, String project, String description, String uniqueCode, BugStatus status,
               String opSystemWhereBugOccured, String usernameOfReporterOfBug, BugPriority priority) {
        this.summary = summary;
        this.project = project;
        this.description = description;
        this.uniqueCode = uniqueCode;
        this.status = status;
        this.opSystemWhereBugOccured = opSystemWhereBugOccured;
        this.usernameOfReporterOfBug = usernameOfReporterOfBug;
        this.priority = priority;
    }
}
