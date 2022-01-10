package jpa;

import javax.persistence.*;

@Entity
public class MemberObjects {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MAP_MEMBER_ID")
    private MappingMember member;

    @ManyToOne
    @JoinColumn(name = "MAP_OBJECT_ID")
    private MappingManyObject manyObjects;
}
