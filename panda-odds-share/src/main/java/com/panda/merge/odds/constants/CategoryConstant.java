package com.panda.merge.odds.constants;

import com.panda.merge.odds.enums.MarketScoreTypeEnum;

import java.util.*;

/**
 * CategoryConstant
 *
 *
 * @description:
 * @date: 3/21/2025
 *
 * <table>
 *     <thead>
 *         <tr>
 *             <th>足球-玩法</th>
 *             <th>常规进球</th>
 *             <th>常规角球</th>
 *             <th>常规罚牌</th>
 *             <th>加时进球</th>
 *             <th>加时角球</th>
 *             <th>加时罚牌</th>
 *             <th>点球大战</th>
 *         </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <td>独赢</td>
 *             <td>1</td>
 *             <td>111</td>
 *             <td>310</td>
 *             <td>126</td>
 *             <td>1100413</td>
 *             <td>1100405</td>
 *             <td>333</td>
 *         </tr>
 *         <tr>
 *             <td>让球</td>
 *             <td>4</td>
 *             <td>113</td>
 *             <td>306</td>
 *             <td>128</td>
 *             <td>1100414</td>
 *             <td>1100406</td>
 *             <td>334</td>
 *         </tr>
 *         <tr>
 *             <td>大小</td>
 *             <td>2</td>
 *             <td>114</td>
 *             <td>307</td>
 *             <td>127</td>
 *             <td>331</td>
 *             <td>1100407</td>
 *             <td>335</td>
 *         </tr>
 *         <tr>
 *             <td>半场独赢</td>
 *             <td>17</td>
 *             <td>119</td>
 *             <td>311</td>
 *             <td>129</td>
 *             <td>1100415</td>
 *             <td>1100408</td>
 *             <td>-</td>
 *         </tr>
 *         <tr>
 *             <td>半场让球</td>
 *             <td>19</td>
 *             <td>121</td>
 *             <td>308</td>
 *             <td>130</td>
 *             <td>1100416</td>
 *             <td>1100409</td>
 *             <td>-</td>
 *         </tr>
 *         <tr>
 *             <td>半场大小</td>
 *             <td>18</td>
 *             <td>122</td>
 *             <td>309</td>
 *             <td>332</td>
 *             <td>1100417</td>
 *             <td>1100410</td>
 *             <td>-</td>
 *         </tr>
 *     </tbody>
 * </table>
 *
 **/
public final class CategoryConstant {

    // 常规进球 (Regular Goals) column
    public static final Set<Long> REGULAR_GOAL_SET = new HashSet<>(Arrays.asList(1L,   // 独赢 (Win)
                                                                                 4L,   // 让球 (Handicap)
                                                                                 2L   // 大小 (Over/Under)
                                                                                ));



    // 常规半场进球 (Regular Goals) column
    public static final Set<Long> REGULAR_HT_GOAL_SET = new HashSet<>(Arrays.asList(17L,  // 半场独赢 (Half-time Win)
                                                                                    19L,  // 半场让球 (Half-time Handicap)
                                                                                    18L   // 半场大小 (Half-time Over/Under)
                                                                                   ));

    // 常规角球 (Regular Corners) column
    public static final Set<Long> REGULAR_CORNER_SET = new HashSet<>(Arrays.asList(111L, // 独赢
                                                                                   113L, // 让球
                                                                                   114L)); // 大小

    // 常规角球 (Regular Corners) column
    public static final Set<Long> REGULAR_HT_CORNER_SET = new HashSet<>(Arrays.asList(119L, // 半场独赢
                                                                                   121L, // 半场让球
                                                                                   122L  // 半场大小
                                                                                  ));

    // 常规罚牌 (Regular Cards) column
    public static final Set<Long> REGULAR_CARD_SET = new HashSet<>(Arrays.asList(310L, // 独赢
                                                                                 306L, // 让球
                                                                                 307L));// 大小

    // 常规半场罚牌 (Regular Cards) column
    public static final Set<Long> REGULAR_HT_CARD_SET = new HashSet<>(Arrays.asList(
                                                                                 311L, // 半场独赢
                                                                                 308L, // 半场让球
                                                                                 309L  // 半场大小
                                                                                ));

    // 加时进球 (Overtime Goals) column
    public static final Set<Long> OVERTIME_GOAL_SET = new HashSet<>(Arrays.asList(126L, // 独赢
                                                                                  128L, // 让球
                                                                                  127L)); // 大小

