package net.jaggerwang.sbip.adapter.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Component;
import net.jaggerwang.sbip.adapter.repository.jpa.PostJpaRepository;
import net.jaggerwang.sbip.adapter.repository.jpa.entity.PostDo;
import net.jaggerwang.sbip.adapter.repository.jpa.entity.PostLikeDo;
import net.jaggerwang.sbip.adapter.repository.jpa.entity.QPostDo;
import net.jaggerwang.sbip.adapter.repository.jpa.entity.QPostLikeDo;
import net.jaggerwang.sbip.adapter.repository.jpa.entity.QUserFollowDo;
import net.jaggerwang.sbip.adapter.repository.jpa.PostLikeJpaRepository;
import net.jaggerwang.sbip.entity.PostEntity;
import net.jaggerwang.sbip.usecase.port.repository.PostRepository;

@Component
public class PostRepositoryImpl implements PostRepository {
    private JPAQueryFactory jpaQueryFactory;
    private PostJpaRepository postJpaRepo;
    private PostLikeJpaRepository postLikeJpaRepo;

    public PostRepositoryImpl(JPAQueryFactory jpaQueryFactory, PostJpaRepository postJpaRepo,
            PostLikeJpaRepository postLikeJpaRepo) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.postJpaRepo = postJpaRepo;
        this.postLikeJpaRepo = postLikeJpaRepo;
    }

    @Override
    public PostEntity save(PostEntity postEntity) {
        return postJpaRepo.save(PostDo.fromEntity(postEntity)).toEntity();
    }

    @Override
    public void delete(Long id) {
        postJpaRepo.deleteById(id);
    }

    @Override
    public Optional<PostEntity> findById(Long id) {
        return postJpaRepo.findById(id).map(postDo -> postDo.toEntity());
    }

    private JPAQuery<PostDo> publishedQuery(Long userId) {
        var post = QPostDo.postDo;
        var query = jpaQueryFactory.selectFrom(post);
        if (userId != null) {
            query.where(post.userId.eq(userId));
        }
        return query;
    }

    @Override
    public List<PostEntity> published(Long userId, Long limit, Long offset) {
        var query = publishedQuery(userId);
        var post = QPostDo.postDo;
        query.orderBy(post.id.desc());
        if (limit != null) {
            query.limit(limit);
        }
        if (offset != null) {
            query.offset(offset);
        }

        return query.fetch().stream().map(postDo -> postDo.toEntity()).collect(Collectors.toList());
    }

    @Override
    public Long publishedCount(Long userId) {
        return publishedQuery(userId).fetchCount();
    }

    @Override
    public void like(Long userId, Long postId) {
        postLikeJpaRepo.save(PostLikeDo.builder().userId(userId).postId(postId).build());
    }

    @Override
    public void unlike(Long userId, Long postId) {
        postLikeJpaRepo.deleteByUserIdAndPostId(userId, postId);
    }

    private JPAQuery<PostDo> likedQuery(Long userId) {
        var post = QPostDo.postDo;
        var postLike = QPostLikeDo.postLikeDo;
        var query = jpaQueryFactory.selectFrom(post).join(postLike).on(post.id.eq(postLike.postId));
        if (userId != null) {
            query.where(postLike.userId.eq(userId));
        }
        return query;
    }

    @Override
    public List<PostEntity> liked(Long userId, Long limit, Long offset) {
        var query = likedQuery(userId);
        var postLike = QPostLikeDo.postLikeDo;
        query.orderBy(postLike.id.desc());
        if (limit != null) {
            query.limit(limit);
        }
        if (offset != null) {
            query.offset(offset);
        }

        return query.fetch().stream().map(postDo -> postDo.toEntity()).collect(Collectors.toList());
    }

    @Override
    public Long likedCount(Long userId) {
        return likedQuery(userId).fetchCount();
    }

    private JPAQuery<PostDo> followingQuery(Long userId) {
        var post = QPostDo.postDo;
        var userFollow = QUserFollowDo.userFollowDo;
        var query = jpaQueryFactory.selectFrom(post).join(userFollow)
                .on(post.userId.eq(userFollow.followingId)).where(userFollow.followerId.eq(userId));
        return query;
    }

    @Override
    public List<PostEntity> following(Long userId, Long limit, Long beforeId, Long afterId) {
        var query = followingQuery(userId);
        var post = QPostDo.postDo;
        query.orderBy(post.id.desc());
        if (limit != null) {
            query.limit(limit);
        }
        if (beforeId != null) {
            query.where(post.id.lt(beforeId));
        }
        if (afterId != null) {
            query.where(post.id.gt(afterId));
        }

        return query.fetch().stream().map(postDo -> postDo.toEntity()).collect(Collectors.toList());
    }

    @Override
    public Long followingCount(Long userId) {
        return followingQuery(userId).fetchCount();
    }

    @Override
    public Boolean isLiked(Long userId, Long postId) {
        return postLikeJpaRepo.existsByUserIdAndPostId(userId, postId);
    }
}
