package com.gestionstock.stock.supplier;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository repository;

    public List<SupplierResponse> findAll() {
        return repository.findAll().stream().map(SupplierService::toResponse).toList();
    }

    public SupplierResponse findById(Integer id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fournisseur introuvable : " + id)));
    }

    @Transactional
    public SupplierResponse create(SupplierRequest request) {
        return toResponse(repository.save(Supplier.builder()
                .name(request.name())
                .registrationNumber(request.registrationNumber())
                .phone(request.phone())
                .email(request.email())
                .build()));
    }

    @Transactional
    public SupplierResponse update(Integer id, SupplierRequest request) {
        Supplier supplier = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fournisseur introuvable : " + id));
        supplier.setName(request.name());
        supplier.setRegistrationNumber(request.registrationNumber());
        supplier.setPhone(request.phone());
        supplier.setEmail(request.email());
        return toResponse(repository.save(supplier));
    }

    @Transactional
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Fournisseur introuvable : " + id);
        }
        repository.deleteById(id);
    }

    static SupplierResponse toResponse(Supplier s) {
        return new SupplierResponse(s.getId(), s.getName(), s.getRegistrationNumber(),
                s.getPhone(), s.getEmail());
    }
}
