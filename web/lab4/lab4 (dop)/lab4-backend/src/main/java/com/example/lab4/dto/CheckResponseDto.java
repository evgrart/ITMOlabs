package com.example.lab4.dto;

import java.io.Serializable;
import java.util.List;

public class CheckResponseDto implements Serializable {
    private boolean inside;
    private List<ProofStepDto> proof;
    private boolean smtVerified; 

    public boolean isInside() { return inside; }
    public void setInside(boolean inside) { this.inside = inside; }
    public List<ProofStepDto> getProof() { return proof; }
    public void setProof(List<ProofStepDto> proof) { this.proof = proof; }
    public boolean isSmtVerified() { return smtVerified; }
    public void setSmtVerified(boolean smtVerified) { this.smtVerified = smtVerified; }
}