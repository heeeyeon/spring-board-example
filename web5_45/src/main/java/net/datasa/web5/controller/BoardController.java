package net.datasa.web5.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.web5.domain.dto.BoardDTO;
import net.datasa.web5.domain.dto.MemberDTO;
import net.datasa.web5.domain.dto.ReplyDTO;
import net.datasa.web5.security.AuthenticatedUser;
import net.datasa.web5.service.BoardService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 게시판 관련 콘트롤러
 */

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("board")
public class BoardController {

    private final BoardService boardService;

    //application.properties 파일의 게시판 관련 설정값
    @Value("${board.pageSize}")
    int pageSize;

    @Value("${board.linkSize}")
    int linkSize;

    @Value("${board.uploadPath}")
    String uploadPath;

    /**
     * 게시판 전체 글 목록. 검색 및 페이지 지정 없이 모두 조회
     * @param model
     * @return 글 목록 페이지
     */
    @GetMapping("listAll")
    public String listAll(Model model) {
        List<BoardDTO> boardList = boardService.getListAll();
        model.addAttribute("boardList", boardList);
        return "boardView/listAll";
    }

    /**
     * 게시판 목록을 조회하고 페이징 및 검색 기능을 제공
     *
     * @param model       모델 객체
     * @param page        현재 페이지 (default: 0)
     * @param searchType  검색 대상 (default: "")
     * @param searchWord  검색어 (default: "")
     * @return 글 목록  한 페이지
     */
    @GetMapping("list")
    public String list(Model model
        , @RequestParam(name = "page", defaultValue = "1") int page
        , @RequestParam(name = "searchType", defaultValue = "") String searchType
        , @RequestParam(name = "searchWord", defaultValue = "") String searchWord) {

        log.debug("설정 값 : pageSize={}, linkSize={}", pageSize, linkSize);
        log.debug("요청파라미터 : page={}, searchType={}, searchWord={}", page, searchType, searchWord);

        //글 목록 1페이지
        Page<BoardDTO> boardPage = boardService.getList(page, pageSize, searchType, searchWord);

        model.addAttribute("boardPage", boardPage);
        model.addAttribute("page", page);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchWord", searchWord);
        model.addAttribute("linkSize", linkSize);

        log.debug("전체 개수 :{}", boardPage.getTotalElements());
        log.debug("전체 페이지수 :{}", boardPage.getTotalPages());
        log.debug("현재 페이지 :{}", boardPage.getNumber());
        log.debug("페이지당 글수 :{}", boardPage.getSize());
        log.debug("이전페이지 존재 :{}", boardPage.hasPrevious());
        log.debug("다음페이지 존재 :{}", boardPage.hasNext());

        return "boardView/list";
    }

    /**
     * 글쓰기 폼으로 이동
     * @return 글쓰기폼 HTML 파일 경로
     */
    @GetMapping("write")
    public String write() {
        return "boardView/writeForm";
    }

    /**
     * 글 저장
     * @param boardDTO 작성한 글 정보 (제목, 내용)
     * @param user 로그인한 사용자 정보
     * @return 게시판 글목록 경로
     */
    @PostMapping("write")
    public String write(
            @ModelAttribute BoardDTO boardDTO
            , @AuthenticationPrincipal AuthenticatedUser user) {

        //작성한 글에 사용자 아이디 추가
        boardDTO.setMemberId(user.getUsername());
        log.debug("저장할 글 정보 : {}", boardDTO);

        boardService.write(boardDTO);
        return "redirect:list";
    }

    /**
     * 게시글 상세보기
     * @param model     모델
     * @param boardNum  조회할 글 번호
     * @return 게시글 상세보기 HTML 경로
     */
    @GetMapping("read")
    public String read(Model model, @RequestParam("boardNum") int boardNum) {
        log.debug("조회할 글번호 : {}", boardNum);

        try {
            BoardDTO boardDTO = boardService.getBoard(boardNum);

            model.addAttribute("board", boardDTO);
            return "boardView/read";
        }
        catch (Exception e) {
            return "redirect:list";
        }
    }

    /**
     * 게시글 삭제
     * @param boardNum      삭제할 글번호
     * @param user          로그인한 사용자 정보
     * @return              글목록 경로
     */
    @GetMapping("delete")
    public String delete(
            @RequestParam("boardNum") int boardNum
            , @AuthenticationPrincipal AuthenticatedUser user) {

        try {
            boardService.delete(boardNum, user.getUsername());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:list";
    }

    /**
     * 게시글 수정 폼으로 이동
     * @param boardNum      수정할 글번호
     * @param user          로그인한 사용자 정보
     * @return              수정폼 HTML
     */
    @GetMapping("update")
    public String update(
            Model model
            , @RequestParam("boardNum") int boardNum
            , @AuthenticationPrincipal AuthenticatedUser user) {

        try {
            BoardDTO boardDTO = boardService.getBoard(boardNum);
            if (!user.getUsername().equals(boardDTO.getMemberId())) {
                throw new RuntimeException("수정 권한이 없습니다.");
            }
            model.addAttribute("board", boardDTO);
            return "boardView/updateForm";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "redirect:list";
        }
    }

    /**
     * 게시글 수정 처리
     * @param boardDTO      수정할 글 정보
     * @param user          로그인한 사용자 정보
     * @return              수정폼 HTML
     */
    @PostMapping("update")
    public String update(
            @ModelAttribute BoardDTO boardDTO
            , @AuthenticationPrincipal AuthenticatedUser user) {

        try {
            boardService.update(boardDTO, user.getUsername());
            return "redirect:read?boardNum=" + boardDTO.getBoardNum();

        }
        catch (Exception e) {
            e.printStackTrace();
            return "redirect:list";
        }
    }

    /**
     * 리플 쓰기
     * @param replyDTO      저장할 리플 정보
     * @param user          로그인 사용자 정보
     * @return              게시글 보기 경로로 이동
     */
    @PostMapping("replyWrite")
    public String replyWrite(@ModelAttribute ReplyDTO replyDTO
            , @AuthenticationPrincipal AuthenticatedUser user) {
        replyDTO.setMemberId(user.getUsername());
        boardService.replyWrite(replyDTO);
        return "redirect:read?boardNum=" + replyDTO.getBoardNum();
    }

    /**
     * 리플 삭제     *
     * @param replyDTO 삭제할 리플번호와 본문 글번호
     * @param user 로그인한 사용자 정보
     * @return 게시글 상세보기 경로
     */
    @GetMapping("replyDelete")
    public String replyDelete(
            @ModelAttribute ReplyDTO replyDTO
            , @AuthenticationPrincipal AuthenticatedUser user) {

        try {
            boardService.replyDelete(replyDTO.getReplyNum(), user.getUsername());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:read?boardNum=" + replyDTO.getBoardNum();
    }

}
