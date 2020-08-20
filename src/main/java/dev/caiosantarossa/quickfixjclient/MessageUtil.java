package dev.caiosantarossa.quickfixjclient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quickfix.DataDictionaryProvider;
import quickfix.FixVersions;
import quickfix.LogUtil;
import quickfix.MessageUtils;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.field.ApplVerID;
import quickfix.field.ClOrdID;
import quickfix.field.OrdType;
import quickfix.field.OrigClOrdID;
import quickfix.field.QtyType;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TransactTime;
import quickfix.fix44.Message;
import quickfix.fix44.NewOrderSingle;
import quickfix.fix44.OrderCancelRequest;
import quickfix.fix44.OrderStatusRequest;

import java.io.IOException;
import java.time.LocalDateTime;

public class MessageUtil {

    private static Logger LOGGER = LogManager.getLogger(MessageUtil.class);

    private MessageUtil() {
    }

    public static void sendNewOrderSingle(SessionID sessionID) {
        NewOrderSingle newOrderSingle = new NewOrderSingle();
        newOrderSingle.set(new Side(Side.BUY));
        newOrderSingle.set(new ClOrdID("123"));
        newOrderSingle.set(new TransactTime(LocalDateTime.now()));
        newOrderSingle.set(new QtyType(1));
        newOrderSingle.set(new OrdType(OrdType.LIMIT));
        newOrderSingle.set(new Symbol("R"));

        sendMessage(sessionID, newOrderSingle);
    }

    public static void sendOrderStatusRequest(SessionID sessionID) {
        OrderStatusRequest orderStatusRequest = new OrderStatusRequest();
        orderStatusRequest.set(new Side(Side.BUY));
        orderStatusRequest.set(new ClOrdID("123"));
        orderStatusRequest.set(new Symbol("R"));

        sendMessage(sessionID, orderStatusRequest);
    }

    public static void sendOrderCancelRequest(SessionID sessionID) {
        OrderCancelRequest orderCancelRequest = new OrderCancelRequest();
        orderCancelRequest.setField(new OrigClOrdID("123"));
        orderCancelRequest.setField(new ClOrdID("321"));
        orderCancelRequest.setField(new Symbol("R"));
        orderCancelRequest.setField(new Side(Side.BUY));
        orderCancelRequest.set(new TransactTime(LocalDateTime.now()));


        sendMessage(sessionID, orderCancelRequest);
    }

    public static void resetSession(SessionID sessionID) {
        try {
            Session session = Session.lookupSession(sessionID);
            if (session == null) {
                throw new SessionNotFound(sessionID.toString());
            }

            session.reset();
        } catch (SessionNotFound | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static void sendMessage(SessionID sessionID, Message message) {
        try {
            Session session = Session.lookupSession(sessionID);
            if (session == null) {
                throw new SessionNotFound(sessionID.toString());
            }

            DataDictionaryProvider dataDictionaryProvider = session.getDataDictionaryProvider();
            if (dataDictionaryProvider != null) {
                try {
                    dataDictionaryProvider.getApplicationDataDictionary(
                            getApplVerID(session, message)).validate(message, true);
                } catch (Exception e) {
                    LogUtil.logThrowable(sessionID, "Outgoing message failed validation: "
                            + e.getMessage(), e);
                    return;
                }
            }

            final boolean messageSent = session.send(message);

            LOGGER.info("Message sent successfully: " + messageSent);

        } catch (SessionNotFound e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static ApplVerID getApplVerID(Session session, Message message) {
        String beginString = session.getSessionID().getBeginString();
        if (FixVersions.BEGINSTRING_FIXT11.equals(beginString)) {
            return new ApplVerID(ApplVerID.FIX50);
        } else {
            return MessageUtils.toApplVerID(beginString);
        }
    }

}
