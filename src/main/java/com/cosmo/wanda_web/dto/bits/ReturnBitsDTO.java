package com.cosmo.wanda_web.dto.bits;

public class ReturnBitsDTO {
    private ReturnsObjectsDTO p1;
    private ReturnsObjectsDTO p2;

    public ReturnBitsDTO() {
        p1 = new ReturnsObjectsDTO();
        p2 = new ReturnsObjectsDTO();
    }

    public ReturnBitsDTO(ReturnsObjectsDTO p1, ReturnsObjectsDTO p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public ReturnsObjectsDTO getP1() {
        return p1;
    }

    public void setP1(ReturnsObjectsDTO p1) {
        this.p1 = p1;
    }

    public ReturnsObjectsDTO getP2() {
        return p2;
    }

    public void setP2(ReturnsObjectsDTO p2) {
        this.p2 = p2;
    }
}
