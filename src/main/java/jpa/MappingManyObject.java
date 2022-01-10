package jpa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MappingManyObject extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "MAP_OBJECT_ID")
    private Long id;
    private String name;

    // ManyToMany 한계
//    @ManyToMany(mappedBy = "manyObjects")
//    private List<MappingMember> members = new ArrayList<>();

    // ManyToMany 한계 극복
    @OneToMany(mappedBy = "manyObjects")
    private List<MemberObjects> manyObjects = new ArrayList<>();

}
