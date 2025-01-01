package com.FindersKeepers.backend;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "spring.liquibase.enabled=false")
class FindersKeepersApplicationTests {

}
