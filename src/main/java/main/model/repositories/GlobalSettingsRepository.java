package main.model.repositories;

import main.model.GlobalSetting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GlobalSettingsRepository extends JpaRepository<GlobalSetting, Integer> {
    @Query("From GlobalSetting as g where g.name = :name")
    GlobalSetting findGlobalSettingByName(@Param("name") String name);
}
