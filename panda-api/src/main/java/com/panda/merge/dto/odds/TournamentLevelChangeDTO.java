package com.panda.merge.dto.odds;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TournamentLevelChangeDTO implements Serializable {


    private List<Long> matchIds;

}
