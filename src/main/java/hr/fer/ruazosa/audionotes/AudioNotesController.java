package hr.fer.ruazosa.audionotes;


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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
    }

    @GetMapping("/")
    public ResponseEntity<Object> listUploadedFiles(){
        String username = jwtUtils.getUsernameFromToken(getBearerTokenHeader());
        return ResponseEntity.ok(audioNotesService.savedRecordings(username));
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        String username = jwtUtils.getUsernameFromToken(getBearerTokenHeader());

        Path destination = storageService.store(file);
        audioNotesService.addRecording(username, destination, file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }


    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
