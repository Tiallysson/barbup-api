package com.barbup.barbup_api.services;

import lombok.RequiredArgsConstructor;
import com.barbup.barbup_api.domain.entity.barbershop.Barbershop;
import com.barbup.barbup_api.domain.entity.service.ServiceRegister;
import com.barbup.barbup_api.domain.entity.service.ServiceResponse;
import com.barbup.barbup_api.domain.entity.service.ServiceUpdate;
import com.barbup.barbup_api.domain.entity.service.Services;
import com.barbup.barbup_api.domain.entity.user.User;
import com.barbup.barbup_api.infra.persistence.BarbershopRepository;
import com.barbup.barbup_api.infra.persistence.ServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceService {
    private final BarbershopRepository barbershopRepository;
    private final ServiceRepository serviceRepository;

    @PreAuthorize("@barbershopAccess.canManage(#register.barbershopId(), principal)")
    public ServiceResponse createService(ServiceRegister register, User authenticatedUser) {
        Barbershop barbershop = barbershopRepository.findById(register.barbershopId())
                .orElseThrow(() -> new EntityNotFoundException("Barbershop not found"));

        Services service = new Services();
        service.setBarbershop(barbershop);
        service.setDescription(register.description());
        service.setDurationMinutes(register.durationMinutes());
        service.setName(register.name());
        service.setPrice(register.price());

        serviceRepository.save(service);

        return new ServiceResponse(
                service.getId(),
                service.getBarbershop().getId(),
                service.getName(),
                service.getDescription(),
                service.getPrice(),
                service.getDurationMinutes()
                );
    }

    @PreAuthorize("@barbershopAccess.canManageService(#register.id(), principal)")
    public ServiceResponse updateService(ServiceUpdate register, User authenticatedUser) {
        Services services = serviceRepository.findById(register.id())
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));

        services.setName(register.name());
        services.setPrice(register.price());
        services.setDurationMinutes(register.durationMinutes());
        services.setDescription(register.description());
        serviceRepository.save(services);

        return new ServiceResponse(
                services.getId(),
                services.getBarbershop().getId(),
                services.getName(),
                services.getDescription(),
                services.getPrice(),
                services.getDurationMinutes()
        );
    }

    @PreAuthorize("@barbershopAccess.canManageService(#id, principal)")
    public boolean deleteService(UUID id, User authenticatedUser) {
        Services services = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));

        services.delete();
        serviceRepository.save(services);

        return  true;
    }

    public List<ServiceResponse> getServices(UUID barbershopId) {
        List<Services> services = serviceRepository.getAllByActiveTrueAndBarbershop_Id(barbershopId);

        return services.stream()
                .map(s -> new ServiceResponse(
                        s.getId(),
                        s.getBarbershop().getId(),
                        s.getName(),
                        s.getDescription(),
                        s.getPrice(),
                        s.getDurationMinutes()
                ))
                .collect(Collectors.toList());
    }
}
