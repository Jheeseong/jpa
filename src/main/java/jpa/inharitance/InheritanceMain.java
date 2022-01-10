package jpa.inharitance;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn
public abstract class InheritanceMain {
    @Id @GeneratedValue
    private Long id;
    private int price;
    private int StockQuantity;
}
