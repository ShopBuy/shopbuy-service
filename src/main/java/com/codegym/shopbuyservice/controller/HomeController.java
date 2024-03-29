package com.codegym.shopbuyservice.controller;

import com.codegym.shopbuyservice.dto.CategoryDto;
import com.codegym.shopbuyservice.dto.ProductDetailDto;
import com.codegym.shopbuyservice.dto.ProductDto;
import com.codegym.shopbuyservice.dto.payload.response.CategoryResponseDto;
import com.codegym.shopbuyservice.dto.payload.response.FindProductResponse;
import com.codegym.shopbuyservice.dto.payload.response.FindProductsReponse;
import com.codegym.shopbuyservice.dto.payload.response.ProductDetailResponseDto;
import com.codegym.shopbuyservice.service.ICategoryService;
import com.codegym.shopbuyservice.service.IProductService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(value = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private IProductService iProductService;
    @Autowired
    private ICategoryService categoryService;

    @GetMapping("/search")
    public ResponseEntity<?> getByName (@RequestParam(value = "name", required = true) String nameProduct, @RequestParam(value = "type", required = false,defaultValue = "all") String type){
        if ("all".equals(type)) {
            List<Optional<ProductDto>> product = iProductService.findProductByName(nameProduct);
            FindProductsReponse response;
            if (!product.isEmpty()) {
                response = FindProductsReponse.builder()
                        .data(product)
                        .statusCode(HttpStatus.OK.value())
                        .message("Success")
                        .build();
            } else {
                response = FindProductsReponse.builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message("Not found Movies")
                        .build();
            }
            return ResponseEntity.ok(response);
        } else {
            ProductDto productDto = iProductService.findByName(nameProduct);
            FindProductResponse response;
            if (productDto != null) {
                response = FindProductResponse.builder()
                        .data(productDto)
                        .statusCode(HttpStatus.OK.value())
                        .message("Success")
                        .build();
            } else {
                response = FindProductResponse.builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message("Not found Movie")
                        .build();
            }
            return ResponseEntity.ok(response);
        }
    }


    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductDetailResponseDto> getProductDetail(@PathVariable Long productId) {
        ProductDetailResponseDto response = new ProductDetailResponseDto();
        try {
            ProductDetailDto productDetailDto = iProductService.detailProduct(productId);
            response.setData(productDetailDto);
            response.setStatusCode(200);
            response.setMessage("The product was found successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatusCode(404);
            response.setMessage("Product does not exist.");
            return ResponseEntity.status(404).body(response);
        }
    }

    @GetMapping("/category/all")
    public ResponseEntity<?> getAllCategory(){
        CategoryResponseDto response = categoryService.fillAll();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}


