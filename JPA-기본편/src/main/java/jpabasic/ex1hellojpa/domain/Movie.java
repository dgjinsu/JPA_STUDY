package jpabasic.ex1hellojpa.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Setter
@ToString
//@Entity
@DiscriminatorValue("Movie")
public class Movie extends Item {

    private String director;
    private String actor;

}