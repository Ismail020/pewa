// package com.example.demo.user;

// import java.util.List;
// import java.util.Optional;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import jakarta.transaction.Transactional;

// @Service
// public class UserService {

//     private final UserRepository userRepository;

//     @Autowired
//     public UserService(UserRepository userRepository) {
//         this.userRepository = userRepository;
//     }

//     public List<User> getUsers() {
//         return userRepository.findAll();
//     }

//     public void addNewUser(User user) {
//         Optional<User> studentOptional = userRepository.findUserByEmail(user.getEmail());

//         if (studentOptional.isPresent()) {
//             throw new IllegalStateException("This email is already taken");
//         }

//         userRepository.save(user);
//     }

//     public void deleteUser(Long userId) {
//         boolean exists = userRepository.existsById(userId);

//         if (!exists) {
//             throw new IllegalStateException("User with id " + userId + " does not exist");
//         }

//         userRepository.deleteById(userId);
//     }

//     @Transactional
//     public void updateUser(Long userId, User updatedUser) {
//         User existingUser = userRepository.findById(userId)
//                 .orElseThrow(() -> new IllegalStateException(
//                         "User with id " + userId + " does not exist"));

//         if (updatedUser.getName() != null &&
//                 updatedUser.getName().length() > 0 &&
//                 !updatedUser.getName().equals(existingUser.getName())) {
//             existingUser.setName(updatedUser.getName());
//         }

//         if (updatedUser.getEmail() != null &&
//                 updatedUser.getEmail().length() > 0 &&
//                 !updatedUser.getEmail().equals(existingUser.getEmail())) {
//             Optional<User> userOptional = userRepository.findUserByEmail(updatedUser.getEmail());
//             if (userOptional.isPresent()) {
//                 throw new IllegalStateException("Email is already taken");
//             }
//             existingUser.setEmail(updatedUser.getEmail());
//         }

//         if (updatedUser.getPassword() != null &&
//                 updatedUser.getPassword().length() > 0 &&
//                 !updatedUser.getPassword().equals(existingUser.getPassword())) {
//             existingUser.setPassword(updatedUser.getPassword());
//         }

//         if (updatedUser.getLocation() != null &&
//                 updatedUser.getLocation().length() > 0 &&
//                 !updatedUser.getLocation().equals(existingUser.getLocation())) {
//             existingUser.setLocation(updatedUser.getLocation());
//         }

//         if (updatedUser.getProfilePicture() != null &&
//                 updatedUser.getProfilePicture().length() > 0 &&
//                 !updatedUser.getProfilePicture().equals(existingUser.getProfilePicture())) {
//             existingUser.setProfilePicture(updatedUser.getProfilePicture());
//         }

//         userRepository.save(existingUser);
//     }
// }
