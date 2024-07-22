package ansarbektassov.service;

import ansarbektassov.dto.UserDTO;
import ansarbektassov.model.User;
import ansarbektassov.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    public User registerUser(UserDTO userDTO) {
        User u = modelMapper.map(userDTO,User.class);
        u.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return userRepository.save(u);
    }
}