    // 加时半场进球 (Overtime Goals) column
    public static final Set<Long> OVERTIME_HT_GOAL_SET = new HashSet<>(Arrays.asList(
                                                                                  129L, // 半场独赢
                                                                                  130L, // 半场让球
                                                                                  332L  // 半场大小
                                                                                 ));

    // 加时角球 (Overtime Corners) column
    public static final Set<Long> OVERTIME_CORNER_SET = new HashSet<>(Arrays.asList(1100413L, // 独赢
                                                                                    1100414L, // 让球
                                                                                    331L));     // 大小

    public static final Set<Long> OVERTIME_HT_CORNER_SET = new HashSet<>(Arrays.asList(

            1100415L, // 半场独赢
            1100416L, // 半场让球
            1100417L  // 半场大小
                                                                                      ));

    // 加时罚牌 (Overtime Cards) column
    public static final Set<Long> OVERTIME_CARD_SET = new HashSet<>(Arrays.asList(1100405L, // 独赢
                                                                                  1100406L, // 让球
                                                                                  1100407L)); // 大小

    public static final Set<Long> OVERTIME_HT_CARD_SET = new HashSet<>(Arrays.asList(1100408L, // 半场独赢
                                                                                  1100409L, // 半场让球
                                                                                  1100410L  // 半场大小
                                                                                 ));

    public static final  Set<Long> OVER_UNDER_SET = new HashSet<>(Arrays.asList(
            //全场
            2L, 114L, 307L, 127L, 331L, 1100407L,
            // 半场
            18L, 122L, 309L, 332L, 1100417L, 1100410L));

    public static final Set<Long> HANDICAP_SET = new HashSet<>(Arrays.asList(
            // 全场
            4L, 113L, 306L, 128L, 1100414L, 1100406L,
            // 半场
            19L, 121L, 308L, 130L, 1100416L, 1100409L));

    public static final Map<Long, MarketScoreTypeEnum> CATEGORY_SCORE_TYPE_MAP = new HashMap<>();

    static {
        REGULAR_GOAL_SET.forEach(categoryId -> CATEGORY_SCORE_TYPE_MAP.put(categoryId, MarketScoreTypeEnum.GOAL));
        REGULAR_HT_GOAL_SET.forEach(categoryId -> CATEGORY_SCORE_TYPE_MAP.put(categoryId, MarketScoreTypeEnum.HT_GOAL));
        REGULAR_CORNER_SET.forEach(categoryId -> CATEGORY_SCORE_TYPE_MAP.put(categoryId, MarketScoreTypeEnum.CORNER));
        REGULAR_HT_CORNER_SET.forEach(categoryId -> CATEGORY_SCORE_TYPE_MAP.put(categoryId,
                                                                                MarketScoreTypeEnum.HT_CORNER));
        REGULAR_CARD_SET.forEach(categoryId -> CATEGORY_SCORE_TYPE_MAP.put(categoryId, MarketScoreTypeEnum.CARD));
        REGULAR_HT_CARD_SET.forEach(categoryId -> CATEGORY_SCORE_TYPE_MAP.put(categoryId, MarketScoreTypeEnum.HT_CARD));

        OVERTIME_GOAL_SET.forEach(categoryId -> CATEGORY_SCORE_TYPE_MAP.put(categoryId, MarketScoreTypeEnum.OVERTIME_GOAL));
        OVERTIME_HT_GOAL_SET.forEach(categoryId -> CATEGORY_SCORE_TYPE_MAP.put(categoryId,
                                                                               MarketScoreTypeEnum.OVERTIME_HT_GOAL));
        OVERTIME_CORNER_SET.forEach(categoryId -> CATEGORY_SCORE_TYPE_MAP.put(categoryId, MarketScoreTypeEnum.OVERTIME_CORNER));
        OVERTIME_HT_CORNER_SET.forEach(categoryId -> CATEGORY_SCORE_TYPE_MAP.put(categoryId,
                                                                                 MarketScoreTypeEnum.OVERTIME_HT_CORNER));
        OVERTIME_CARD_SET.forEach(categoryId -> CATEGORY_SCORE_TYPE_MAP.put(categoryId, MarketScoreTypeEnum.OVERTIME_CARD));
        OVERTIME_HT_CARD_SET.forEach(categoryId -> CATEGORY_SCORE_TYPE_MAP.put(categoryId,
                                                                               MarketScoreTypeEnum.OVERTIME_HT_CARD));

    }

    public static final Set<Long> AO_CATEGORY_CHECK_ODDSVALUE = new HashSet<>(Arrays.asList(

            2L, // 全场大小
            18L // 半场大小
    ));
}
