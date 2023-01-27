package jpabasic.ex1hellojpa.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
//@Entity
public class Member {

    @Id @GeneratedValue
    private Long id;
    private String username;

    @ManyToOne(fetch = FetchType.LAZY) //얘를 프록시 객체로 조회
    @JoinColumn(name = "team_id")
    private Team team;

}