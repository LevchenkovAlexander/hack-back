package com.hack.botinki.demo.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="tasks")
@Getter
@Setter
public class Task {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate deadline;

    @Column(nullable = false)
    private Double priority;

    @Column(nullable = false)
    private Integer complexity;

    @Column(nullable = false)
    private Double estimatedHours;

    // Связь через proxy таблицу
    @OneToOne(mappedBy = "task", cascade = CascadeType.ALL)
    private Proxy proxy;

   
}
