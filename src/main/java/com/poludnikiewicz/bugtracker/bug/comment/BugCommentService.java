package com.poludnikiewicz.bugtracker.bug.comment;

import com.poludnikiewicz.bugtracker.bug.Bug;
import com.poludnikiewicz.bugtracker.bug.BugRepository;
import com.poludnikiewicz.bugtracker.bug.comment.dto.BugCommentRequest;
import com.poludnikiewicz.bugtracker.bug.comment.dto.BugCommentResponse;
import com.poludnikiewicz.bugtracker.exception.BugNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class BugCommentService {

    private final BugCommentRepository commentRepository;
    private final BugRepository bugRepository;


    public void addComment(BugCommentRequest request, String author) {
        Long id = request.getBugId();
        Bug bug = bugRepository.findById(id)
                .orElseThrow(() -> new BugNotFoundException("Bug with id " + id + " not found."));

        BugComment comment = BugComment.builder()
                .content(request.getContent())
                .author(author)
                .bug(bug)
                .build();

        //TODO: bug.addComment(comment) and addComment/removeComment in Bug entity ?

        commentRepository.save(comment);
    }

//    private BugCommentResponse mapToBugCommentResponse(BugComment comment) {
//        BugCommentResponse commentResponse = new BugCommentResponse();
//        commentResponse.setBugId(comment.getBug().getId());
//        commentResponse.setAuthor(comment.getAuthor());
//        commentResponse.setContent(comment.getContent());
//        return commentResponse;
//    }
}
