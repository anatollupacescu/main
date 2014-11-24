package mr.odata;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

public interface ProductRepository extends Repository<Product, Long> {

	Page<Product> findAll(Pageable pageable);

	Product findByName(String name);
	
	@Transactional
	Product save(Product bean);
}