package hr.fer.ruazosa.audionotes.storage;

import hr.fer.ruazosa.audionotes.storage.AudioNotes;
import hr.fer.ruazosa.audionotes.storage.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

public interface IAudioBackendService extends UserDetailsService {
    User registerUser(User user);
    boolean checkUsernameUnique(User user);

    List<AudioNotes> savedRecordings(String username);

    String addRecording(String username, Path storedLocation, MultipartFile file, String description);

    void removeRecording(String username, String fileId);

    AudioNotes findRecording(String username, String fileId);

}
