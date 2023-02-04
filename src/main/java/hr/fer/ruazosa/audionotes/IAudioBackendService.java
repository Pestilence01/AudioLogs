package hr.fer.ruazosa.audionotes;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.nio.channels.MulticastChannel;
import java.nio.file.Path;
import java.util.List;

public interface IAudioBackendService extends UserDetailsService {
    User registerUser(User user);
    boolean checkUsernameUnique(User user);

    List<String> savedRecordings(String username);

    void addRecording(String username, Path storedLocation, MultipartFile file);

}
