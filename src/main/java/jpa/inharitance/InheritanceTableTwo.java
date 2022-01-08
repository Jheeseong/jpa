package jpa.inharitance;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Two")
public class InheritanceTableTwo {

    private String twoName;
    private String twoOther;
}
