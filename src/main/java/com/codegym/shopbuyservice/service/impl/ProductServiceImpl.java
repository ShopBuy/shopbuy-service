package com.codegym.shopbuyservice.service.impl;

import com.codegym.shopbuyservice.converter.IProductConvect;
import com.codegym.shopbuyservice.dto.PagingProductResponseDto;
import com.codegym.shopbuyservice.dto.ProductDetailDto;
import com.codegym.shopbuyservice.dto.ProductDto;
import com.codegym.shopbuyservice.dto.payload.response.PagingProductResponse;
import com.codegym.shopbuyservice.entity.Product;
import com.codegym.shopbuyservice.entity.Variant;
import com.codegym.shopbuyservice.repository.IProductPagingRepository;
import com.codegym.shopbuyservice.repository.IProductRepository;
import com.codegym.shopbuyservice.service.INameNormalizationService;
import com.codegym.shopbuyservice.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private IProductRepository iProductRepository;
    @Autowired
    private INameNormalizationService iNameNormalizationService;
    @Autowired
    private IProductConvect iProductConvect;
    @Autowired
    private IProductPagingRepository iProductPagingRepository;

    @Override
    public List<Optional<ProductDto>> findProductByName(String nameProduct) {
        nameProduct = iNameNormalizationService.normalizeName(nameProduct);
        List<Product> products = iProductRepository.findAll();
        List<ProductDto> productDtos = iProductConvect.convertToListDTO(products);
        List<Optional<ProductDto>> ProductDtoNew = new ArrayList<>();
        for (var item : productDtos) {
            if (ProductDtoNew.size() == 10)
                break;
            String movieName = iNameNormalizationService.normalizeName(item.getName());
            if (movieName.contains(nameProduct)) {
                ProductDtoNew.add(Optional.of(item));
            }
        }
        return ProductDtoNew;
    }

    @Override
    public ProductDto findByName(String nameProduct) {
        nameProduct = iNameNormalizationService.normalizeName(nameProduct);
        List<Product> movies = iProductRepository.findAll();
        Optional<Product> movie = Optional.empty();
        for (var item : movies) {
            String movieName = iNameNormalizationService.normalizeName(item.getName());
            if (movieName.equalsIgnoreCase(nameProduct)) {
                movie = Optional.of(item);
            }
        }
        return movie.map(value -> iProductConvect.convertToDTO(value)).orElse(null);
    }

    @Override
    public ProductDetailDto detailProduct(Long productId) throws Exception {
        Product product = iProductRepository.findProductById(productId).orElseThrow(() -> new Exception("Sản phẩm không tồn tại"));
        ProductDetailDto productDetailDto = iProductConvect.convertToDTOs(product);
        return productDetailDto ;
    }

    @Override
    public PagingProductResponse findAll(int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<Product> productPage = iProductPagingRepository.findAll(pageRequest);
        List<Product> productList = productPage.getContent();
        List<ProductDto> productDtoList = new ArrayList<>();
        for (Product product : productList){
            ProductDto productDto = iProductConvect.convertToDTO(product);
            productDtoList.add(productDto);
        }
        PagingProductResponseDto response = PagingProductResponseDto.builder()
                .totalPages(productPage.getTotalPages())
                .totalElements(productPage.getTotalElements())
                .pageNumber(productPage.getNumber())
                .size(productPage.getSize())
                .data(productDtoList)
                .build();
        return PagingProductResponse.builder()
                .data(response)
                .message("Get movie page number " + pageNumber + " success")
                .statusCode(HttpStatus.OK.value())
                .build();
    }
}
