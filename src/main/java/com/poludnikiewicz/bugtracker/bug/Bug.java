package com.poludnikiewicz.bugtracker.bug;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Table(name="bug")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
