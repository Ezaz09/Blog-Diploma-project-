package main.services;

import lombok.extern.slf4j.Slf4j;
import main.api.responses.SettingsResponse;
import main.model.GlobalSetting;
import main.model.repositories.GlobalSettingsRepository;
import main.services.mappers.SettingsMapperImpl;
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

    public ResponseEntity<SettingsResponse> getGlobalSettings() {
        List<GlobalSetting> allGlobalSettings = globalSettingsRepository.findAll();
        SettingsResponse settingsResponse = new SettingsMapperImpl().settingsToSettingsResponse(allGlobalSettings);
        return new ResponseEntity<>(settingsResponse, HttpStatus.OK);
    }
}
