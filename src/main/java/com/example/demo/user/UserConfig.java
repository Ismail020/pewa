// package com.example.demo.user;

// import java.util.List;

// import org.springframework.boot.CommandLineRunner;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// public class UserConfig {

//     @Bean
//     CommandLineRunner commandLineRunner(UserRepository repository) {
//         return args -> {
//             User ismail = new User(
//                     "Ismail Çetin",
//                     "ismail@gmail.com",
//                     "123456",
//                     "Amsterdam",
//                     "https://avatars.githubusercontent.com/u/77468756?v=4");

//             User alex = new User(
//                     "Alex",
//                     "alex@gmail.com",
//                     "789101",
//                     "Rotterdam",
//                     "https://avatars.githubusercontent.com/u/77468756?v=4");

//             repository.saveAll(
//                     List.of(ismail, alex));
//         };
//     }
// }
