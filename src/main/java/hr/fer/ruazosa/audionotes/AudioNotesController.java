package hr.fer.ruazosa.audionotes;


import hr.fer.ruazosa.audionotes.security.JwtResponse;
import hr.fer.ruazosa.audionotes.security.JwtUtils;
import hr.fer.ruazosa.audionotes.storage.AudioNotes;
import hr.fer.ruazosa.audionotes.storage.IAudioBackendService;
import hr.fer.ruazosa.audionotes.storage.StorageService;
import hr.fer.ruazosa.audionotes.storage.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class AudioNotesController {

    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private IAudioBackendService audioNotesService;

    @Autowired
    private JwtUtils jwtUtils;


    private final StorageService storageService;

    @Autowired
    public AudioNotesController(StorageService storageService) {
        this.storageService = storageService;
    }


    @PostMapping("/registerUser")
    public ResponseEntity<Object> registerUser(@RequestBody User user) {
        // validation
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        Map<String, Object> body = new LinkedHashMap<>();
        for (ConstraintViolation<User> violation : violations) {
            body.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        factory.close();
        if (!body.isEmpty()) {
            return new ResponseEntity<>(body, HttpStatus.NOT_ACCEPTABLE);
        }

        if(!audioNotesService.checkUsernameUnique(user)){
            return new ResponseEntity<>(user, HttpStatus.CONFLICT);
        }

        audioNotesService.registerUser(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }



    @PostMapping("/authenticate")
    public ResponseEntity<Object> createAuthenticationToken(@RequestBody User user) throws Exception {

        authenticate(user.getUsername(), user.getPassword());

        UserDetails userDetails = audioNotesService.loadUserByUsername(user.getUsername());
        final String token = jwtUtils.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    public static String getBearerTokenHeader() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization").split("\\s+")[1];
    }

    @GetMapping("/files")
    public ResponseEntity<List<ResponseFile>> listUploadedFiles(){
        String username = jwtUtils.getUsernameFromToken(getBearerTokenHeader());
        List<ResponseFile> files = audioNotesService.savedRecordings(username).stream().map(note -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("files/")
                    .path(note.getId())
                    .toUriString();

            return new ResponseFile(
                    note.getName(),
                    note.getDescription(),
                    fileDownloadUri,
                    note.getSize()
            );
                }).collect(Collectors.toList());
        return ResponseEntity.ok(files);
    }

    @GetMapping("/files/{fileId:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String fileId) {
        String username = jwtUtils.getUsernameFromToken(getBearerTokenHeader());
        AudioNotes note = audioNotesService.findRecording(username, fileId);
        if(Objects.isNull(note)){
            return ResponseEntity.notFound().build();
        } else{
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + note.getName() + "\"")
                    .body(storageService.loadAsResource(note.getPath()));
        }

    }

    @PostMapping("/upload")
    public ResponseEntity<Object> handleFileUpload(@RequestParam("file") MultipartFile file) {

        String username = jwtUtils.getUsernameFromToken(getBearerTokenHeader());

        Path destination = storageService.store(file);
        audioNotesService.addRecording(username, destination, file);

        return ResponseEntity.ok().body("You successfully uploaded " + file.getOriginalFilename() + "!");
    }

    @DeleteMapping("/files/{fileId:.+}")
    public ResponseEntity<Object> removeNote(@PathVariable String fileId){
        String username = jwtUtils.getUsernameFromToken(getBearerTokenHeader());
        AudioNotes note = audioNotesService.findRecording(username, fileId);

        storageService.delete(note.getPath());
        audioNotesService.removeRecording(username, fileId);
        return ResponseEntity.ok("deleted file " + note.getName());
    }


    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
