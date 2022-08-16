package ru.gur.archauth.web.healthcheck.response;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Health {

    HealthStatus status;

    @AllArgsConstructor
    public enum HealthStatus {

        OK("OK"),
        DOWN("DOWN");

        private final String value;

        @JsonValue
        public String getValue() {
            return this.value;
        }

        public String toString() {
            return String.valueOf(value);
        }
    }
}


