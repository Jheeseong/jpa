package jpa;

public class MemberDTO {

    private String username;
    private int age;

    public MemberDTO(String name, int age) {
        this.username = name;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
