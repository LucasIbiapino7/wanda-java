package com.cosmo.wanda_web.dto.auditoria;

import com.cosmo.wanda_web.projections.auditoria.FuncaoSummaryProjection;

public class FuncaoDTO {
    private String funcao;
    private Long total;
    private Double percentual;

    public FuncaoDTO() {}

    public FuncaoDTO(FuncaoSummaryProjection projection, Long totalGeral) {
        this.funcao = projection.getFunctionName();
        this.total = projection.getTotal();
        this.percentual = totalGeral > 0 ? Math.round((projection.getTotal() * 100.0 / totalGeral) * 10.0) / 10.0 : 0.0;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
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
