package jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class MappingOneObject extends BaseEntity {

    @Id @GeneratedValue
    private Long id;
    private String name;

    @OneToOne(mappedBy = "oneObject")
    private MappingMember mappingMember;
}
