package com.cosmo.wanda_web.utils;

public class ParametersBits {
    private int bit8;
    private int bit16;
    private int bit32;
    private int firewall;
    private String oppLast;

    public ParametersBits(int bit8, int bit16, int bit32, int firewall, String oppLast) {
        this.bit8 = bit8;
        this.bit16 = bit16;
        this.bit32 = bit32;
        this.firewall = firewall;
        this.oppLast = oppLast;
    }

    public ParametersBits() {
    }

    public int getBit8() {
        return bit8;
    }

    public void setBit8(int bit8) {
        this.bit8 = bit8;
    }

    public int getBit16() {
        return bit16;
    }

    public void setBit16(int bit16) {
        this.bit16 = bit16;
    }

    public int getBit32() {
        return bit32;
    }

    public void setBit32(int bit32) {
        this.bit32 = bit32;
    }

    public int getFirewall() {
        return firewall;
    }

    public void setFirewall(int firewall) {
        this.firewall = firewall;
    }

    public String getOppLast() {
        return oppLast;
    }

    public void setOppLast(String oppLast) {
        this.oppLast = oppLast;
    }
}
