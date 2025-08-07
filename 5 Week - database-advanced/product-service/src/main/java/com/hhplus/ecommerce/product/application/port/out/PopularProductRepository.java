package com.hhplus.ecommerce.product.application.port.out;

import com.hhplus.ecommerce.product.adapter.out.persistence.query.PopularProductReadModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PopularProductRepository extends JpaRepository<PopularProductReadModel, Long> {

}
