package edu.cit.batucan.StockEase.repository;

import edu.cit.batucan.StockEase.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByIsActiveTrue(Pageable pageable);
    Page<Product> findByIsActiveTrueAndCategory(String category, Pageable pageable);
    Page<Product> findByCreatedById(Long userId, Pageable pageable);
}
