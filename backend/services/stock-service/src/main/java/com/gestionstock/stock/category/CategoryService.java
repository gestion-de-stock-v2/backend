package com.gestionstock.stock.category;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    public List<CategoryResponse> findAll() {
        return repository.findAll().stream().map(CategoryService::toResponse).toList();
    }

    public CategoryResponse findById(Integer id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categorie introuvable : " + id)));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        return toResponse(repository.save(Category.builder()
                .name(request.name())
                .description(request.description())
                .build()));
    }

    @Transactional
    public CategoryResponse update(Integer id, CategoryRequest request) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categorie introuvable : " + id));
        category.setName(request.name());
        category.setDescription(request.description());
        return toResponse(repository.save(category));
    }

    @Transactional
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Categorie introuvable : " + id);
        }
        repository.deleteById(id);
    }

    static CategoryResponse toResponse(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription());
    }
}
