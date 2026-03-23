package com.cosmo.wanda_web.dto.auditoria;

import com.cosmo.wanda_web.projections.auditoria.JogoSummaryProjection;

public class JogoDTO {
    private String jogo;
    private Long total;
    private Double percentual;

    public JogoDTO() {}

    public JogoDTO(JogoSummaryProjection projection, Long totalGeral) {
        this.jogo = projection.getGameName();
        this.total = projection.getTotal();
        this.percentual = totalGeral > 0 ? Math.round((projection.getTotal() * 100.0 / totalGeral) * 10.0) / 10.0 : 0.0;
    }

    public String getJogo() {
        return jogo;
    }

    public void setJogo(String jogo) {
        this.jogo = jogo;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Double getPercentual() {
        return percentual;
    }

    public void setPercentual(Double percentual) {
        this.percentual = percentual;
    }
}