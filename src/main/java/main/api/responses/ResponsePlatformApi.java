package main.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponsePlatformApi<T> {
    private String message;
    private Long timestamp;
    private Integer total;
    private Integer offset;
    private Integer perPage;
    private T data;
    private String message_description;

    public ResponsePlatformApi(String message, Integer total, Integer offset, Integer perPage, T data) {
        this.message = message;
        this.total = total;
        this.offset = offset;
        this.perPage = perPage;
        this.data = data;
        timestamp = new Date().getTime();
    }
}
