package com.atimera.api.repositories;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoryRepositoryTest {
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @BeforeAll
//    public void setUp() {
//        Category category1 = new Category(null, "Smartphone");
//        Category category2 = new Category(null, "Computers");
//        categoryRepository.save(category1);
//        categoryRepository.save(category2);
//        assertNotNull(category1.getId());
//        assertNotNull(category2.getId());
//    }
//
//    @Test
//    void dataTest() {
//        List<Category> categories = categoryRepository.findAll();
//        assertTrue( categories.size() >= 2);
//        assertTrue(categories.stream().anyMatch((category -> category.getName().equals("Computers"))));
//        Category category = categories.stream().filter(c -> c.getName().equals("Computers")).findFirst().get();
//        category.setName("Ordinateurs");
//        category = categoryRepository.save(category);
//        Category category1 = categoryRepository.findById(category.getId()).get();
//        assertEquals("Ordinateurs", category1.getName());
//    }
//
//    @AfterAll
//    public void tearDown() {
//        //categoryRepository.deleteAll();
//    }

}