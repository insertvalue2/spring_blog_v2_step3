package com.tenco.blog_jpa_step3.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.tenco.blog_jpa_step3.user.UserDTO.JoinDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final HttpSession session;

    /**
     * 회원 수정 폼을 표시하는 메서드
     * 요청 주소: **GET http://localhost:8080/user/update-form**
     * 
     * @param request HTTP 요청 객체
     * @return 회원 수정 페이지 뷰
     */
    @GetMapping("/user/update-form")
    public String updateForm(HttpServletRequest request) {
        log.info("회원 수정 페이지 이동");

        User sessionUser = (User) session.getAttribute("sessionUser");
        User user = userRepository.findById(sessionUser.getId());
        request.setAttribute("name", "회원정보 수정");
        request.setAttribute("user", user);
        return "user/update-form";
    }


    /**
     * 회원정보 수정 처리 메서드
     * 요청 주소: **POST http://localhost:8080/user/update**
     * 
     * @param updateDTO 수정된 데이터를 담은 DTO
     * @return 메인 페이지로 리다이렉트
     */
    @PostMapping("/user/update")
    public String update(@ModelAttribute(name = "updateDTO") UserDTO.UpdateDTO updateDTO) {
        // 세션에서 로그인한 사용자 정보 가져오기
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login-form"; // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        // 사용자 수정
        User updatedUser = userRepository.updateById(sessionUser.getId(), updateDTO.getPassword(),
                updateDTO.getEmail());

        // 세션 정보 동기화
        session.setAttribute("sessionUser", updatedUser);

        // 수정 완료 후 메인 페이지로 리다이렉트
        return "redirect:/";
    }

    /**
     * 회원가입 폼을 표시하는 메서드
     * 요청 주소: **GET http://localhost:8080/join-form**
     * 
     * @return 회원가입 폼 뷰
     */
    @GetMapping("/join-form")
    public String joinForm(Model model) {
        log.info("로그인 페이지 이동");
        model.addAttribute("name", "회원가입 페이지");
        return "user/join-form";
    }

    /**
     * 회원가입 처리 메서드
     * 요청 주소: **POST http://localhost:8080/join**
     * 
     * @param joinDTO 회원가입 데이터를 담은 DTO
     * @return 로그인 폼 페이지로 리다이렉트
     */
    @PostMapping("/join")
    public String join(@ModelAttribute(name = "joinDTO") UserDTO.JoinDTO joinDTO) {
        // DTO를 엔티티로 변환 후 저장
        userRepository.save(joinDTO.toEntity());
        return "redirect:/login-form";
    }

    /**
     * 자원에 요청은 GET 이지만 보안에 이유로 예외 !
     * 로그인 처리 메서드
     * 요청 주소: POST http://localhost:8080/login
     */
    @PostMapping("/login")
    public String login(UserDTO.LoginDTO dto) {
        try {
            User sessionUser = userRepository.findByUsernameAndPassword(dto.getUsername(), dto.getPassword());
            session.setAttribute("sessionUser", sessionUser);
            return "redirect:/";
        } catch (Exception e) {
            // 로그인 실패 시 로그인 폼으로 리다이렉트
            return "redirect:/login-form?error";
        }
    }

    /**
     * 로그아웃 처리 메서드
     * 요청 주소: GET http://localhost:8080/logout
     */
    @GetMapping("/logout")
    public String logout() {
        session.invalidate(); // 세션 무효화 (로그아웃)
        return "redirect:/"; // 메인 페이지로 리다이렉트
    }

    /**
     * 로그인 페이지로 이동
     * 주소설계: http://localhost:8080/login-form
     * 
     * 이 메서드는 사용자를 로그인 페이지로 이동시킵니다.
     * 반환되는 문자열은 뷰 리졸버(View Resolver)가 처리하여
     * Mustache 템플릿 엔진을 통해 뷰 파일을 렌더링합니다.
     * 
     * - Mustache 템플릿 파일 위치: src/main/resources/templates/user/login-form.mustache
     * 
     * @return 로그인 페이지 뷰
     */
    @GetMapping("/login-form")
    public String loginForm(Model model) {
        log.info("로그인 페이지 이동");
        model.addAttribute("name", "로그인 페이지");
        return "user/login-form"; // Mustache 템플릿 경로: user/login-form.mustache
    }



}
