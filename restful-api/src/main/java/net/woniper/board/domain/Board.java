package net.woniper.board.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Builder;
import lombok.extern.slf4j.Slf4j;
import net.woniper.board.support.dto.BoardDto;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* Created by woniper on 15. 1. 26..
*/
@Entity(name = "board")
@Getter @Setter
@Slf4j
@Builder
@ToString(exclude = {"user", "comments"})
@NoArgsConstructor
@AllArgsConstructor
public class Board implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long boardId;

    @Column(nullable = false, length = 50)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    private int readCount;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate = new Date();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FileInfo> fileInfos = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    private KindBoard kindBoard;

    public Board(String title, String content) {
        setTitle(title);
        setContent(content);
    }

    public void setUser(User user) {
        if (this.user != null) {
            this.user.getBoards().remove(this);
        }
        this.user = user;
        user.getBoards().add(this);
    }

    @Transient
    public int commentCount() {
        return comments != null ? comments.size() : 0;
    }

    @Transient
    public void patch(BoardDto boardDto) {
        String title = boardDto.getTitle();
        String content = boardDto.getContent();
        String kindBoardName = boardDto.getKindBoardName();
        if (StringUtils.isNotEmpty(title))
            setTitle(title);

        if (StringUtils.isNotEmpty(content))
            setContent(content);

        log.info("patch board {}", this);
    }

    @Transient
    public void update(BoardDto boardDto) {
        String title = boardDto.getTitle();
        String content = boardDto.getContent();

        if (StringUtils.isEmpty(title) || StringUtils.isEmpty(content))
            throw new IllegalArgumentException("board title or content is null");

        setTitle(boardDto.getTitle());
        setContent(boardDto.getContent());
        log.info("update board {}", this);
    }

    @Transient
    public void read() {
        readCount++;
    }

    @Transient
    public void addAttachFile(FileInfo fileInfo) {
        this.fileInfos.add(fileInfo);
    }
}