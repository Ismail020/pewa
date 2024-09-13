package com.example.demo.appuser;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppUserConfig {

    @Bean
    CommandLineRunner commandLineRunner(AppUserRepository repository) {
        return args -> {
            AppUser ismail = new AppUser(
                    "Ismail Çetin",
                    "ismail@gmail.com",
                    "123456",
                    "Amsterdam",
                    "https://avatars.githubusercontent.com/u/77468756?v=4");

            AppUser alex = new AppUser(
                    "Alex",
                    "alex@gmail.com",
                    "789101",
                    "Rotterdam",
                    "https://avatars.githubusercontent.com/u/77468756?v=4");

            repository.saveAll(
                    List.of(ismail, alex));
        };
    }
}
