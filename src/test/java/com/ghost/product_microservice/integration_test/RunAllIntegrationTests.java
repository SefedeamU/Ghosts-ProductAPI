package com.ghost.product_microservice.integration_test;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.ghost.product_microservice.integration_test.product_controller.ProductControllerPublicTest;
import com.ghost.product_microservice.integration_test.subcategory_controller.SubcategoryControllerEdgeCasesTest;
import com.ghost.product_microservice.integration_test.subcategory_controller.SubcategoryControllerTest;
import com.ghost.product_microservice.integration_test.product_controller.ProductControllerAdminTest;
import com.ghost.product_microservice.integration_test.product_controller.ProductControllerEdgeCasesTest;
import com.ghost.product_microservice.integration_test.category_controller.CategoryControllerEdgeCasesTest;
import com.ghost.product_microservice.integration_test.category_controller.CategoryControllerTest;

@Suite
@SelectClasses({
    ProductControllerPublicTest.class,
    ProductControllerAdminTest.class,
    ProductControllerEdgeCasesTest.class,
    CategoryControllerTest.class,
    CategoryControllerEdgeCasesTest.class,
    SubcategoryControllerTest.class,
    SubcategoryControllerEdgeCasesTest.class
})
public class RunAllIntegrationTests {}