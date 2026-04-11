package edu.cit.batucan.StockEase.repository;

import edu.cit.batucan.StockEase.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
    List<SaleItem> findBySaleId(Long saleId);
}
