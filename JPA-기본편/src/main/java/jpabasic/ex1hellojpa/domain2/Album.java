package jpabasic.ex1hellojpa.domain2;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Setter
@ToString
@Entity
@DiscriminatorValue("Album")
public class Album extends Item {

    private String artist;
    private String etc;

}