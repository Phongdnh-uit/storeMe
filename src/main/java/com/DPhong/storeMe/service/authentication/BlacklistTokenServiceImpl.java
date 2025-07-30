package com.DPhong.storeMe.service.authentication;

import com.DPhong.storeMe.constant.RedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BlacklistTokenServiceImpl implements BlacklistTokenService {

  private final RedisTemplate<String, Object> redisTemplate;

  @Override
  public void addToBlacklist(String token) {
    redisTemplate.opsForValue().set(RedisKey.BLACKLIST_TOKEN + token, true, 3600);
  }

  @Override
  public boolean isBlacklisted(String token) {
    Boolean isBlacklisted = redisTemplate.hasKey(RedisKey.BLACKLIST_TOKEN + token);
    return Boolean.TRUE.equals(isBlacklisted);
  }

  @Override
  public void removeFromBlacklist(String token) {
    if (isBlacklisted(token)) {
      redisTemplate.delete(RedisKey.BLACKLIST_TOKEN + token);
    }
  }
}
