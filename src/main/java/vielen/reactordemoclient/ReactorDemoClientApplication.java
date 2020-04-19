package vielen.reactordemoclient;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import vielen.reactordemoclient.domain.Movie;
import vielen.reactordemoclient.domain.MovieEvent;

@SpringBootApplication
public class ReactorDemoClientApplication {

	@Bean
	public WebClient client() {
		return WebClient.create();
	}

	@Bean
	public CommandLineRunner demo(WebClient client) {
		return args -> {
			client.get()
					.uri("http://localhost:8080/movies")
					.exchange()
					.flatMapMany(clientResponse -> clientResponse.bodyToFlux(Movie.class))
					.filter(movie -> movie.getName().equalsIgnoreCase("The night of Spring"))
					.subscribe(movie -> client.get().uri("http://localhost:8080/movies/{id}/events", movie.getId())
							.exchange()
							.flatMapMany(clientResponse -> clientResponse.bodyToFlux(MovieEvent.class))
							.subscribe(System.out::println));
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(ReactorDemoClientApplication.class, args);
	}

}

