package com.cosmo.wanda_web.dto.classroom;

public class DashboardOverviewDTO {

    private Long totalAlunos;
    private Long totalSubmeteram;
    private Long totalAtivos;
    private Long totalInteracoes;

    public DashboardOverviewDTO(Long totalAlunos, Long totalSubmeteram, Long totalAtivos, Long totalInteracoes) {
        this.totalAlunos = totalAlunos;
        this.totalSubmeteram = totalSubmeteram;
        this.totalAtivos = totalAtivos;
        this.totalInteracoes = totalInteracoes;
    }

    public Long getTotalAlunos() {
        return totalAlunos;
    }

    public void setTotalAlunos(Long totalAlunos) {
        this.totalAlunos = totalAlunos;
    }

    public Long getTotalSubmeteram() {
        return totalSubmeteram;
    }

    public void setTotalSubmeteram(Long totalSubmeteram) {
        this.totalSubmeteram = totalSubmeteram;
    }

    public Long getTotalAtivos() {
        return totalAtivos;
    }

    public void setTotalAtivos(Long totalAtivos) {
        this.totalAtivos = totalAtivos;
    }

    public Long getTotalInteracoes() {
        return totalInteracoes;
    }

    public void setTotalInteracoes(Long totalInteracoes) {
        this.totalInteracoes = totalInteracoes;
    }
}