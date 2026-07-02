package com.panda.merge.dto;

import com.panda.merge.dto.scores.MatchScoresBetterDto;
import com.panda.merge.model.MatchScoresInfo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Data
public class MatchScoresDto {


    private String matchId;

    private Boolean isStandard;
    private Integer matchStatus;
    /**
     * 是否被关注 true 被关注 需要详情
     * */
    private Boolean attention;
    /**
     * 数据源
     * */
    private String dataSourceCode;

    /**比赛比分*/
    private CommonItem  matchScores;

    /**半场比分*/
    private CommonItem  periodScores;
    /**当前阶段*/
    private Long periodNow;
    /**阶段时间*/
    private Long remainingTime;
    /**比赛进行时间*/
    private Long secondsMatchStart;
    /**是否暂停 ：1 正常  0  暂停 */
    private Integer isTimeGo;
    /**当前系统时间 */
    private Long nowSystemTime;
    /**事件当前时间*/
    private Long eventTime;

    private String dataSourceType;

    /**
     * "比赛是否暂停"
     */
    private  boolean whetherStop;

    private Map<String,Object> scoresJson;

    private List<String> allDataSourceCode;

    //时间
    private String thirdMatchId;

    private Integer currentRound;

    //当前盘数
    private Integer currentSet;

    private Integer matchLength;

    private Map<String,CommonItem> allScore;

    public MatchScoresDto(){}
    public MatchScoresDto(MatchScoresInfo matchScoresInfo) {
        this.setDataSourceCode(matchScoresInfo.getDataSourceCode());
        this.setMatchLength(matchScoresInfo.getMatchLength());
        this.setRemainingTime(matchScoresInfo.getRemainingTime());
        this.setSecondsMatchStart(matchScoresInfo.getSecondsMatchStart());
        this.setPeriodNow(matchScoresInfo.getPeriod());
        CommonItem wholeScores= new CommonItem();
        wholeScores.setHome(matchScoresInfo.getT1());
        wholeScores.setAway(matchScoresInfo.getT2());
        CommonItem periodScores= new CommonItem();
        periodScores.setHome(matchScoresInfo.getPeriodT1());
        periodScores.setAway(matchScoresInfo.getPeriodT2());
        dataSourceType=matchScoresInfo.getDataSourceType();
        this.setMatchScores(wholeScores);
        this.setPeriodScores(periodScores);

    }

    public MatchScoresDto(MatchScoresBetterDto matchScoresBetterDto) {
        this.matchId= matchScoresBetterDto.getMatchId();
        this.setMatchStatus(matchScoresBetterDto.getMatchStatus());
        this.setDataSourceCode(matchScoresBetterDto.getDataSourceCode());
        this.setMatchLength(matchScoresBetterDto.getMatchLength());
        this.setRemainingTime(matchScoresBetterDto.getRemainingTime());
        this.setSecondsMatchStart(matchScoresBetterDto.getSecondsMatchStart());
        this.setPeriodNow(matchScoresBetterDto.getPeriodNow());
        CommonItem wholeScores= new CommonItem();
        wholeScores.setHome(matchScoresBetterDto.getT1());
        wholeScores.setAway(matchScoresBetterDto.getT2());
        CommonItem periodScores= new CommonItem();
        periodScores.setHome(matchScoresBetterDto.getPeriodT1());
        periodScores.setAway(matchScoresBetterDto.getPeriodT2());
        dataSourceType=matchScoresBetterDto.getDataSourceType();
        this.setMatchScores(wholeScores);
        this.setPeriodScores(periodScores);
        this.setIsTimeGo(matchScoresBetterDto.getIsTimeGo());
        this.setPeriodNow(matchScoresBetterDto.getPeriodNow());
        this.setSecondsMatchStart(matchScoresBetterDto.getSecondsMatchStart());
        this.setRemainingTime(matchScoresBetterDto.getRemainingTime());
        this.setNowSystemTime(System.currentTimeMillis());
        this.setEventTime(matchScoresBetterDto.getEventTime());
        this.setCurrentRound(matchScoresBetterDto.getCurrentRound());
        this.setCurrentSet(matchScoresBetterDto.getCurrentSet());
    }
}
