package jpa;


import javax.persistence.*;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "name")
    private String username;
    @Embedded
    private Address homeAddress;
    @Embedded
    private Period workPeriod;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city",column = @Column(name = "WORK_CITY")),
            @AttributeOverride(name = "street",column = @Column(name = "WORK_STREET")),
            @AttributeOverride(name = "zipcode",column = @Column(name = "WORK_ZIPCODE"))
    })
    private Address workAddress;


//    private Integer age;
//    @Enumerated(EnumType.STRING)
//    private RoleType roleType;
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date createdDate;
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date lastModifiedDate;
//    @Lob
//    private String description;

    public Member() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Period getWorkPeriod() {
        return workPeriod;
    }

    public void setWorkPeriod(Period workPeriod) {
        this.workPeriod = workPeriod;
    }
}
