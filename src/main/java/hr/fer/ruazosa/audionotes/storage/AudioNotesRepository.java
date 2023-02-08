package hr.fer.ruazosa.audionotes.storage;

import hr.fer.ruazosa.audionotes.storage.AudioNotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudioNotesRepository extends JpaRepository<AudioNotes, String> {
}
