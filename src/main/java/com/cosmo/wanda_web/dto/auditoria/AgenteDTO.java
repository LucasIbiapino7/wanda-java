package com.cosmo.wanda_web.dto.auditoria;

import com.cosmo.wanda_web.projections.auditoria.AgenteSummaryProjection;

public class AgenteDTO {
    private String agente;
    private Long total;
    private Double percentual;
    private Long likes;
    private Long dislikes;

    public AgenteDTO() {
    }

    public AgenteDTO(AgenteSummaryProjection projection, Long totalGeral) {
        this.agente = projection.getAssistantStyle();
        this.total = projection.getTotal();
        this.percentual = totalGeral > 0 ? Math.round((projection.getTotal() * 100.0 / totalGeral) * 10.0) / 10.0: 0.0;
        this.likes = projection.getLikes() != null ? projection.getLikes() : 0L;
        this.dislikes = projection.getDislikes() != null ? projection.getDislikes() : 0L;
    }

    public String getAgente() {
        return agente;
    }

    public void setAgente(String agente) {
        this.agente = agente;
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

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public Long getDislikes() {
        return dislikes;
    }

    public void setDislikes(Long dislikes) {
        this.dislikes = dislikes;
    }
}
