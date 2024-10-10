package com.tenco.blog_jpa_step3.board;

import com.tenco.blog_jpa_step3.commom.errors.Exception403;
import com.tenco.blog_jpa_step3.commom.errors.Exception404;
import com.tenco.blog_jpa_step3.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service // 서비스 계층으로 등록
public class BoardService {

    private final BoardJPARepository boardJPARepository;

    /**
     * 게시글 ID로 게시글을 조회합니다.
     *
     * @param boardId 조회할 게시글의 ID
     * @return 해당 ID의 게시글 객체
     * @throws Exception404 게시글을 찾을 수 없는 경우 발생
     */
    public Board getBoard(int boardId){
        // 게시글을 ID로 조회하고, 존재하지 않으면 예외를 던집니다.
        Board board = boardJPARepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다"));
        return board;
    }

    /**
     * 게시글의 상세 정보를 조회하고, 현재 사용자가 해당 게시글의 작성자인지 여부를 확인합니다.
     *
     * @param boardId 조회할 게시글의 ID
     * @param sessionUser 현재 세션에 로그인한 사용자
     * @return 해당 ID의 게시글 객체에 isOwner 필드를 설정한 후 반환
     * @throws Exception404 게시글을 찾을 수 없는 경우 발생
     */
    public Board getBoardDetails(int boardId, User sessionUser) {
        // 게시글을 ID와 조인하여 조회하고, 존재하지 않으면 예외를 던집니다.
        Board board = boardJPARepository.findByIdJoinUser(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다"));

        // 현재 사용자가 게시글의 작성자인지 확인하여 isOwner 필드를 설정합니다.
        boolean isOwner = false;
        if(sessionUser != null){
            if(sessionUser.getId().equals(board.getUser().getId())){
                isOwner = true;
            }
        }

        // 코드 추가
        board.setOwner(isOwner); // @Transient 필드 설정
        return board;
    }

    /**
     * 새로운 게시글을 작성하여 저장합니다.
     *
     * @param reqDTO 게시글 작성 요청 DTO
     * @param sessionUser 현재 세션에 로그인한 사용자
     */
    @Transactional // 트랜잭션 관리: 데이터베이스 연산이 성공적으로 완료되면 커밋, 실패하면 롤백
    public void createBoard(BoardDTO.SaveDTO reqDTO, User sessionUser){
        // 요청 DTO를 엔티티로 변환하여 저장합니다.
        boardJPARepository.save(reqDTO.toEntity(sessionUser));
    }

    /**
     * 기존 게시글을 수정합니다.
     *
     * @param boardId 수정할 게시글의 ID
     * @param sessionUserId 현재 세션에 로그인한 사용자의 ID
     * @param reqDTO 게시글 수정 요청 DTO
     * @throws Exception404 게시글을 찾을 수 없는 경우 발생
     * @throws Exception403 권한이 없는 사용자가 수정하려는 경우 발생
     */
    @Transactional // 트랜잭션 관리
    public void updateBoard(int boardId, int sessionUserId, BoardDTO.UpdateDTO reqDTO){
        // 1. 게시글을 ID로 조회하고, 존재하지 않으면 예외를 던집니다.
        Board board = boardJPARepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다"));

        // 2. 현재 사용자가 게시글의 작성자인지 확인합니다.
        if(sessionUserId != board.getUser().getId()){
            throw new Exception403("게시글을 수정할 권한이 없습니다");
        }

        // 3. 게시글의 제목과 내용을 수정합니다.
        board.setTitle(reqDTO.getTitle());
        board.setContent(reqDTO.getContent());
        // 더티 체킹을 통해 변경 사항이 자동으로 반영됩니다.
    }


    /**
     * 기존 게시글을 삭제합니다.
     *
     * @param boardId 삭제할 게시글의 ID
     * @param sessionUserId 현재 세션에 로그인한 사용자의 ID
     * @throws Exception404 게시글을 찾을 수 없는 경우 발생
     * @throws Exception403 권한이 없는 사용자가 삭제하려는 경우 발생
     */
    @Transactional // 트랜잭션 관리
    public void deleteBoard(int boardId, int sessionUserId) {
        // 1. 게시글을 ID로 조회하고, 존재하지 않으면 예외를 던집니다.
        Board board = boardJPARepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다"));

        // 2. 현재 사용자가 게시글의 작성자인지 확인합니다.
        if(sessionUserId != board.getUser().getId()){
            throw new Exception403("게시글을 삭제할 권한이 없습니다");
        }

        // 3. 게시글을 삭제합니다.
        boardJPARepository.deleteById(boardId);
    }


    /**
     * 모든 게시글을 조회하여 리스트로 반환합니다.
     * 게시글은 ID를 기준으로 내림차순 정렬됩니다.
     *
     * @return 모든 게시글의 리스트
     */
    public List<Board> getAllBoards() {
        // 게시글을 ID 기준으로 내림차순 정렬하여 모두 조회합니다.
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return boardJPARepository.findAll(sort);
    }

}
