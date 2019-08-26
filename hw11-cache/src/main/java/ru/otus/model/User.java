package ru.otus.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import java.util.Collection;

import static javax.persistence.CascadeType.ALL;

@Data
@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private Integer age;

    @OneToOne(cascade = ALL)
    private AddressDataSet addressDataSet;

    @OneToMany(cascade = ALL)
    private Collection<PhoneDataSet> phoneDataSets;

    @PostLoad
    @SuppressWarnings("unused")
    public void postLoad() {
        if (phoneDataSets.isEmpty()) {
            phoneDataSets = null;
        }
    }

}
