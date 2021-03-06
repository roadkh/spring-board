package net.woniper.board.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.woniper.board.domain.type.AuthorityType;
import net.woniper.board.support.dto.UserDto;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by woniper on 15. 1. 28..
 */
@Entity(name = "user")
@Getter @Setter
@Slf4j
//@Builder
@ToString(exclude = {"boards", "password"})
@NoArgsConstructor
public class User implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String nickName;

    @Enumerated(EnumType.STRING)
    private AuthorityType authorityType;

    private boolean active = true;

    @Temporal(TemporalType.TIMESTAMP)
    private Date joinDate = new Date();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Board> boards = new ArrayList<>();

    public User(String username, String password, String firstName, String lastName,
                String nickName, AuthorityType authorityType, boolean active) {
        setUsername(username);
        setPassword(password);
        setFirstName(firstName);
        setLastName(lastName);
        setNickName(nickName);
        setAuthorityType(authorityType);
        setActive(active);
    }

    @Transient
    public int getBoardCount() {
        return boards != null ? boards.size() : 0;
    }

    @Transient
    public String getFullName() {
        return getLastName() + " " + getFirstName();
    }

    @Transient
    public void patch(UserDto.Request userDto) {
        String password = userDto.getPassword();
        String nickName = userDto.getNickName();
        String firstName = userDto.getFirstName();
        String lastName = userDto.getLastName();

        if(StringUtils.isNotEmpty(password))
            setPassword(password);
        if(StringUtils.isNotEmpty(nickName))
            setNickName(nickName);
        if(StringUtils.isNotEmpty(firstName))
            setFirstName(firstName);
        if(StringUtils.isNotEmpty(lastName))
            setLastName(lastName);
        log.info("patch User : {}", this);
    }

    @Transient
    public void update(UserDto.Request userDto) {
        String password = userDto.getPassword();
        String nickName = userDto.getNickName();
        String firstName = userDto.getFirstName();
        String lastName = userDto.getLastName();

        if(StringUtils.isEmpty(password) || StringUtils.isEmpty(nickName) ||
           StringUtils.isEmpty(firstName) || StringUtils.isEmpty(lastName))
            throw new IllegalArgumentException("argument is null");

        setPassword(userDto.getPassword());
        setNickName(userDto.getNickName());
        setFirstName(userDto.getFirstName());
        setLastName(userDto.getLastName());
        log.info("update User : {}", this);
    }
}
