package cl.duoc.notas_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration",
		"spring.flyway.enabled=false",
		"eureka.client.enabled=false",
		"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect",
		"spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect"
})
class NotasServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
