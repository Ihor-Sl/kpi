package ua.mctv32.kpi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.mctv32.kpi.domain.User;
import ua.mctv32.kpi.exception.NotFoundException;
import ua.mctv32.kpi.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
