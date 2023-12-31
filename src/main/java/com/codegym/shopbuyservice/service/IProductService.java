package com.codegym.shopbuyservice.service;

import com.codegym.shopbuyservice.dto.ProductDetailDto;
import com.codegym.shopbuyservice.dto.ProductDto;
import com.codegym.shopbuyservice.dto.payload.response.ProductResponseDto;

import java.util.List;
import java.util.Optional;

public interface IProductService {
List<Optional<ProductDto>> findProductByName (String nameProduct);
    ProductDto findByName(String nameProduct);
    ProductDetailDto detailProduct (Long productId) throws Exception;


}
