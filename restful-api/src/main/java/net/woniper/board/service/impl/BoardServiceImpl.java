package net.woniper.board.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.woniper.board.domain.Board;
import net.woniper.board.domain.KindBoard;
import net.woniper.board.domain.User;
import net.woniper.board.domain.type.AuthorityType;
import net.woniper.board.errors.support.BoardNotFoundException;
import net.woniper.board.errors.support.UserNotFoundException;
import net.woniper.board.repository.BoardRepository;
import net.woniper.board.repository.FileInfoRepository;
import net.woniper.board.repository.UserRepository;
import net.woniper.board.service.BoardService;
import net.woniper.board.service.KindBoardService;
import net.woniper.board.service.UserService;
import net.woniper.board.support.dto.BoardDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by woniper on 15. 1. 26..
 */
@Service
@Transactional
@Slf4j
public class BoardServiceImpl implements BoardService {

    private BoardRepository boardRepository;
    private UserRepository userRepository;
    @Autowired private FileInfoRepository fileInfoRepository;

    @Autowired private UserService userService;
    @Autowired private KindBoardService kindBoardService;
    @Autowired private ModelMapper modelMapper;

    @Autowired
    public void setBoardRepository(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Board save(BoardDto.Request boardDto, String username) {
        Board board = modelMapper.map(boardDto, Board.class);
        User user = userService.find(username);
        board.setUser(user);
        KindBoard kindBoard = kindBoardService.find(boardDto.getKindBoardName());
        board.setKindBoard(kindBoard);

        List<String> attachFiles = boardDto.getAttachFiles();
        if(attachFiles != null && !attachFiles.isEmpty()) {
            for (String file : attachFiles) {
                board.addAttachFile(fileInfoRepository.findByFileName(file));
            }
        }
        return boardRepository.save(board);
    }

    @Override
    public Board find(Long boardId) {
        Board board = boardRepository.findOne(boardId);
        if(board == null)
            throw new BoardNotFoundException(boardId);

        board.read();
        return board;
    }

    @Override
    public Page<Board> find(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    @Override
    public Page<Board> find(Pageable pageable, String username) {
        User user = userRepository.findByUsername(username);
        if(user == null)
            throw new UserNotFoundException(username);

        return boardRepository.findByUser(user, pageable);
    }

    @Override
    public Board update(Long boardId, BoardDto.Request boardDto, String username, String method) {
        User user = userService.find(username);
        Board board = null;
        if(isAccessPossibleUser(user)) {
            board = boardRepository.findOne(boardId);
        } else{
            board = boardRepository.findByBoardIdAndUser(boardId, user);
        }

        // todo : file update

        if(board == null)
            throw new BoardNotFoundException(boardId);

        KindBoard kindBoard = kindBoardService.find(boardDto.getKindBoardName());
        board.setKindBoard(kindBoard);

        if(RequestMethod.valueOf(method) == RequestMethod.PATCH)
            board.patch(boardDto);
        else
            board.update(boardDto);

        return board;
    }

    @Override
    public boolean delete(Long boardId, String username) {
        User user = userService.find(username);
        Board board = null;

        if(AuthorityType.ADMIN.equals(user.getAuthorityType())) {
            board = find(boardId);
        } else {
            board = boardRepository.findByBoardIdAndUser(boardId, user);
        }

        if(board != null) {
            boardRepository.delete(board);
            return true;
        } else {
            return false;
        }
    }

    private boolean isAccessPossibleUser(User user) {
        return AuthorityType.ADMIN == user.getAuthorityType();
    }

    /**
     * 게시글 삭제, 수정 가능한 계정인지 확인
     * admin 계정 OR 게시글을 등록한 계정만 가능
     * @param boardId
     * @param username
     * @return
     */
//    public boolean isBoardAccessRole(Long boardId, String username) {
//        Board board = boardRepository.findOne(boardId);
//        User user = userRepository.findByUsername(username);
//
//        if(board != null && user != null) {
//            if(user.isAdmin()) return true;
//            if(board.getUser().getUserId().equals(user.getUserId())) return true;
//        }
//
//        return false;
//    }
}
