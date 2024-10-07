package com.tenco.blog_jpa_step3.board;

import java.util.List;
import java.util.Objects;

import com.tenco.blog_jpa_step3.commom.errors.Exception400;
import com.tenco.blog_jpa_step3.commom.errors.Exception403;
import com.tenco.blog_jpa_step3.commom.errors.Exception404;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.tenco.blog_jpa_step3.board.BoardDTO.UpdateDTO;
import com.tenco.blog_jpa_step3.user.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * BoardController는 블로그 게시글과 관련된 HTTP 요청을 처리하는 컨트롤러 클래스입니다.
 */
@Slf4j
@RequiredArgsConstructor
@Controller
public class BoardController {

    // 서비스 객체 없이 바로 사용
    private final BoardNativeRepository boardNativeRepository;
    // 사용하기
    private final BoardRepository boardRepository;
    private final HttpSession session;


    /**
     * 게시글 수정 처리 메서드
     * 요청 주소: **POST http://localhost:8080/board/{id}/update**
     * 
     * @param id        수정할 게시글의 ID
     * @param updateDTO 수정된 데이터를 담은 DTO
     * @return 게시글 상세보기 페이지로 리다이렉트
     */
    @PostMapping("/board/{id}/update")
    public String update(@PathVariable(name = "id") Integer id, @ModelAttribute(name = "updateDTO") UpdateDTO updateDTO) {
        // 세션에서 로그인한 사용자 정보 가져오기
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login-form"; // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        // 게시글 조회
        Board board = boardRepository.findById(id);
        if (board == null) {
            throw new Exception404("게시글이 존재하지 않습니다"); // 게시글이 없을 경우 404 에러 페이지로 리다이렉트
        }

        // 권한 검증 (게시글 작성자만 수정 가능)
        if(!Objects.equals(sessionUser.getId(), board.getUser().getId())){
            throw new Exception403("게시글을 수정할 권한이 없습니다");
        }


        // 게시글 수정 (JPA API 사용)
        boardRepository.updateByIdJPA(id, updateDTO.getTitle(), updateDTO.getContent(), updateDTO.getUsername());

        // 수정 완료 후 게시글 상세보기 페이지로 리다이렉트
        return "redirect:/board/" + id;
    }

    /**
     * 게시글 수정 폼을 표시하는 메서드
     * 요청 주소: **GET http://localhost:8080/board/{id}/update-form**
     * 글 수정하기 페이지 요청 메서드
     * 
     * @param id 수정할 게시글의 ID
     * @return 게시글 수정 페이지 뷰
     */
    @GetMapping("/board/{id}/update-form")
    public String updateForm(@PathVariable(name = "id") Integer id, HttpServletRequest request) {

        // 현재 서비스 레이어가 없음
        // 1. 게시글 조회
        Board board = boardRepository.findById(id);
        // 2. 조회한 게시글을 요청 속성에 추가
        request.setAttribute("board", board);
        // 3. 수정 폼 템플릿 반환

        return "board/update-form";
    }

    // /**
    // * 게시글 삭제를 처리하는 메서드
    // * 요청 주소: **POST http://localhost:8080/board/{id}/delete**
    // */
    // @PostMapping("/board/{id}/delete")
    // public String delete(@PathVariable(name = "id") Integer id) {
    // // 1. 게시글 삭제
    // boardNativeRepository.deleteById(id);
    // // 2. 메인 페이지로 리다이렉트
    // return "redirect:/";
    // }

    /**
     * 게시글 삭제 처리 메서드
     * 
     * @param id 삭제할 게시글의 ID
     * @return 메인 페이지로 리다이렉트
     */
    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable(name = "id") Integer id) {
        // 세션에서 로그인한 사용자 정보 가져오기
        User sessionUser = (User) session.getAttribute("sessionUser");

        // 세션 유효성 검증
        if (sessionUser == null) {
            return "redirect:/login-form"; // 로그인 페이지로 리다이렉트
        }

        // 권한 검증 (게시글 작성자만 삭제 가능)
        Board board = boardRepository.findById(id);
        if (board == null) {
            // 예외 처리 또는 에러 페이지로 리다이렉트
            
            throw new Exception400("게시글이 존재하지 않습니다");
        }
        if (!board.getUser().getId().equals(sessionUser.getId())) {
            // 권한이 없는 사용자일 경우
            throw new Exception403("게시글을 삭제할 권한이 없습니다");
        }

        // 게시글 삭제
        boardRepository.deleteById(id);

        // 메인 페이지로 리다이렉트
        return "redirect:/";
    }

    /**
     * 새로운 게시글을 저장하는 메서드
     * 요청 주소: **POST http://localhost:8080/board/save**
     * //
     */
    // @PostMapping("/board/save")
    // public String save(@RequestParam("title") String title,
    // @RequestParam("content") String content) {
    // log.warn("메서드 실행됨: 제목={}, 내용={}", title, content); // 파라미터가 올바르게 전달되는지 확인
    // boardNativeRepository.save(title, content); // 게시글 저장
    // return "redirect:/";
    // }

    /**
     * 게시글 작성 처리 메서드
     * 
     * @param dto 게시글 작성 요청 DTO
     * @return 메인 페이지로 리다이렉트
     */
    @PostMapping("/board/save")
    public String save(@ModelAttribute BoardDTO.SaveDTO dto) {
        // 세션에서 로그인한 사용자 정보 가져오기
        User sessionUser = (User) session.getAttribute("sessionUser");

        // 세션 유효성 검증
        if (sessionUser == null) {
            return "redirect:/login-form"; // 로그인 페이지로 리다이렉트
        }

        // SaveDTO를 엔티티로 변환하고 저장
        boardRepository.save(dto.toEntity(sessionUser));

        return "redirect:/"; // 메인 페이지로 리다이렉트
    }

    /**
     * 게시글 업데이트를 처리하는 메서드
     * 요청 주소: **POST http://localhost:8080/board/{id}/update**
     */
    // @PostMapping("/board/{id}/update")
    // public String update(@PathVariable(name = "id") Integer id, String title, String content) {
    //     // 1. 게시글 업데이트
    //     boardNativeRepository.updateById(id, title, content);
    //     // 2. 상세 페이지로 리다이렉트
    //     return "redirect:/board/" + id;
    // }

    /**
     * 메인 페이지를 표시하는 메서드
     * 요청 주소: **GET http://localhost:8080/**
     */
    @GetMapping("/")
    public String index(Model model) {
        // 1. 모든 게시글 조회
        List<Board> boardList = boardNativeRepository.findAll();
        // 2. 조회한 게시글 목록을 모델에 추가
        model.addAttribute("boardList", boardList);
        // 3. 메인 페이지 템플릿 반환
        return "index";
    }

    /**
     * 게시글 작성 폼을 표시하는 메서드
     * 요청 주소: GET http://localhost:8080/board/save-form
     */
    @GetMapping("/board/save-form")
    public String saveForm() {
        // 1. 게시글 작성 폼 템플릿 반환
        return "board/save-form";
    }


    @GetMapping("/board/{id}")
    public String detail(@PathVariable Integer id, HttpServletRequest request) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        Board board = boardRepository.findByIdJoinUser(id);

        // 로그인을 하고, 게시글의 주인이면 isOwner가 true가 된다.
        boolean isOwner = false;
        if(sessionUser != null){
            if(Objects.equals(sessionUser.getId(), board.getUser().getId())){
                isOwner = true;
            }
        }

        request.setAttribute("isOwner", isOwner);
        request.setAttribute("board", board);
        return "board/detail";
    }

}
