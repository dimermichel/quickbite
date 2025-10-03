package com.michelmaia.quickbite.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Restaurant {
    private Long id;
    private User owner;
    private Address address;
    private String name;
    private String cuisine;
    private Double rating;
    private String openingHours;
    private Boolean isOpen;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Transient field for pagination
    @JsonIgnore
    private transient Long totalCount;
}
