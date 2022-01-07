package jpa;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class MemberObjects {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private MappingMember member;

    @ManyToOne
    @JoinColumn(name = "OBJECT_ID")
    private MappingManyObject manyObject;
}
