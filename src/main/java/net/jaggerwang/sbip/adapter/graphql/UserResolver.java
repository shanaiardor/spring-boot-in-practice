package net.jaggerwang.sbip.adapter.graphql;

import java.util.Optional;
import com.coxautodev.graphql.tools.GraphQLResolver;
import org.springframework.stereotype.Component;
import net.jaggerwang.sbip.entity.FileEntity;
import net.jaggerwang.sbip.entity.UserEntity;
import net.jaggerwang.sbip.entity.UserStatEntity;

@Component
public class UserResolver extends BaseResolver implements GraphQLResolver<UserEntity> {
    public Optional<FileEntity> avatar(UserEntity userEntity) {
        if (userEntity.getAvatarId() == null) {
            return Optional.empty();
        }

        var fileEntity = fileUsecases.info(userEntity.getAvatarId());
        return Optional.of(fileEntity);
    }

    public UserStatEntity stat(UserEntity userEntity) {
        return statUsecases.userStatInfoByUserId(userEntity.getId());
    }

    public Boolean following(UserEntity userEntity) {
        return userUsecases.isFollowing(loggedUserId(), userEntity.getId());
    }
}