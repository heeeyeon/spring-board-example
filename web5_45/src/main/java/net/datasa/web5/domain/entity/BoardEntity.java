package net.datasa.web5.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 정보 엔티티
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "web5_board")
public class BoardEntity {
    //게시글 테이블의 기본키
    //기본키 생성 전략 : 자동 증가(auto-increment) 값으로 설정
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_num")
    private Integer boardNum;                       //게시글 일련번호

    //작성자 정보 (외래키)
    //다대일 관계. 게시글 여러개가 회원정보 하나를 참조한다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    private MemberEntity member;

    //글 제목
    @Column(name = "title", nullable = false, length = 1000)
    private String title;

    //글 내용
    @Column(name = "contents", nullable = false, columnDefinition = "text")
    private String contents;

    //조회수
    @Column(name = "view_count", columnDefinition = "integer default 0")
    private Integer viewCount = 0;

    //추천수
    @Column(name = "like_count", columnDefinition = "integer default 0")
    private Integer likeCount = 0;

    //첨부파일의 원래 이름
    @Column(name = "original_name")
    private String originalName;

    //첨부파일의 저장된 이름
    @Column(name = "file_name")
    private String fileName;

    //작성 시간
    @CreatedDate
    @Column(name = "create_date", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createDate;

    //수정 시간
    @LastModifiedDate
    @Column(name = "update_date", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime updateDate;

    //게시글의 리플 정보
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReplyEntity> replyList;

}
