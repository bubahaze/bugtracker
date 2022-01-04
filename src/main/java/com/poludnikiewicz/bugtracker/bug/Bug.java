package com.poludnikiewicz.bugtracker.bug;

import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="bug")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Bug {

    @Id
    @SequenceGenerator(name="bug_sequence", sequenceName = "bug_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bug_sequence")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="application_user_id")
    private ApplicationUser assignedStaffMember;

    private String opSystemWhereBugOccurred;

    private String usernameOfReporter;

    @Enumerated(EnumType.STRING)
    private BugPriority priority;

    @OneToMany
    @JoinColumn(name = "bug_id", updatable = false, insertable = false)
    private List<Comment> comments;

    //TODO: comments and comments tag features

}
