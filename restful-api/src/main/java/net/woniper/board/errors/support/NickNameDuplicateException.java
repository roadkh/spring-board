package net.woniper.board.errors.support;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by woniper on 15. 2. 9..
 */
@AllArgsConstructor
public class NickNameDuplicateException extends RuntimeException {

    @Getter private final String nickName;

}
