package net.woniper.board.support.dto;

import com.wordnik.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.woniper.board.domain.type.AuthorityType;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by woniper on 15. 2. 4..
 */
@ApiModel(value = "comment Request")
@Getter @Setter
public class CommentDto extends ResourceSupport implements Serializable {

    @NotNull
    private String content;

    @ApiModel(value = "comment Response")
    @Getter @Setter
    @ToString
    public static class Response extends CommentDto {
        private Long commentId;
        private int depth;
        private Date createDate;

        private Long userId;
        private String username;
        private String nickName;
        private AuthorityType authorityType;
    }

}
