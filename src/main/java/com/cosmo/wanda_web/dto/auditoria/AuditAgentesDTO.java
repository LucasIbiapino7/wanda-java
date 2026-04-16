package com.cosmo.wanda_web.dto.auditoria;

import java.util.List;

public class AuditAgentesDTO {
    private Long totalInteracoes;
    private Long totalAlunosAtivos;
    private Double mediaInteracoesPorAluno;
    private List<AgenteDTO> porAgente;
    private List<JogoDTO> porJogo;
    private List<FuncaoDTO> porFuncao;
    private List<InteractionTypeDTO> porInteractionType;

    public AuditAgentesDTO() {

    }

    public AuditAgentesDTO(Long totalInteracoes, Long totalAlunosAtivos,
                           List<AgenteDTO> porAgente, List<JogoDTO> porJogo,
                           List<FuncaoDTO> porFuncao, List<InteractionTypeDTO> porInteractionType) {
        this.totalInteracoes = totalInteracoes;
        this.totalAlunosAtivos = totalAlunosAtivos;
        this.mediaInteracoesPorAluno = totalAlunosAtivos > 0
                ? Math.round((totalInteracoes * 10.0 / totalAlunosAtivos)) / 10.0 : 0.0;
        this.porAgente = porAgente;
        this.porJogo = porJogo;
        this.porFuncao = porFuncao;
        this.porInteractionType = porInteractionType;
    }

    public Long getTotalInteracoes() {
        return totalInteracoes;
    }

    public void setTotalInteracoes(Long totalInteracoes) {
        this.totalInteracoes = totalInteracoes;
    }

    public Long getTotalAlunosAtivos() {
        return totalAlunosAtivos;
    }

    public void setTotalAlunosAtivos(Long totalAlunosAtivos) {
        this.totalAlunosAtivos = totalAlunosAtivos;
    }

    public Double getMediaInteracoesPorAluno() {
        return mediaInteracoesPorAluno;
    }

    public void setMediaInteracoesPorAluno(Double mediaInteracoesPorAluno) {
        this.mediaInteracoesPorAluno = mediaInteracoesPorAluno;
    }

    public List<AgenteDTO> getPorAgente() {
        return porAgente;
    }

    public void setPorAgente(List<AgenteDTO> porAgente) {
        this.porAgente = porAgente;
    }

    public List<JogoDTO> getPorJogo() {
        return porJogo;
    }

    public void setPorJogo(List<JogoDTO> porJogo) {
        this.porJogo = porJogo;
    }

    public List<FuncaoDTO> getPorFuncao() {
        return porFuncao;
    }

    public void setPorFuncao(List<FuncaoDTO> porFuncao) {
        this.porFuncao = porFuncao;
    }

    public List<InteractionTypeDTO> getPorInteractionType() {
        return porInteractionType;
    }

    public void setPorInteractionType(List<InteractionTypeDTO> porInteractionType) {
        this.porInteractionType = porInteractionType;
    }
}
