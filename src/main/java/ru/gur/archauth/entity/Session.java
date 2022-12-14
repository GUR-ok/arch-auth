package ru.gur.archauth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("session")
public class Session implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String jwt;

    @TimeToLive
    private Long timeout;
}
