package main.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeGlobalSettingsRequest {
    @JsonProperty("MULTIUSER_MODE")
    private boolean multiUserMode;
    @JsonProperty("POST_PREMODERATION")
    private boolean postPreModeration;
    @JsonProperty("STATISTICS_IS_PUBLIC")
    private boolean statisticIsPublic;
}
