package hr.fer.ruazosa.audionotes;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface IAudioBackendService extends UserDetailsService {
    User registerUser(User user);
    boolean checkUsernameUnique(User user);
}
