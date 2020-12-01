package main.services;

import lombok.extern.slf4j.Slf4j;
import main.api.requests.ChangeGlobalSettingsRequest;
import main.api.responses.ChangeGlobalSettingsResponse;
import main.api.responses.SettingsResponse;
import main.model.GlobalSetting;
import main.model.User;
import main.model.repositories.GlobalSettingsRepository;
import main.model.repositories.UsersRepository;
import main.api.mappers.SettingsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class GlobalSettingsService {

    private final GlobalSettingsRepository globalSettingsRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public GlobalSettingsService(GlobalSettingsRepository globalSettingsRepository,
                                 UsersRepository usersRepository) {
        this.globalSettingsRepository = globalSettingsRepository;
        this.usersRepository = usersRepository;
    }

    public ResponseEntity<SettingsResponse> getGlobalSettings() {
        List<GlobalSetting> allGlobalSettings = globalSettingsRepository.findAll();
        SettingsResponse settingsResponse = new SettingsMapper().settingsToSettingsResponse(allGlobalSettings);
        return new ResponseEntity<>(settingsResponse, HttpStatus.OK);
    }

    public GlobalSetting getGlobalSettingForAPIByName(String nameOfGlobalSetting) {
        return globalSettingsRepository.findGlobalSettingByName(nameOfGlobalSetting);
    }

    public ResponseEntity<ChangeGlobalSettingsResponse> changeGlobalSettings(ChangeGlobalSettingsRequest changeGlobalSettingsRequest, String userEmail) {

        ChangeGlobalSettingsResponse changeGlobalSettingsResponse = checkUser(userEmail);

        if(!changeGlobalSettingsResponse.isResult()) {
            return new ResponseEntity<>(changeGlobalSettingsResponse, HttpStatus.OK);
        }

        globalSettingsRepository.deleteAll();

        GlobalSetting multiUserMode = new GlobalSetting();
        multiUserMode.setCode("1");
        multiUserMode.setName("MULTIUSER_MODE");
        multiUserMode.setValue(String.valueOf(changeGlobalSettingsRequest.isMultiUserMode()));

        GlobalSetting postPreModeration = new GlobalSetting();
        postPreModeration.setCode("2");
        postPreModeration.setName("POST_PREMODERATION");
        postPreModeration.setValue(String.valueOf(changeGlobalSettingsRequest.isPostPreModeration()));

        GlobalSetting statisticsIfPublic = new GlobalSetting();
        statisticsIfPublic.setCode("3");
        statisticsIfPublic.setName("STATISTICS_IS_PUBLIC");
        statisticsIfPublic.setValue(String.valueOf(changeGlobalSettingsRequest.isStatisticIsPublic()));

        globalSettingsRepository.save(multiUserMode);
        globalSettingsRepository.save(postPreModeration);
        globalSettingsRepository.save(statisticsIfPublic);

        return new ResponseEntity<>(changeGlobalSettingsResponse,HttpStatus.OK);
    }

    private ChangeGlobalSettingsResponse checkUser(String userEmail) {
        User user = usersRepository.findByEmail(userEmail);
        ChangeGlobalSettingsResponse changeGlobalSettingsResponse = new ChangeGlobalSettingsResponse();
        changeGlobalSettingsResponse.setResult(true);
        if(user == null)
        {
            changeGlobalSettingsResponse.setResult(false);
            HashMap<String, String> errors = new HashMap<>();
            errors.put("пользователь", "Пользователь не найден!");
            changeGlobalSettingsResponse.setErrors(errors);
            return changeGlobalSettingsResponse;
        }

        if(user.getIsModerator() == 0) {
            changeGlobalSettingsResponse.setResult(false);
            HashMap<String, String> errors = new HashMap<>();
            errors.put("пользователь", "Пользователь не является модератором!");
            changeGlobalSettingsResponse.setErrors(errors);
            return changeGlobalSettingsResponse;
        }

        return changeGlobalSettingsResponse;
    }
}
