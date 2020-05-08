package com.viettel.backend.dto.report;

import java.io.Serializable;

public class VisitResultDto implements Serializable {

    private static final long serialVersionUID = 2868471630896589265L;

    private int nbVisit;
    private int nbVisitPlanned;
    private int nbVisitErrorDuration;
    private int nbVisitErrorPosition;

    private int nbDistributor;
    private int nbSalesman;
    private int nbCustomer;
    
    public VisitResultDto() {
        this(0, 0);
    }

    public VisitResultDto(int nbVisit, int nbVisitPlanned) {
        this(nbVisit, nbVisitPlanned, 0, 0, 0, 0, 0);
    }

    public VisitResultDto(int nbVisit, int nbVisitPlanned, int nbVisitErrorDuration, int nbVisitErrorPosition,
            int nbDistributor, int nbSalesman, int nbCustomer) {
        super();

        this.nbVisit = nbVisit;
        this.nbVisitPlanned = nbVisitPlanned;
        this.nbVisitErrorDuration = nbVisitErrorDuration;
        this.nbVisitErrorPosition = nbVisitErrorPosition;
        this.nbDistributor = nbDistributor;
        this.nbSalesman = nbSalesman;
        this.nbCustomer = nbCustomer;
    }

    public int getNbVisit() {
        return nbVisit;
    }

    public void setNbVisit(int nbVisit) {
        this.nbVisit = nbVisit;
    }

    public int getNbVisitPlanned() {
        return nbVisitPlanned;
    }

    public void setNbVisitPlanned(int nbVisitPlanned) {
        this.nbVisitPlanned = nbVisitPlanned;
    }

    public int getNbVisitErrorDuration() {
        return nbVisitErrorDuration;
    }

    public void setNbVisitErrorDuration(int nbVisitErrorDuration) {
        this.nbVisitErrorDuration = nbVisitErrorDuration;
    }

    public int getNbVisitErrorPosition() {
        return nbVisitErrorPosition;
    }

    public void setNbVisitErrorPosition(int nbVisitErrorPosition) {
        this.nbVisitErrorPosition = nbVisitErrorPosition;
    }

    public int getNbDistributor() {
        return nbDistributor;
    }

    public void setNbDistributor(int nbDistributor) {
        this.nbDistributor = nbDistributor;
    }

    public int getNbSalesman() {
        return nbSalesman;
    }

    public void setNbSalesman(int nbSalesman) {
        this.nbSalesman = nbSalesman;
    }

    public int getNbCustomer() {
        return nbCustomer;
    }

    public void setNbCustomer(int nbCustomer) {
        this.nbCustomer = nbCustomer;
    }

}
