package jpa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MappingTeam extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
//    @JoinColumn(name = "TEAM_ID") // 연관관계 주인 설정
    List<MappingMember> members = new ArrayList<>();

    public void addTeam(MappingMember member) {
        members.add(member);
        member.setTeam(this);
    }

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

    @Override
    public String toString() {
        return "MappingTeam{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

