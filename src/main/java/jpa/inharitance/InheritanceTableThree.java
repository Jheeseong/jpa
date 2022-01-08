package jpa.inharitance;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Three")
public class InheritanceTableThree extends InheritanceMain {

    private String threeName;
    private String threeOther;
}
