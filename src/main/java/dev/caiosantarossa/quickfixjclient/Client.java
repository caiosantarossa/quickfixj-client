package dev.caiosantarossa.quickfixjclient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quickfix.Application;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.Initiator;
import quickfix.LogFactory;
import quickfix.MessageStoreFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.fix44.MessageFactory;

import java.util.concurrent.TimeUnit;

public class Client {

    private static final Logger LOGGER = LogManager.getLogger(Client.class);

    public static void main(String args[]) throws Exception {

        LOGGER.info("Starting client...");

        Application application = new FixApplication();

        SessionSettings settings = new SessionSettings(Client.class.getClassLoader().getResourceAsStream("client.cfg"));
        MessageStoreFactory storeFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new FileLogFactory(settings);
        MessageFactory messageFactory = new MessageFactory();

        Initiator initiator = new SocketInitiator(application, storeFactory, settings, logFactory, messageFactory);
        initiator.start();

        SessionID sessionID = initiator.getSessions().get(0);

        MessageUtil.resetSession(sessionID);

        TimeUnit.SECONDS.sleep(2);

        MessageUtil.sendNewOrderSingle(sessionID);
        MessageUtil.sendOrderStatusRequest(sessionID);
        MessageUtil.sendOrderCancelRequest(sessionID);

        TimeUnit.SECONDS.sleep(5);

        initiator.stop();

        LOGGER.info("Starting stopped");
    }

}
