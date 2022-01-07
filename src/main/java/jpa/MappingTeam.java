package jpa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MappingTeam {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team")
    List<MappingMember> members = new ArrayList<>();

    // 연관관계 편의 메소드
//    public void addMember(MappingMember mappingMember) {
//        mappingMember.setTeam(this);
//        members.add(mappingMember);
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MappingMember> getMembers() {
        return members;
    }

    public void setMembers(List<MappingMember> members) {
        this.members = members;
    }
}
