package ma.zs.stocky.unit.dao.facade.core.catalog;

import ma.zs.stocky.bean.core.catalog.Product;
import ma.zs.stocky.dao.facade.core.catalog.ProductDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
//@ActiveProfiles("test")
public class ProductDaoTest {

    @Autowired
    private ProductDao underTest;


    @Test
    void shoulDeleteById() {
        Long id = 1L;
        Product entity = new Product();
        entity.setId(id);
        underTest.save(entity);

        underTest.deleteById(id);

        Product loaded = underTest.findById(id).orElse(null);
        assertThat(loaded).isNull();
    }


    @Test
    void shouldFindAll() {
        underTest.deleteAll();
        // Given
        List<Product> items = IntStream.rangeClosed(1, 10).mapToObj(i -> constructSample(i)).collect(Collectors.toList());

        // Init
        items.forEach(underTest::save);

        // When
        List<Product> loadedItems = underTest.findAll();

        // Then
        assertThat(loadedItems).isNotNull();
        assertThat(loadedItems.size()).isEqualTo(10);
    }

    @Test
    void shouldSave() {
        Product given = constructSample(1);
        Product saved = underTest.save(given);
        assertThat(saved.getId()).isNotNull();
    }

    private Product constructSample(int i) {
        Product given = new Product();
        given.setCode("code-" + i);
        given.setReference("reference-" + i);
        return given;
    }

}
