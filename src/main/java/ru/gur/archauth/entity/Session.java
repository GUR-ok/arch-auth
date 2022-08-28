package ru.gur.archauth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("session")
public class Session implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String jwt;
}
