package jpa;

import javax.persistence.*;

@Entity
public class MemberObjects {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MAP_MEMBER_ID")
    private MappingMember member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MAP_OBJECT_ID")
    private MappingManyObject manyObjects;
}
