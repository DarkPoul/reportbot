package esvar.ua.botreport.session;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {

    private final Map<Long, UserSession> sessions = new ConcurrentHashMap<>();

    public UserSession getOrCreate(long userId) {
        return sessions.computeIfAbsent(userId, id -> new UserSession());
    }

    public UserSession reset(long userId) {
        UserSession session = new UserSession();
        sessions.put(userId, session);
        return session;
    }

    public void remove(long userId) {
        sessions.remove(userId);
    }
}
