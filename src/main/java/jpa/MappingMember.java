package jpa;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
//@NamedQuery(
//        name = "Member.findByName",
//        query = "select m from MappingMember m where m.name = :name"
//)
public class MappingMember extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "MAP_MEMBER_ID")
    private Long id;

    private String name;

    //참조대신 외래 키 그대로 사용
//   @Column(name = "TEAM_ID")
//    private Long teamId;

    //객체의 참조와 테이블의 외래 키 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    // OneToMany 양방향 읽기 전용 필드
//    @JoinColumn(insertable = false, updatable = false)
    private MappingTeam team;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ONE_ID")
    private MappingOneObject oneObject;

    @ElementCollection
    @CollectionTable(name = "FAVORITE_FOOD", joinColumns =@JoinColumn(name = "MEMBER_ID"))
    @Column(name = "FOOD_NAME")
    private Set<String> favoriteFoods = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "ADDRESS", joinColumns = @JoinColumn(name = "MEMBER_ID"))
    private List<Address> addressHistory = new ArrayList<>();

    //ManyToMany 한계
//    @ManyToMany
//    @JoinColumn(name = "MEMBER_OBJECTS")
//    private List<MappingManyObject> manyObjects = new ArrayList<>();

    //ManyToMany 한계 해결방안
    @OneToMany(mappedBy = "member")
    private List<MemberObjects> memberObjects = new ArrayList<>();


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

    public MappingTeam getTeam() {
        return team;
    }

    public void setTeam(MappingTeam team) {
        this.team = team;
    }
    //연관관계 편의 메소드
    public void changeTeam(MappingTeam team) {
        this.team = team;
        team.getMembers().add(this);
    }

    @Override
    public String toString() {
        return "MappingMember{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", team=" + team +
                '}';
    }
}
