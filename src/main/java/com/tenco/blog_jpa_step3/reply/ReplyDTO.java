package com.tenco.blog_jpa_step3.reply;

import com.tenco.blog_jpa_step3.board.Board;
import com.tenco.blog_jpa_step3.user.User;
import lombok.Data;

public class ReplyDTO {

    @Data
    public static class SaveDTO {

        private Integer boardId;
        private String comment;

        public Reply toEntity(User sessionUser, Board board){
            return Reply.builder()
                    .comment(comment)
                    .board(board)
                    .user(sessionUser)
                    .build();
        }
    }
}
