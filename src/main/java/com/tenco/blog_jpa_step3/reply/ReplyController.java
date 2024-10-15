package com.tenco.blog_jpa_step3.reply;

import com.tenco.blog_jpa_step3.user.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@RequiredArgsConstructor
@Controller
public class ReplyController {
    private final ReplyService replyService;
    private final HttpSession session;

    /**
     * 댓글 생성
     *
     * @param reqDTO 댓글 작성 정보
     * @return 리다이렉트 URL
     */
    @PostMapping("/reply/save")
    // @SessionAttribute 어노테이션을 사용하여 세션에 저장된 객체를 매개변수로 바로 받아올 수 있습니다.
    public String save(ReplyDTO.SaveDTO reqDTO, @SessionAttribute("sessionUser") User sessionUser) {
        // 인터셉트 수정후 제거
        // User sessionUser = (User) session.getAttribute("sessionUser");
        replyService.saveReply(reqDTO, sessionUser);
        return "redirect:/board/" + reqDTO.getBoardId();
    }

    /**
     * 댓글 삭제
     *
     * @param boardId 게시글 ID
     * @param replyId 댓글 ID
     * @return 리다이렉트 URL
     */
    @PostMapping("/board/{boardId}/reply/{replyId}/delete")
    public String delete(@PathVariable Integer boardId, @PathVariable Integer replyId, @SessionAttribute("sessionUser") User sessionUser) {
        // User sessionUser = (User) session.getAttribute("sessionUser");
        // if (sessionUser == null) {
        //   return "redirect:/loginForm";
        // }
        replyService.deleteReply(replyId, sessionUser.getId(), boardId);
        return "redirect:/board/" + boardId;
    }

}
