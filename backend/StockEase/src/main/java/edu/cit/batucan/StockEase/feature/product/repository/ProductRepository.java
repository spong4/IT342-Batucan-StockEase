package edu.cit.batucan.StockEase.feature.product.repository;

import edu.cit.batucan.StockEase.feature.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByIsActiveTrue(Pageable pageable);
    Page<Product> findByIsActiveTrueAndCategory(String category, Pageable pageable);
    Page<Product> findByCreatedById(Long userId, Pageable pageable);
}
