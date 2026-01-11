package com.cosmo.wanda_web.dto.bits;

public class MatchDecisionBitsDTO {

    private Integer officialWinner;

    private String decidedBy;

    public MatchDecisionBitsDTO() {}

    public MatchDecisionBitsDTO(Integer officialWinner, String decidedBy) {
        this.officialWinner = officialWinner;
        this.decidedBy = decidedBy;
    }

    public Integer getOfficialWinner() {
        return officialWinner;
    }

    public void setOfficialWinner(Integer officialWinner) {
        this.officialWinner = officialWinner;
    }

    public String getDecidedBy() {
        return decidedBy;
    }

    public void setDecidedBy(String decidedBy) {
        this.decidedBy = decidedBy;
    }
}
