package com.barbup.barbup_api.services;

import com.barbup.barbup_api.domain.entity.barbershop.Barbershop;
import com.barbup.barbup_api.domain.entity.service.ServiceRegister;
import com.barbup.barbup_api.domain.entity.service.ServiceResponse;
import com.barbup.barbup_api.domain.entity.service.ServiceUpdate;
import com.barbup.barbup_api.domain.entity.service.Services;
import com.barbup.barbup_api.domain.entity.user.User;
import com.barbup.barbup_api.infra.persistence.BarbershopRepository;
import com.barbup.barbup_api.infra.persistence.MemberRepository;
import com.barbup.barbup_api.infra.persistence.ServiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ServiceService {
    @Autowired
    private BarbershopRepository barbershopRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private MemberRepository memberRepository;

    public ServiceResponse createService(ServiceRegister register, User authenticatedUser) {
        Barbershop barbershop = barbershopRepository.getReferenceById(register.barbershopId());

        boolean isMember = memberRepository.existsByBarbershopIdAndUserId(barbershop.getId(), authenticatedUser.getId());
        if (!isMember) {
            throw new AccessDeniedException("User is not a member of this barbershop");
        }

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

    public ServiceResponse updateService(ServiceUpdate register, User authenticatedUser) {
        Barbershop barbershop = barbershopRepository.getReferenceById(register.barbershopId());

        boolean isMember = memberRepository.existsByBarbershopIdAndUserId(barbershop.getId(), authenticatedUser.getId());
        if (!isMember) {
            throw new AccessDeniedException("User is not a member of this barbershop");
        }

        Services services = serviceRepository.getReferenceById(register.id());
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

    public boolean deleteService(UUID id, User authenticatedUser) {
        Services services = serviceRepository.getReferenceById(id);

        boolean isMember = memberRepository.existsByBarbershopIdAndUserId(services.getBarbershop().getId(), authenticatedUser.getId());
        if (!isMember)
            throw new AccessDeniedException("User is not a member of this barbershop");

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
