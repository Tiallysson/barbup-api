package com.barbup.barbup_api.services;

import com.barbup.barbup_api.domain.entity.appointment.Appointment;
import com.barbup.barbup_api.domain.entity.appointment.AppointmentSource;
import com.barbup.barbup_api.domain.entity.appointment.AppointmentStatus;
import com.barbup.barbup_api.domain.entity.barbershop.Barbershop;
import com.barbup.barbup_api.domain.entity.member.Member;
import com.barbup.barbup_api.domain.entity.member.MemberRole;
import com.barbup.barbup_api.domain.entity.schedule.BarberSchedule;
import com.barbup.barbup_api.domain.entity.schedule.BusinessHours;
import com.barbup.barbup_api.domain.entity.service.Services;
import com.barbup.barbup_api.domain.entity.user.User;
import com.barbup.barbup_api.infra.persistence.AppointmentRepository;
import com.barbup.barbup_api.infra.persistence.BarberScheduleRepository;
import com.barbup.barbup_api.infra.persistence.BarberTimeOffRepository;
import com.barbup.barbup_api.infra.persistence.BarbershopRepository;
import com.barbup.barbup_api.infra.persistence.BusinessHoursRepository;
import com.barbup.barbup_api.infra.persistence.MemberRepository;
import com.barbup.barbup_api.infra.persistence.ServiceRepository;
import com.barbup.barbup_api.shared.dto.appointment.AppointmentResponseDTO;
import com.barbup.barbup_api.shared.dto.appointment.AvailabilityResponseDTO;
import com.barbup.barbup_api.shared.dto.appointment.AvailableBarberDTO;
import com.barbup.barbup_api.shared.dto.appointment.AvailableSlotDTO;
import com.barbup.barbup_api.shared.dto.appointment.CreateAppointmentDTO;
import com.barbup.barbup_api.shared.exception.SlotUnavailableException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private static final Duration SLOT_STEP = Duration.ofMinutes(15);
    private static final Duration APPOINTMENT_LOOKBACK = Duration.ofDays(1);
    private static final Set<MemberRole> BARBER_ROLES = EnumSet.of(MemberRole.OWNER, MemberRole.BARBER);
    private static final Set<AppointmentStatus> NON_BLOCKING_STATUSES = EnumSet.of(AppointmentStatus.CANCELLED, AppointmentStatus.NO_SHOW);

    private final BarbershopRepository barbershopRepository;
    private final ServiceRepository serviceRepository;
    private final MemberRepository memberRepository;
    private final BusinessHoursRepository businessHoursRepository;
    private final BarberScheduleRepository barberScheduleRepository;
    private final BarberTimeOffRepository barberTimeOffRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional(readOnly = true)
    public AvailabilityResponseDTO getAvailability(UUID barbershopId, UUID serviceId, LocalDate date, UUID barberId) {
        Barbershop barbershop = findBarbershop(barbershopId);
        Services service = findService(serviceId, barbershopId);
        ZoneId zone = ZoneId.of(barbershop.getTimeZone());
        Duration duration = Duration.ofMinutes(service.getDurationMinutes());

        Map<Instant, List<Member>> slots = computeSlots(barbershopId, zone, date, duration, resolveBarbers(barbershopId, barberId));

        List<AvailableSlotDTO> slotDtos = slots.entrySet().stream()
                .map(e -> new AvailableSlotDTO(
                        LocalTime.ofInstant(e.getKey(), zone),
                        e.getKey(),
                        e.getKey().plus(duration),
                        e.getValue()
                                .stream()
                                .map(this::toBarberDto)
                                .toList()
                        )
                )
                .toList();

        return new AvailabilityResponseDTO(barbershopId, serviceId, date, zone.getId(), service.getDurationMinutes(), slotDtos);
    }

    @Transactional
    public AppointmentResponseDTO book(UUID barbershopId, User client, CreateAppointmentDTO dto) {
        Barbershop barbershop = findBarbershop(barbershopId);
        Services service = findService(dto.serviceId(), barbershopId);
        ZoneId zone = ZoneId.of(barbershop.getTimeZone());
        Duration duration = Duration.ofMinutes(service.getDurationMinutes());
        Instant start = dto.startAt();
        Instant end = start.plus(duration);
        LocalDate date = LocalDate.ofInstant(start, zone);

        List<Member> candidates = computeSlots(barbershopId, zone, date, duration, resolveBarbers(barbershopId, dto.barberId()))
                .get(start);
        if (candidates == null || candidates.isEmpty()) {
            throw new SlotUnavailableException("The selected time is not available");
        }

        for (Member candidate : candidates) {
            Member barber = memberRepository.lockById(candidate.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Barber not found"));
            if (hasConflict(barber.getId(), start, end)) {
                continue;
            }

            Appointment appointment = new Appointment();
            appointment.setBarbershop(barbershop);
            appointment.setBarber(barber);
            appointment.setClient(client);
            appointment.setService(service);
            appointment.setScheduledAt(start);
            appointment.setDurationMinutes(service.getDurationMinutes());
            appointment.setPrice(service.getPrice());
            appointment.setStatus(AppointmentStatus.CONFIRMED);
            appointment.setNotes(dto.notes());
            appointment.setCreatedBySource(AppointmentSource.CLIENT.name());

            return new AppointmentResponseDTO(appointmentRepository.save(appointment));
        }

        throw new SlotUnavailableException("The selected time is not available");
    }

    private Map<Instant, List<Member>> computeSlots(UUID barbershopId, ZoneId zone, LocalDate date, Duration duration, List<Member> barbers) {
        Map<Instant, List<Member>> result = new TreeMap<>();
        if (barbers.isEmpty()) {
            return result;
        }

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        List<BusinessHours> hours = businessHoursRepository.findAllByBarbershopIdAndDayOfWeek(barbershopId, dayOfWeek);
        if (hours.isEmpty()) {
            return result;
        }

        List<UUID> barberIds = barbers.stream().map(Member::getId).toList();
        Set<UUID> barbersWithSchedule = barberScheduleRepository.findAllByBarberIdIn(barberIds).stream()
                .map(s -> s.getBarber().getId())
                .collect(Collectors.toSet());
        Map<UUID, List<BarberSchedule>> schedulesOfDay = barberScheduleRepository.findAllByBarberIdInAndDayOfWeek(barberIds, dayOfWeek).stream()
                .collect(Collectors.groupingBy(s -> s.getBarber().getId()));

        Instant dayStart = date.atStartOfDay(zone).toInstant();
        Instant dayEnd = date.plusDays(1).atStartOfDay(zone).toInstant();

        Map<UUID, List<Interval>> busy = new HashMap<>();
        appointmentRepository.findBlockingByBarbers(barberIds, NON_BLOCKING_STATUSES, dayStart.minus(APPOINTMENT_LOOKBACK), dayEnd)
                .forEach(a -> busy.computeIfAbsent(a.getBarber().getId(), k -> new ArrayList<>())
                        .add(appointmentInterval(a)));
        barberTimeOffRepository.findOverlapping(barberIds, dayStart, dayEnd)
                .forEach(t -> busy.computeIfAbsent(t.getBarber().getId(), k -> new ArrayList<>())
                        .add(new Interval(t.getStartAt(), t.getEndAt())));

        Instant now = Instant.now();
        for (Member barber : barbers) {
            List<BarberSchedule> schedules = barbersWithSchedule.contains(barber.getId())
                    ? schedulesOfDay.getOrDefault(barber.getId(), List.of())
                    : null;
            List<Interval> barberBusy = busy.getOrDefault(barber.getId(), List.of());

            for (Interval window : workingWindows(date, zone, hours, schedules)) {
                for (Instant t = window.start(); !t.plus(duration).isAfter(window.end()); t = t.plus(SLOT_STEP)) {
                    if (!t.isAfter(now)) {
                        continue;
                    }
                    Interval slot = new Interval(t, t.plus(duration));
                    if (barberBusy.stream().noneMatch(slot::overlaps)) {
                        result.computeIfAbsent(t, k -> new ArrayList<>()).add(barber);
                    }
                }
            }
        }
        return result;
    }

    private List<Interval> workingWindows(LocalDate date, ZoneId zone, List<BusinessHours> hours, List<BarberSchedule> schedules) {
        List<Interval> windows = new ArrayList<>();
        for (BusinessHours bh : hours) {
            if (schedules == null) {
                windows.add(toInterval(date, zone, bh.getOpenTime(), bh.getCloseTime()));
                continue;
            }
            for (BarberSchedule s : schedules) {
                LocalTime start = max(bh.getOpenTime(), s.getStartTime());
                LocalTime end = min(bh.getCloseTime(), s.getEndTime());
                if (start.isBefore(end)) {
                    windows.add(toInterval(date, zone, start, end));
                }
            }
        }
        return windows;
    }

    private boolean hasConflict(UUID barberId, Instant start, Instant end) {
        Interval requested = new Interval(start, end);
        return appointmentRepository.findBlockingByBarbers(List.of(barberId), NON_BLOCKING_STATUSES, start.minus(APPOINTMENT_LOOKBACK), end)
                .stream()
                .anyMatch(a -> requested.overlaps(appointmentInterval(a)));
    }

    private List<Member> resolveBarbers(UUID barbershopId, UUID barberId) {
        if (barberId == null) {
            return memberRepository.findAllByBarbershopIdAndRoleIn(barbershopId, BARBER_ROLES);
        }
        return memberRepository.findByIdAndBarbershopIdAndRoleIn(barberId, barbershopId, BARBER_ROLES)
                .map(List::of)
                .orElseThrow(() -> new EntityNotFoundException("Barber not found"));
    }

    private Barbershop findBarbershop(UUID barbershopId) {
        return barbershopRepository.findById(barbershopId)
                .orElseThrow(() -> new EntityNotFoundException("Barbershop not found"));
    }

    private Services findService(UUID serviceId, UUID barbershopId) {
        return serviceRepository.findByIdAndBarbershopId(serviceId, barbershopId)
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));
    }

    private AvailableBarberDTO toBarberDto(Member member) {
        return new AvailableBarberDTO(member.getId(), member.getUser().getName());
    }

    private static Interval appointmentInterval(Appointment appointment) {
        return new Interval(appointment.getScheduledAt(),
                appointment.getScheduledAt().plus(Duration.ofMinutes(appointment.getDurationMinutes())));
    }

    private static Interval toInterval(LocalDate date, ZoneId zone, LocalTime start, LocalTime end) {
        return new Interval(date.atTime(start).atZone(zone).toInstant(), date.atTime(end).atZone(zone).toInstant());
    }

    private static LocalTime max(LocalTime a, LocalTime b) {
        return a.isAfter(b) ? a : b;
    }

    private static LocalTime min(LocalTime a, LocalTime b) {
        return a.isBefore(b) ? a : b;
    }

    private record Interval(Instant start, Instant end) {
        boolean overlaps(Interval other) {
            return start.isBefore(other.end) && other.start.isBefore(end);
        }
    }
}
