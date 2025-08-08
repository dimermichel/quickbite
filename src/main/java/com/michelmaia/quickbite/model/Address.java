package com.michelmaia.quickbite.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;
}
