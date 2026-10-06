package com.budgetflow.category;

import com.budgetflow.category.dto.CategoryRequest;
import com.budgetflow.category.dto.CategoryResponse;
import com.budgetflow.common.ApiException;
import com.budgetflow.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> list(User user) {
        return categoryRepository.findByUserIdOrderByNameAsc(user.getId())
                .stream().map(CategoryResponse::from).toList();
    }

    @Transactional
    public CategoryResponse create(User user, CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCaseAndUserId(request.name(), user.getId())) {
            throw new ApiException(HttpStatus.CONFLICT, "Une catégorie porte déjà ce nom");
        }
        Category category = Category.builder()
                .name(request.name())
                .color(request.color())
                .user(user)
                .build();
        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(User user, Long id, CategoryRequest request) {
        Category category = getOwned(user, id);
        category.setName(request.name());
        category.setColor(request.color());
        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public void delete(User user, Long id) {
        categoryRepository.delete(getOwned(user, id));
    }

    private Category getOwned(User user, Long id) {
        return categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> ApiException.notFound("Catégorie introuvable"));
    }
}
