package jpa.inharitance;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("One")
public class InheritanceTableOne extends InheritanceMain {

    private String oneName;
    private String oneOther;
}
