package dev.caiosantarossa.quickfixjclient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quickfix.Application;
import quickfix.Message;
import quickfix.SessionID;

public class FixApplication implements Application {

    private static final Logger LOGGER = LogManager.getLogger(FixApplication.class);

    @Override
    public void onCreate(SessionID sessionId) {
        LOGGER.info("onCreate: SessionId={}", sessionId);
    }

    @Override
    public void onLogon(SessionID sessionId) {
        LOGGER.info("onLogon: SessionID={}", sessionId);
    }

    @Override
    public void onLogout(SessionID sessionId) {
        LOGGER.info("onLogout: SessionID={}", sessionId);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {
        LOGGER.info("toAdmin: Message={}, SessionId={}", message, sessionId);
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId) {
        LOGGER.info("fromAdmin: Message={}, SessionId={}", message, sessionId);
    }

    @Override
    public void toApp(Message message, SessionID sessionId) {
        LOGGER.info("toApp: Message={}, SessionId={}", message, sessionId);
    }

    @Override
    public void fromApp(Message message, SessionID sessionId) {
        LOGGER.info("fromApp: Message={}, SessionId={}", message, sessionId);
    }

}
