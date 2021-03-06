package net.woniper.board.test.service;

import net.woniper.board.builder.EntityBuilder;
import net.woniper.board.domain.Board;
import net.woniper.board.domain.KindBoard;
import net.woniper.board.errors.support.BoardNotFoundException;
import net.woniper.board.errors.support.UserNotFoundException;
import net.woniper.board.service.BoardService;
import net.woniper.board.service.KindBoardService;
import net.woniper.board.support.dto.BoardDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.junit.Assert.*;

/**
 * Created by woniper on 15. 2. 4..
 */
public class BoardServiceTest extends BaseServiceTest {

    @Autowired private BoardService boardService;
    @Autowired private KindBoardService kindBoardService;

    private Board adminBoard;
    private Board userBoard;

    private KindBoard kindBoard;

    @Before
    public void setUp() throws Exception {
        kindBoard = kindBoardService.save("General");
        Board newAdminBoard = EntityBuilder.createBoard(admin);
        newAdminBoard.setKindBoard(kindBoard);
        Board newUserBoard = EntityBuilder.createBoard(user);
        newUserBoard.setKindBoard(kindBoard);

        adminBoard = boardService.save(modelMapper.map(newAdminBoard, BoardDto.Request.class), admin.getUsername());
        userBoard = boardService.save(modelMapper.map(newUserBoard, BoardDto.Request.class), user.getUsername());
    }

    @Test
    public void test_getBoard() throws Exception {
        // given
        assertEquals(0, userBoard.getReadCount());

        // when
        Board board = boardService.find(userBoard.getBoardId());

        // then
        assertEquals(1, board.getReadCount());
        assertEquals(userBoard.getBoardId(), board.getBoardId());
        assertEquals(userBoard.getTitle(), board.getTitle());
    }

    @Test(expected = UserNotFoundException.class)
    public void test_createBoard_UserNotFound() throws Exception {
        // given
        BoardDto.Request boardDto = modelMapper.map(EntityBuilder.createBoard(user), BoardDto.Request.class);

        // when
        boardService.save(boardDto, "notUser");

        // then
        fail("UserNotFoundException");
    }

    @Test
    public void test_createBoard() throws Exception {
        // given
        Board newBoard = EntityBuilder.createBoard(user);
        newBoard.setKindBoard(kindBoard);
        BoardDto.Request newBoardDto = modelMapper.map(newBoard, BoardDto.Request.class);

        // when
        Board createBoard = boardService.save(newBoardDto, user.getUsername());

        // then
        assertEquals(newBoard.getTitle(), createBoard.getTitle());
        assertEquals(newBoard.getContent(), createBoard.getContent());
    }

    @Test(expected = BoardNotFoundException.class)
    public void test_updateBoard_admin_notFoundException() throws Exception {
        // given
        Long boardId = Long.MAX_VALUE;
        BoardDto.Request boardDto = modelMapper.map(adminBoard, BoardDto.Request.class);
        String username = admin.getUsername();

        // when
        boardService.update(boardId, boardDto, username, RequestMethod.PUT.toString());

        // then
        fail("Board Not Found Exception");
    }

    @Test(expected = BoardNotFoundException.class)
    public void test_updateBoard_user_notFoundException() throws Exception {
        // given
        Long boardId = Long.MAX_VALUE;
        BoardDto.Request boardDto = modelMapper.map(userBoard, BoardDto.Request.class);
        String username = user.getUsername();

        // when
        boardService.update(boardId, boardDto, username, RequestMethod.PUT.toString());

        // then
        fail("Board Not Found Exception");
    }

    @Test
    public void test_updateBoard_patch_null() throws Exception {
        // given
        Long boardId = userBoard.getBoardId();
        BoardDto.Request boardDto = modelMapper.map(userBoard, BoardDto.Request.class);
        boardDto.setTitle(null);
        boardDto.setContent("updateContent");
        String username = user.getUsername();

        // when
        Board board = boardService.update(boardId, boardDto, username, RequestMethod.PATCH.toString());

        // then
        assertNotNull(board.getTitle());
        assertEquals(userBoard.getContent(), board.getContent());
    }

    @Test
    public void test_updateBoard_patch_not_null() throws Exception {
        // given
        Long boardId = userBoard.getBoardId();
        BoardDto.Request boardDto = modelMapper.map(userBoard, BoardDto.Request.class);
        boardDto.setTitle("updateTitle");
        boardDto.setContent("updateContent");
        String username = user.getUsername();

        // when
        Board board = boardService.update(boardId, boardDto, username, RequestMethod.PATCH.toString());

        // then
        assertEquals(boardDto.getTitle(), board.getTitle());
        assertEquals(boardDto.getContent(), board.getContent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_updateBoard_update_null() throws Exception {
        // given
        Long boardId = userBoard.getBoardId();
        BoardDto.Request boardDto = modelMapper.map(userBoard, BoardDto.Request.class);
        boardDto.setTitle(null);
        boardDto.setContent("updateContent");
        String username = user.getUsername();

        // when
        boardService.update(boardId, boardDto, username, RequestMethod.PUT.toString());

        // then
        fail("IllegalArgumentException");
    }

    @Test
    public void test_updateBoard_update_not_null() throws Exception {
        // given
        Long boardId = userBoard.getBoardId();
        BoardDto.Request boardDto = modelMapper.map(userBoard, BoardDto.Request.class);
        boardDto.setTitle("updateTitle");
        boardDto.setContent("updateContent");
        String username = user.getUsername();

        // when
        Board board = boardService.update(boardId, boardDto, username, RequestMethod.PUT.toString());

        // then
        assertEquals(boardDto.getTitle(), board.getTitle());
        assertEquals(boardDto.getContent(), board.getContent());
    }

    @Test
    public void test_deleteBoard_admin이_다른_user_Board_삭제() throws Exception {
        // given
        Long userBoardId = userBoard.getBoardId();
        String adminUsername = admin.getUsername();

        // when
        boolean isDelete = boardService.delete(userBoardId, adminUsername);

        // then
        assertEquals(true, isDelete);
    }

    @Test
    public void test_deleteBoard_user가_다른_user_Board_삭제() throws Exception {
        // given
        Long adminBoardId = adminBoard.getBoardId();
        String username = user.getUsername();

        // when
        boolean isDelete = boardService.delete(adminBoardId, username);

        // then
        assertEquals(false, isDelete);
    }

    @Test
    public void test_deleteBoard_자신이쓴_Board_삭제() throws Exception {
        // given
        Long userBoardId = userBoard.getBoardId();
        String username = user.getUsername();

        // when
        boolean isDelete = boardService.delete(userBoardId, username);

        // then
        assertEquals(true, isDelete);
    }
}