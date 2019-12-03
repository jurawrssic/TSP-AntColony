package tsp.antcolony;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/** *
 * @author Julia
 */
public class Exe {
    private int nFormigas;
    private int nCidades;
    private int posicao;
    private int iteracoes = 1;
    int vencedora;
    
    private double tamMelhorCaminho;
    private double nTrilhas = 1.0;
    private double feromonio = 1;
    private double feromonioTotal = 500;
    private double prioridade = 5;
    private double evaporacao = 0.5;
    private double form = 10;
    private double random = 0.01;
    
    private int ordemCaminhos[];
    private double prob[];
    private double cidades[][];    
    private double caminhos[][];    
    private double mAux[][];
    
    Random rand = new Random();
    DecimalFormat db = new DecimalFormat("#0.00");
    DecimalFormat db2 = new DecimalFormat("#0.0000");
    private List<Formiga> formigas = new ArrayList<>();
    
    public Exe(int nCidades){
        cidades = gerarMatriz(nCidades);        
        this.nCidades = cidades.length;
        nFormigas = (int)(this.nCidades * form);
        caminhos = new double[this.nCidades][this.nCidades];
        prob = new double[this.nCidades];
        for(int i=0; i<nFormigas; i++){
            formigas.add(new Formiga(this.nCidades));
        }
        System.out.println("\tMatriz de distâncias entre cidades:");
        for(int i=0; i<nCidades; i++){
            for(int j=0; j<nCidades; j++) {
                System.out.print(" \t" + db.format(cidades[i][j]) + " ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("Número de cidades: " + nCidades);
        System.out.println("Número de formigas: " + nFormigas);
    }
    
    public void TSP(int geracoes){
        System.out.println("Número de gerações: " + geracoes);
        for(int i=0; i<geracoes; i++){
            System.out.println();
            System.out.println("GERAÇÃO #" + i);
            System.out.println();
            iniciar();
        }
        System.out.println();
        System.out.println("Melhor caminho: " + tamMelhorCaminho);
        for(int i=0; i<nCidades; i++){
            System.out.print(" ->" + ordemCaminhos[i] + " ");
        }
        System.out.println();
    }
    
    public int[] iniciar(){
        iniciarFormigas();
        resetCaminhos();
        int i = 0;
        while(i < iteracoes){
            moverFormigas();
            atualizarFeromonio();
            melhorSolucao();
            int j = 1;
            for(Formiga f: formigas){
                System.out.print("Caminho formiga #" + j);
                for(int k=0; k<nCidades; k++){
                    System.out.print(" ->"+f.caminho[k] + " ");
                }
                System.out.print(" \t" + f.tamanhoCaminho(cidades));
                System.out.println();
                j++;
            }
            i++;
        }
        return ordemCaminhos.clone();
    }

    private void iniciarFormigas(){
        for(int i=0; i<nFormigas; i++){
            formigas.forEach(f ->{
                f.reset();
                f.visitarCidade(-1, rand.nextInt(nCidades));                
            });
        }
        posicao = 0;
    }
    
    private void moverFormigas(){
        for(; posicao<nCidades-1;posicao++){
            for(Formiga f : formigas) {
                f.visitarCidade(posicao, proxCidade(f));
            }
        }
    }
    
    private int proxCidade(Formiga f) throws RuntimeException{
        int c = rand.nextInt(nCidades - posicao);
        if(rand.nextDouble() < random){
            if(!f.checarVisitado(c)) {
                return c;
            }
        }
        
        calcProb(f);
        double r = rand.nextDouble();
        double total = 0;
        for(int i=0; i<nCidades; i++){
            total += prob[i];
            if(total >=r){
                return i;
            }
        }
        throw new RuntimeException("Sem cidades para ir");
    }
    
    private double[][] gerarMatriz(int nCidades) {
        double min = 0.00;
        double max = 20.01;
        double r;
        
        cidades = new double[nCidades][nCidades];
        for(int i=0; i<nCidades; i++){
            for(int j=0; j<nCidades; j++){
                cidades[i][j] = 0.00;
            }
        }
        
        for(int i=0; i<nCidades; i++){
            for(int j=0; j<nCidades; j++){
                if(i==j){
                    cidades[i][j] = 0.00;
                }else{                    
                    r = ThreadLocalRandom.current().nextDouble(min, max);
                    cidades[i][j] = r;
                    cidades[j][i] = r;
                }                
            }
        }
        return cidades;
    }
    
    public void atualizarFeromonio(){
        for(int i=0; i<nCidades; i++){
            for(int j=0; j<nCidades; j++){
                caminhos[i][j] *= evaporacao;
            }
        }
        for(Formiga f: formigas){
            double adicionar = feromonioTotal / f.tamanhoCaminho(cidades);
            int k, j;
            for(int i=0; i>nCidades-1; i++){
                k = f.caminho[i];
                j = f.caminho[i+1];
                caminhos[j][k] += adicionar;
            }
            k = f.caminho[nCidades-1];
            j = f.caminho[0];            
            caminhos[k][j] += adicionar;
        }
    }
    
    private void melhorSolucao(){
        if(ordemCaminhos == null){
            ordemCaminhos = formigas.get(0).caminho;
            tamMelhorCaminho = formigas.get(0).tamanhoCaminho(cidades);
        }
        
        for(Formiga f: formigas){
            if(f.tamanhoCaminho(cidades) < tamMelhorCaminho){
                tamMelhorCaminho = f.tamanhoCaminho(cidades);
                ordemCaminhos = f.caminho.clone();
            }
            
        }
        
    }
    
    private void resetCaminhos(){
        for(int i=0; i<nCidades; i++){
            for(int j=0; j<nCidades; j++){
                caminhos[i][j] = nTrilhas;
            }
        }
    }
    
    public void calcProb(Formiga f){
        int i = f.caminho[posicao];
        double p = 0.00;
        for (int l = 0; l < nCidades; l++) {
            if (!f.checarVisitado(l)) {
                p += Math.pow(caminhos[i][l], feromonio) * Math.pow(1.0 / cidades[i][l], prioridade);
            }
        }
        for (int j = 0; j < nCidades; j++) {
            if (f.checarVisitado(j)) {
                prob[j] = 0.0;
            } else {
                double m = Math.pow(caminhos[i][j], feromonio) * Math.pow(1.0 / cidades[i][j], prioridade);
                prob[j] = m / p;
            }
        }
    }
}