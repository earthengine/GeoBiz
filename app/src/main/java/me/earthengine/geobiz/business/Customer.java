package me.earthengine.geobiz.business;

import java.math.BigDecimal;

/**
 * Created by earthengine on 2015/6/12.
 */
public class Customer {
    public int id;
    public String firstName;
    public String lastName;
    public boolean firstNameLast;
    public String suburb;
    public String street;
    public String streetType;
    public String streetNo;
    public String frequency;
    public int dueDay;
    public BigDecimal amount;
    public String contactNumber;
    public String email;
    public String facebook;

    public Customer(int id, String firstName) {
        this.id = id;
        this.firstName = firstName;
    }

    @Override
    public String toString() {
        if(lastName!=null) {
            if (firstNameLast) {
                return lastName + ' ' + firstName;
            } else
                return firstName + ' ' + lastName;
        }
        else {
            return firstName;
        }
    }
}
