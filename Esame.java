// GIUSEPPE MUSCHETTA, corso A, prof. Ferrari

package com.company;

public class Esame {

    /*
        Overview: Immutable type that represents the result of a student exam
        Abstraction Function:
                              ("nomeEsame" , "matricola" , voto) --> Idea of an Exam
                                     implementor's view               users's view

        Representation Invariant:
                                  nomeEsame != null && matricola != null && 0 <= voto <= 30
     */

    private String nomeEsame;
    private String matricola;
    private int voto;

    public Esame(String nome, String matricola, int voto){
        if(nome == null || matricola == null)
            throw new NullPointerException("Exam name or badge number must not be null ");
        if(!(voto >= 0 && voto <= 30))
            throw new IllegalArgumentException("vote must be in this range [0,30]");

        this.nomeEsame = nome;
        this.matricola = matricola;
        this.voto = voto;
    }

    public String getNomeEsame() {
        return nomeEsame;
    }

    public String getMatricola() {
        return matricola;
    }

    public int getVoto() {
        return voto;
    }

    @Override
    public String toString(){
        return this.nomeEsame;// +" sostenuto dalla matricola "+matricola+" con voto "+voto+"/30";
    }

    @Override
    public boolean equals(Object o) {
        if(o == null)
            throw new NullPointerException();
        if (o instanceof Esame) {
            Esame oo = (Esame) o;
            return this.nomeEsame.equals(oo.nomeEsame) && this.matricola.equals(oo.matricola)
                && this.voto == oo.voto;
        }
        else
            return false;
    }
}
