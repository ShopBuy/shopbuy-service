package com.codegym.shopbuyservice.service.impl;

import com.codegym.shopbuyservice.service.INameNormalizationService;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.regex.Pattern;
@Service
public class NameNormalizationServiceImpl implements INameNormalizationService {
    @Override
    public String normalizeName(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll(":", "").replaceAll("-", " ").toLowerCase();
    }
}
