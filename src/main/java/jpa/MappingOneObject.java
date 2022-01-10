package jpa;

import javax.persistence.*;

@Entity
public class MappingOneObject extends BaseEntity {

    @Id @GeneratedValue
    private Long id;
    private String name;

    @OneToOne(mappedBy = "oneObject", fetch = FetchType.LAZY)
    private MappingMember mappingMember;
}
