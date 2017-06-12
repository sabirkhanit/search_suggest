package suggest.spellchecker.configuration;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
@PropertySource({ "classpath:application.properties" , "classpath:spellchecker.properties"})
public class SpellCheckerConfig {
	
	
	/* START - Beans to enable JSR - 349 ( bean validation 1.1 ) for method arguments in Controllers */
	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		MethodValidationPostProcessor mvProcessor = new MethodValidationPostProcessor();
		mvProcessor.setValidator(validator());
		return mvProcessor;
	}
	
	@Bean
	public LocalValidatorFactoryBean validator(){
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.setProviderClass(HibernateValidator.class);
		validator.afterPropertiesSet();
		return validator;
	}
	
	/* End : JSR - 349 */

}
