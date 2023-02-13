package hr.fer.ruazosa.audionotes.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public List<AudioNotes> savedRecordings(String username) {
        List<User> users = userRepository.findUserByUserName(username);

        if (users.isEmpty()) {
            return null;
        }
        User user = users.get(0);

        return user.getNotes();
    }

    @Override
    public String addRecording(String username, Path storedLocation, MultipartFile file, String description) {
        List<User> users = userRepository.findUserByUserName(username);
        User user = users.get(0);

        AudioNotes note = new AudioNotes();
        note.setPath(storedLocation.toString());
        note.setName(file.getOriginalFilename());
        note.setDescription(description);
        note.setSize(file.getSize());
        user.addAudioNote(note);
        audioNotesRepository.save(note);

        List<AudioNotes> notes = savedRecordings(username);
        for(AudioNotes audio : notes){
            if(audio.getPath().equals(note.getPath())){
                return audio.getId();
            }
        }

        return "error";
    }

    @Override
    public void removeRecording(String username, String fileId) {
        List<User> users = userRepository.findUserByUserName(username);
        User user = users.get(0);

        List<AudioNotes> notes = user.getNotes();
        AudioNotes noteToDelete = null;
        for(AudioNotes note : notes){
            if(note.getId().equals(fileId)){
                noteToDelete = note;
            }
        }
        notes.remove(noteToDelete);
        audioNotesRepository.delete(noteToDelete);
    }

    @Override
    public AudioNotes findRecording(String username, String fileId) {
        List<AudioNotes> notes = savedRecordings(username);
        for(AudioNotes note : notes){
            if(note.getId().equals(fileId)){
                return note;
            }
        }
        return null;
    }
}
