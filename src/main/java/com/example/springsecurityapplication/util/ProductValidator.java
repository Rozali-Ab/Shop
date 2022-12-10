package com.example.springsecurityapplication.util;

import com.example.springsecurityapplication.models.Product;
import com.example.springsecurityapplication.services.ProductService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ProductValidator implements Validator {

    private final ProductService productService;

    public ProductValidator(ProductService productService) {
        this.productService = productService;
    }


    //указываем для какой модели предназначен данный валидатор
    @Override
    public boolean supports(Class<?> clazz) {
        return Product.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Product product = (Product) target;
        if(productService.getProductFindByTitle(product) != null){
            errors.rejectValue("title", "","Данное наименование товара уже используется");
        }
    }
}
