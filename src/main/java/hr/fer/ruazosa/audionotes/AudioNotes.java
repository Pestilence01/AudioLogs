package hr.fer.ruazosa.audionotes;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name="audio_notes")
public class AudioNotes {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "audio_notes_id")
    private Long id;

    @NotBlank(message = "Path on disk cannot be unknown")
    @Column(name = "path_on_disc")
    private String path;

    @Column(name = "note_name")
    private String name;

    @Column(name = "note_description")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
