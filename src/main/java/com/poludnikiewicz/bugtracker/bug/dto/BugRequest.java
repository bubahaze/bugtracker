package com.poludnikiewicz.bugtracker.bug.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class BugRequest {

    @NotBlank(message = "Please provide the summary of issue")
    private String summary;

    @NotBlank(message = "Please specify the project")
    private String project;

    @NotBlank(message = "Please provide the description of issue")
    private String description;

    @NotBlank(message = "Please specify the OS on which the issue occured")
    private String opSystemWhereBugOccurred;

}
