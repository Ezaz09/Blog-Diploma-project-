package main.model.repositories;

import main.model.GlobalSettings;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GlobalSettingsRepository extends JpaRepository<GlobalSettings, Integer> {
}
