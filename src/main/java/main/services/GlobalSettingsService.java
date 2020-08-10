package main.services;

import lombok.extern.slf4j.Slf4j;
import main.model.GlobalSettings;
import main.model.repositories.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class GlobalSettingsService {

    private final GlobalSettingsRepository globalSettingsRepository;

    @Autowired
    public GlobalSettingsService(GlobalSettingsRepository globalSettingsRepository) {
        this.globalSettingsRepository = globalSettingsRepository;
    }

    public ResponseEntity<List<GlobalSettings>> getGlobalSettings()
    {
        List<GlobalSettings> allGlobalSettings = globalSettingsRepository.findAll();
        return new ResponseEntity<>(allGlobalSettings, HttpStatus.OK);
    }
}
