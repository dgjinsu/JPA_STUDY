package jpabasic.ex1hellojpa.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
//@Entity
public class Team {

    @Id
    @GeneratedValue
    private Long id;
    private String name;

}