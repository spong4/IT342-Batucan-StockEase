package edu.cit.batucan.StockEase.feature.sales.repository;

import edu.cit.batucan.StockEase.feature.sales.model.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
    List<SaleItem> findBySaleId(Long saleId);
}
