package com.michelmaia.quickbite.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class MenuItem {
    private Long id;
    private Restaurant restaurant;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private Boolean available;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
