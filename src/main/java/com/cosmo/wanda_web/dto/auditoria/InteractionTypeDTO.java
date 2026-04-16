package com.cosmo.wanda_web.dto.auditoria;

import com.cosmo.wanda_web.projections.auditoria.InteractionTypeSummaryProjection;

public class InteractionTypeDTO {
    private String interactionType;
    private Long total;
    private Double percentual;

    public InteractionTypeDTO() {}

    public InteractionTypeDTO(InteractionTypeSummaryProjection projection, Long totalGeral) {
        this.interactionType = projection.getInteractionType();
        this.total = projection.getTotal();
        this.percentual = totalGeral > 0
                ? Math.round((projection.getTotal() * 100.0 / totalGeral) * 10.0) / 10.0
                : 0.0;
    }

    public String getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(String interactionType) {
        this.interactionType = interactionType;
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
