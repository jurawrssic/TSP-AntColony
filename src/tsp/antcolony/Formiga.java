package tsp.antcolony;
/**
 * @author Julia
 */
public class Formiga {
    public double tamCaminho;
    public int cidades;
    public int caminho[];
    public boolean visitados[];
    
    public Formiga(int cidades){
        this.tamCaminho = 0;
        this.cidades = cidades;
        this.caminho = new int[cidades];
        this.visitados = new boolean[cidades];
    }
    
    public void visitarCidade(int posicao, int cidade){
        caminho[posicao+1] = cidade;
        visitados[cidade] = true;
    }
    
    public boolean checarVisitado(int i){
        return visitados[i];
    }
    
    public double tamanhoCaminho(double m[][]){
        double tam = m[caminho[cidades - 1]][caminho[0]];
        for (int i = 0; i < cidades - 1; i++) {
            tam += m[caminho[i]][caminho[i + 1]];
        }
        return tam;
    }
    
    public void reset(){
        for (int i = 0; i < cidades; i++)
            visitados[i] = false;
    }
    
    public void somarCaminho(double cidades[][]){
        for(int i=0; i<caminho.length-1; i++){
            int de = caminho[i];
            int para = caminho[i+1];
            this.tamCaminho += cidades[de][para];
        }            
    }
}
