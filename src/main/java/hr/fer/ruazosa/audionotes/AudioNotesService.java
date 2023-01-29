package hr.fer.ruazosa.audionotes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AudioNotesService implements IAudioBackendService {

    @Autowired
    private UserRepository userRepository;

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

    //TODO add store and retrieve functionality for audio logs i.e., BLOBS
}
