package inmobiliaria.es.uclm.negocio;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest; // IMPORTANTE
import org.springframework.context.ApplicationContext;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest // Esta anotación soluciona el error de "No beans found"
class ContextIntegrationTest {

	@Autowired
	private ApplicationContext context;

	@Test
	void contextLoads() {
		// Esta es la aserción que pedía SonarQube
		assertThat(context).isNotNull();
	}
}