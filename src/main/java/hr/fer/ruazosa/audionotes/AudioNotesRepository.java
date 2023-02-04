package hr.fer.ruazosa.audionotes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudioNotesRepository extends JpaRepository<AudioNotes, Integer> {
}
