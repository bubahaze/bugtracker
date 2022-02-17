package com.poludnikiewicz.bugtracker.bug.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.poludnikiewicz.bugtracker.bug.BugPriority;
import com.poludnikiewicz.bugtracker.bug.BugStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BugShorterResponse {

    private long bugId;
    private String summary;
    @JsonFormat(pattern="yyyy/MM/dd HH:mm:ss")
    private LocalDateTime lastChangeAt;
    private BugStatus status;
    private BugPriority priority;
}
