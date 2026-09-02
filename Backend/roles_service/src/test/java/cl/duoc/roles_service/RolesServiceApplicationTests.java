package cl.duoc.roles_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration",
		"spring.flyway.enabled=false",
		"eureka.client.enabled=false"
})
class RolesServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
