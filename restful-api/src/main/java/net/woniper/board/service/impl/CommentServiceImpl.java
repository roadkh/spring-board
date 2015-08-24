package net.woniper.board.service.impl;

import net.woniper.board.domain.Board;
import net.woniper.board.domain.Comment;
import net.woniper.board.domain.User;
import net.woniper.board.domain.type.AuthorityType;
import net.woniper.board.repository.BoardRepository;
import net.woniper.board.repository.CommentRepository;
import net.woniper.board.repository.UserRepository;
import net.woniper.board.service.CommentService;
import net.woniper.board.support.dto.CommentDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by woniper on 15. 2. 7..
 */
@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    @Autowired private CommentRepository commentRepository;
    @Autowired private BoardRepository boardRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ModelMapper modelMapper;

    @Override
    public Comment createComment(CommentDto commentDto, Long boardId, String username) {
        User user = userRepository.findByUsername(username);
        Board board = boardRepository.findOne(boardId);
        if(board != null) {
            Comment newComment = modelMapper.map(commentDto, Comment.class);
            newComment.setUser(user);
            newComment.setBoard(board);
            return commentRepository.save(newComment);
        }
        return null;
    }

    @Override
    public boolean deleteComment(Long commentId, String username) {
        User user = userRepository.findByUsername(username);
        if(user != null) {
            Comment comment = null;
            if(AuthorityType.ADMIN.equals(user.getAuthorityType())) {
                comment = commentRepository.findOne(commentId);
            } else {
                comment = commentRepository.findByCommentIdAndUser(commentId, user);
            }

            if(comment != null) {
                commentRepository.delete(commentId);
                return true;
            }
        }

        return false;
    }

    @Override
    public Comment updateComment(Long commentId, CommentDto commentDto, String username) {
        User user = userRepository.findByUsername(username);
        Comment comment = null;

        if(AuthorityType.ADMIN.equals(user.getAuthorityType())) {
            comment = commentRepository.findOne(commentId);
        } else {
            comment = commentRepository.findByCommentIdAndUser(commentId, user);
        }

        if(comment != null) {
            comment.update(commentDto);
        }

        return comment;
    }
}
