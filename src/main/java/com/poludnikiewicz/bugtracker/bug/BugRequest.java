package com.poludnikiewicz.bugtracker.bug;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class BugRequest {

    @NotBlank(message = "Please provide the summary of issue")
    private String summary;

    @NotBlank(message = "Please specify the project")
    private String project;

    @NotBlank(message = "Please provide the description of issue")
    private String description;

    @NotBlank(message = "Please specify the OS on which the issue occured")
    private String opSystemWhereBugOccured;

}
