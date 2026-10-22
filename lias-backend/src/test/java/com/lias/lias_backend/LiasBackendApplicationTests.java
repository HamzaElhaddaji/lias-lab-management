package com.lias.lias_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:context;NON_KEYWORDS=YEAR,CONDITION,READ;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop", "app.seed.enabled=false", "app.mail.enabled=false"
})
class LiasBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
