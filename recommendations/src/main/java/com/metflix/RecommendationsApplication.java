package com.metflix;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
public class RecommendationsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecommendationsApplication.class, args);
    }

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    RequestDumperFilter requestDumperFilter() {
        return new RequestDumperFilter();
    }
}

@RestController
@RequestMapping("/api/recommendations")
class RecommendationsController {
    List<Movie> kidRecommendations = Arrays.asList(new Movie("lion king"), new Movie("frozen"));
    List<Movie> adultRecommendations = Arrays.asList(new Movie("shawshank redemption"), new Movie("spring"));
    List<Movie> familyRecommendations = Arrays.asList(new Movie("hook"), new Movie("the sandlot"));

    @Autowired
    RestTemplate restTemplate;
    @Value("${member.api:http://localhost:4444}")
    URI memberApi;

    @RequestMapping("/{user}")
    @HystrixCommand(fallbackMethod = "recommendationFallback",
            ignoreExceptions = UserNotFoundException.class,
            commandProperties = @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000"))
    public List<Movie> findRecommendationsForUser(@PathVariable String user) throws UserNotFoundException {
        Member member = restTemplate.exchange(
                RequestEntity.get(UriComponentsBuilder.fromUri(memberApi)
                        .pathSegment("api", "members", user)
                        .build().toUri()).build(),
                Member.class).getBody();
        if (member == null)
            throw new UserNotFoundException();
        return member.age < 17 ? kidRecommendations : adultRecommendations;
    }

    /**
     * Should be safe for all audiences
     */
    List<Movie> recommendationFallback(String user) {
        return familyRecommendations;
    }
}


class Movie {
    public String title;

    Movie(String title) {
        this.title = title;
    }

    public Movie() {
    }
}


class Member {
    public String user;
    public Integer age;
}

class UserNotFoundException extends Exception {
}