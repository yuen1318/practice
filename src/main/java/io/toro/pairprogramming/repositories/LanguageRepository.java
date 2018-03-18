package io.toro.pairprogramming.repositories;

import io.toro.pairprogramming.models.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {

    Language findByNameIgnoreCase(String name);

}
