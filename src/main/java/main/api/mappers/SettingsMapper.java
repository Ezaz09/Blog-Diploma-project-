package main.api.mappers;

import main.api.responses.SettingsResponse;
import main.model.GlobalSetting;

import java.util.List;

public class SettingsMapper {
    public SettingsResponse settingsToSettingsResponse(List<GlobalSetting> settings) {
        if (settings == null) {
            return null;
        }

        SettingsResponse settingsResponse = new SettingsResponse();
        for (GlobalSetting settings1 : settings) {
            String settingName = settings1.getName();
            switch (settingName) {
                case "MULTIUSER_MODE":
                    settingsResponse.setMultiUserMode(Boolean.parseBoolean(settings1.getValue()));
                    break;
                case "POST_PREMODERATION":
                    settingsResponse.setPostPreModeration(Boolean.parseBoolean(settings1.getValue()));
                    break;
                case "STATISTICS_IS_PUBLIC":
                    settingsResponse.setStatisticIsPublic(Boolean.parseBoolean(settings1.getValue()));
                    break;
            }
        }

        return settingsResponse;
    }

}
