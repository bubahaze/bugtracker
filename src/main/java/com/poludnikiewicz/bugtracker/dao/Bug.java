package com.poludnikiewicz.bugtracker.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="bug")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bug implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private LocalDateTime reportedAt;
    @Column
    private String uniqueCode;

}
