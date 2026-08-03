package com.panda.merge.dto.message;


import lombok.Data;


@Data
public class MarketSuspendMessage {

    private String linkId;
    private MessageData data;
    private Long dataSourceTime;
    private String dataSourceCode;
    private Boolean isReissue;
    private Boolean spareMq;


    @Data
    public static class MessageData{
        private Long matchId;
        private Long sportId;
    }

}
