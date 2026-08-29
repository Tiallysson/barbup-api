package com.barbup.barbup_api.domain.entity.schedule;

import com.barbup.barbup_api.domain.abstracts.BaseEntity;
import com.barbup.barbup_api.domain.entity.barbershop.Barbershop;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(
        name = "business_hours",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_business_hours_barbershop_day",
                columnNames = {"barbershop_id", "day_of_week"}
        )
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BusinessHours extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "barbershop_id", nullable = false)
    private Barbershop barbershop;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;
    @Column(nullable = false)
    private LocalTime openTime;
    @Column(nullable = false)
    private LocalTime closeTime;
}
