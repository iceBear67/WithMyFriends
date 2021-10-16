package io.ib67.serverutil.modules.basic.tpa;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class Request {
    private final UUID requestId;
    private final Request.Type requestType;
    private final UUID requester;
    private final UUID invitee;
    private final long createdTimeStamp;

    public Request(Type requestType, UUID requester, UUID invitee) {
        this.requestId = UUID.randomUUID();
        this.requestType = requestType;
        this.requester = requester;
        this.invitee = invitee;
        this.createdTimeStamp = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return super.toString(); //todo WIP
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
