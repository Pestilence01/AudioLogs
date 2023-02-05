package hr.fer.ruazosa.audionotes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AudioNotesService implements IAudioBackendService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AudioNotesRepository audioNotesRepository;

    @Override
    public User registerUser(User user) {
        if(!checkUsernameUnique(user)){
            return null;
        }
        return userRepository.save(user);
    }

    @Override
    public boolean checkUsernameUnique(User user) {
        return Objects.isNull(loadUserByUsername(user.getUsername()));
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<User> users = userRepository.findUserByUserName(username);

        if (users.isEmpty()) {
            return null;
        }
        User user = users.get(0);
        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
        return userDetails;
    }

    @Override
    public List<String> savedRecordings(String username) {
        List<User> users = userRepository.findUserByUserName(username);

        if (users.isEmpty()) {
            return null;
        }
        User user = users.get(0);

        return user.getNotes().stream().map(AudioNotes::getName).collect(Collectors.toList());
    }

    @Override
    public void addRecording(String username, Path storedLocation, MultipartFile file) {
        List<User> users = userRepository.findUserByUserName(username);
        User user = users.get(0);

        AudioNotes note = new AudioNotes();
        note.setPath(storedLocation.toString());
        note.setName(file.getName());
        note.setDescription(file.getResource().getDescription());
        user.addAudioNote(note);
    }

    @Override
    public void removeRecording(String username, String storedLocation) {
        List<User> users = userRepository.findUserByUserName(username);
        User user = users.get(0);

        List<AudioNotes> notes = user.getNotes();
        notes.removeIf(note -> note.getPath().equals(storedLocation));
    }
}
