package com.productinventory.product_inventory;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Use in-memory database for tests to avoid MySQL configuration
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ProductInventoryApplicationTests {

    @Test
    void contextLoads() {
    }

}
