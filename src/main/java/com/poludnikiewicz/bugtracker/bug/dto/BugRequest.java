package com.poludnikiewicz.bugtracker.bug.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
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

    @JsonAlias({"operatingSystem", "OS", "opSys", "opSystemWhereBugOccurred"})
    @NotBlank(message = "Please specify the Operating System on which the issue occurred")
    private String opSystemWhereBugOccurred;

}
