package com.barbup.barbup_api.domain.entity.schedule;

import com.barbup.barbup_api.domain.abstracts.BaseEntity;
import com.barbup.barbup_api.domain.entity.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "barber_time_off")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BarberTimeOff extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "barbershop_member_id", nullable = false)
    private Member barber;

    @Column(nullable = false)
    private LocalDateTime startAt;
    @Column(nullable = false)
    private LocalDateTime endAt;
    @Column(nullable = false)
    private String reason;
}
