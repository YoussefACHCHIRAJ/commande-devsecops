package ma.zs.stocky.ws.facade.admin.catalog;


import ma.zs.stocky.bean.core.catalog.Product;
import ma.zs.stocky.dao.criteria.core.catalog.ProductCriteria;
import ma.zs.stocky.service.facade.admin.catalog.ProductAdminService;
import ma.zs.stocky.ws.converter.catalog.ProductConverter;
import ma.zs.stocky.ws.dto.catalog.ProductDto;
import ma.zs.stocky.zynerator.util.PaginatedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/admin/product/")
public class ProductRestAdmin {

    // no opration
    
    @PostMapping("import-data")
    public ResponseEntity<List<ProductDto>> importData(@RequestBody List<ProductDto> dtos) {
        List<Product> items = converter.toItem(dtos);
        List<Product> imported = this.service.importData(items);
        List<ProductDto> result = converter.toDto(imported);
        return ResponseEntity.ok(result);
    }


    
    @GetMapping("")
    public ResponseEntity<List<ProductDto>> findAll() throws Exception {
        ResponseEntity<List<ProductDto>> res = null;
        List<Product> list = service.findAll();
        HttpStatus status = HttpStatus.NO_CONTENT;
        List<ProductDto> dtos = converter.toDto(list);
        if (dtos != null && !dtos.isEmpty())
            status = HttpStatus.OK;
        res = new ResponseEntity<>(dtos, status);
        return res;
    }

    
    @GetMapping("optimized")
    public ResponseEntity<List<ProductDto>> findAllOptimized() throws Exception {
        ResponseEntity<List<ProductDto>> res = null;
        List<Product> list = service.findAllOptimized();
        HttpStatus status = HttpStatus.NO_CONTENT;
        List<ProductDto> dtos = converter.toDto(list);
        if (dtos != null && !dtos.isEmpty())
            status = HttpStatus.OK;
        res = new ResponseEntity<>(dtos, status);
        return res;
    }

    @GetMapping("id/{id}")
    public ResponseEntity<ProductDto> findById(@PathVariable Long id) {
        Product t = service.findById(id);
        if (t != null) {
            ProductDto dto = converter.toDto(t);
            return getDtoResponseEntity(dto);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("reference/{reference}")
    public ResponseEntity<ProductDto> findByReference(@PathVariable String reference) {
        Product t = service.findByReferenceEntity(new Product(reference));
        if (t != null) {
            ProductDto dto = converter.toDto(t);
            return getDtoResponseEntity(dto);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("")
    public ResponseEntity<ProductDto> save(@RequestBody ProductDto dto) throws Exception {
        if (dto != null) {
            Product myT = converter.toItem(dto);
            Product t = service.create(myT);
            if (t == null) {
                return new ResponseEntity<>(null, HttpStatus.IM_USED);
            } else {
                ProductDto myDto = converter.toDto(t);
                return new ResponseEntity<>(myDto, HttpStatus.CREATED);
            }
        } else {
            return new ResponseEntity<>(dto, HttpStatus.NO_CONTENT);
        }
    }

    @PutMapping("")
    public ResponseEntity<ProductDto> update(@RequestBody ProductDto dto) throws Exception {
        ResponseEntity<ProductDto> res;
        if (dto.getId() == null || service.findById(dto.getId()) == null)
            res = new ResponseEntity<>(HttpStatus.CONFLICT);
        else {
            Product t = service.findById(dto.getId());
            converter.copy(dto, t);
            Product updated = service.update(t);
            ProductDto myDto = converter.toDto(updated);
            res = new ResponseEntity<>(myDto, HttpStatus.OK);
        }
        return res;
    }

    @PostMapping("multiple")
    public ResponseEntity<List<ProductDto>> delete(@RequestBody List<ProductDto> dtos) throws Exception {
        ResponseEntity<List<ProductDto>> res;
        HttpStatus status = HttpStatus.CONFLICT;
        if (dtos != null && !dtos.isEmpty()) {
            List<Product> ts = converter.toItem(dtos);
            service.delete(ts);
            status = HttpStatus.OK;
        }
        res = new ResponseEntity<>(dtos, status);
        return res;
    }

    @DeleteMapping("")
    public ResponseEntity<ProductDto> delete(@RequestBody ProductDto dto) throws Exception {
        ResponseEntity<ProductDto> res;
        HttpStatus status = HttpStatus.CONFLICT;
        if (dto != null) {
            Product t = converter.toItem(dto);
            service.delete(Arrays.asList(t));
            status = HttpStatus.OK;
        }
        res = new ResponseEntity<>(dto, status);
        return res;
    }

    @DeleteMapping("id/{id}")
    public ResponseEntity<Long> deleteById(@PathVariable Long id) throws Exception {
        ResponseEntity<Long> res;
        HttpStatus status = HttpStatus.PRECONDITION_FAILED;
        if (id != null) {
            boolean resultDelete = service.deleteById(id);
            if (resultDelete) {
                status = HttpStatus.OK;
            }
        }
        res = new ResponseEntity<>(id, status);
        return res;
    }

    @DeleteMapping("multiple/id")
    public ResponseEntity<List<Long>> deleteByIdIn(@RequestBody List<Long> ids) throws Exception {
        ResponseEntity<List<Long>> res;
        HttpStatus status = HttpStatus.CONFLICT;
        if (ids != null) {
            service.deleteByIdIn(ids);
            status = HttpStatus.OK;
        }
        res = new ResponseEntity<>(ids, status);
        return res;
    }


    @GetMapping("detail/id/{id}")
    public ResponseEntity<ProductDto> findWithAssociatedLists(@PathVariable Long id) {
        Product loaded = service.findWithAssociatedLists(id);
        ProductDto dto = converter.toDto(loaded);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("find-by-criteria")
    public ResponseEntity<List<ProductDto>> findByCriteria(@RequestBody ProductCriteria criteria) throws Exception {
        ResponseEntity<List<ProductDto>> res = null;
        List<Product> list = service.findByCriteria(criteria);
        HttpStatus status = HttpStatus.NO_CONTENT;
        List<ProductDto> dtos = converter.toDto(list);
        if (dtos != null && !dtos.isEmpty())
            status = HttpStatus.OK;

        res = new ResponseEntity<>(dtos, status);
        return res;
    }

    @PostMapping("find-paginated-by-criteria")
    public ResponseEntity<PaginatedList> findPaginatedByCriteria(@RequestBody ProductCriteria criteria) throws Exception {
        List<Product> list = service.findPaginatedByCriteria(criteria, criteria.getPage(), criteria.getMaxResults(), criteria.getSortOrder(), criteria.getSortField());
        List<ProductDto> dtos = converter.toDto(list);
        PaginatedList paginatedList = new PaginatedList();
        paginatedList.setList(dtos);
        if (dtos != null && !dtos.isEmpty()) {
            int dateSize = service.getDataSize(criteria);
            paginatedList.setDataSize(dateSize);
        }
        return new ResponseEntity<>(paginatedList, HttpStatus.OK);
    }

    @PostMapping("data-size-by-criteria")
    public ResponseEntity<Integer> getDataSize(@RequestBody ProductCriteria criteria) throws Exception {
        int count = service.getDataSize(criteria);
        return new ResponseEntity<Integer>(count, HttpStatus.OK);
    }

    public List<ProductDto> findDtos(List<Product> list) {
        List<ProductDto> dtos = converter.toDto(list);
        return dtos;
    }

    private ResponseEntity<ProductDto> getDtoResponseEntity(ProductDto dto) {
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @Autowired
    private ProductAdminService service;
    @Autowired
    private ProductConverter converter;


}
