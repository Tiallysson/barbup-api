package com.barbup.barbup_api.domain.barbershop.appointment;

import com.barbup.barbup_api.domain.abstracts.BaseEntity;
import com.barbup.barbup_api.domain.barbershop.Barbershop;
import com.barbup.barbup_api.domain.barbershop.member.Member;
import com.barbup.barbup_api.domain.barbershop.service.Service;
import com.barbup.barbup_api.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "appointment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Appointment extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "barbershop_id", nullable = false)
    private Barbershop barbershop;

    @ManyToOne @JoinColumn(name = "barbershop_member_id", nullable = false)
    private Member barber;

    @ManyToOne @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(nullable = false)
    private Instant scheduledAt;
    @Column(nullable = false)
    private Integer durationMinutes;
    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    private String notes;
    @Column(nullable = false)
    private String createdBySource;

}
