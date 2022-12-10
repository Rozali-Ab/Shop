package com.example.springsecurityapplication.services;

import com.example.springsecurityapplication.models.Product;
import com.example.springsecurityapplication.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    //вернуть весь лист товаров
    public List<Product> getAllProduct(){
        return productRepository.findAll();
    }

    //вернуть товар по id
    public Product getProductId(int id){
        Optional<Product> optionalProduct = productRepository.findById(id);
        return optionalProduct.orElse(null);
    }

    //сохранить объект продукта, который пришел с формы
    @Transactional
    public void saveProduct(Product product){
        productRepository.save(product);
    }

    //обновить информацию о продукте
    @Transactional
    public void updateProduct(int id, Product product){
        product.setId(id);
        productRepository.save(product);
    }

    //удалить товар по id
    @Transactional
    public void deleteProduct(int id){
        productRepository.deleteById(id);
    }


    //получить товар по наименованию
    public Product getProductFindByTitle(Product product){
        Optional<Product> product_db = productRepository.findByTitle(product.getTitle());
        return product_db.orElse(null);
    }

    //получить товары по категории
    public List<Product> getProductByCategory(int category){
        List<Product> productCategory = productRepository.findByCategory(category);
        return productCategory;
    }

}
