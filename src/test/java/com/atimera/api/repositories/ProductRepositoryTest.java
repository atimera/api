package com.atimera.api.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductRepositoryTest {
//
//    @Autowired
//    private ProductRepository productRepository;
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @BeforeAll
//    public void setUp() {
//        Category category1 = new Category(null, "Smartphone");
//        Category category2 = new Category(null, "Tech");
//
//        Product p1 = new Product(null, "HP P12", 260.0, 1, null);
//        //p1.getCategories().addAll(List.of(category1, category2));
//        //List<Category> categories = p1.getCategories();
//        //p1.getCategories().add(category1);
//        //p1.getCategories().add(category2);
//        //categoryRepository.save(category1);
//        //categoryRepository.save(category2);
//
//        Product p2 = new Product(null, "Samsung A1", 200.0, 2, null);
//        //p2.getCategories().add(category1);
//
//        Product added1 = productRepository.save(p1);
//        Product added2 = productRepository.save(p2);
//        assertNotNull(p1.getId());
//        assertNotNull(p2.getId());
//        //assertNotNull(added1.getCategories());
//        //assertEquals(2, added1.getCategories().size());
//    }
//
//    //@Test
//    void dataTest() {
//        List<Product> products = productRepository.findAll();
//        assertTrue( products.size() >= 2);
//        assertTrue(products.stream().anyMatch((p -> p.getName().equals("HP P12"))));
//        Product product = products.stream().filter(c -> c.getName().equals("HP P12")).findFirst().get();
//        product.setName("HP P13");
//        product.setPrice(600.0);
//        //product.getCategories().clear();
//        product.setQuantity(2);
//        product = productRepository.save(product);
//        Product updatedProduct = productRepository.findById(product.getId()).get();
//        assertEquals("HP P13", updatedProduct.getName());
//        assertEquals(649, updatedProduct.getPrice());
//        assertEquals(2, updatedProduct.getQuantity());
//        //assertFalse( updatedProduct.getCategories().isEmpty());
//    }
//
//    @AfterAll
//    public void tearDown() {
//        //productRepository.deleteAll();
//    }

}
