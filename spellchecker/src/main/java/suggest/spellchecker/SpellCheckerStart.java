package suggest.spellchecker;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpellCheckerStart extends SpellCheckerServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(SpellCheckerStart.class, args);

	}

}
