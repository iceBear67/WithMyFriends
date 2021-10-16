package io.ib67.serverutil.modules.basic.tpamodule;

import java.util.UUID;

public class Request {
    private final UUID requestId;
    private final Request.Type requestType;
    private final UUID requester;
    private final UUID invitee;
    private final long createdTimeStamp;

    public Request(UUID requestId, Type requestType, UUID requester, UUID invitee, long createTimeStamp) {
        this.requestId = requestId;
        this.requestType = requestType;
        this.requester = requester;
        this.invitee = invitee;
        this.createdTimeStamp = createTimeStamp;
    }

    public Request(Type requestType, UUID requester, UUID invitee) {
        this.requestId = UUID.randomUUID();
        this.requestType = requestType;
        this.requester = requester;
        this.invitee = invitee;
        this.createdTimeStamp = System.currentTimeMillis();
    }

    public UUID getRequestId() {
        return requestId;
    }

    public Type getRequestType() {
        return requestType;
    }

    public UUID getRequester() {
        return requester;
    }

    public UUID getInvitee() {
        return invitee;
    }

    public long getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestId=" + requestId +
                ", requestType=" + requestType +
                ", requester=" + requester +
                ", invitee=" + invitee +
                ", createdTimeStamp=" + createdTimeStamp +
                '}';
    }



    enum Type {
        TPA("tpa"),
        TPAHERE("tpahere");

        private final String name;

        Type(String name) {
            this.name = name;
        }
    }
}
