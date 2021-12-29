package buscaminas;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
public class TableroBuscaminas {
	
	private Casilla [][] casillas;
	private int numFilas,numColumnas,numMinas,numCasillasEspeciales;
	private int numCasillasAbiertas;
	private Consumer<ArrayList<Casilla>> eventoPartidaPerdida;
	private Consumer<Casilla> eventocasillaAbierta;
	private Consumer<Casilla> eventocasillaEspecial;
	private Consumer<ArrayList<Casilla>> eventoPartidaGanada;
	private Consumer<ArrayList<Casilla>> eventoVerMinasRandom;
	private Consumer<Casilla [][]> eventoGirarTablero;//pasar tablero con giro
	private Consumer<Casilla [][]> eventoMoverMinas;//pasar tablero con minas movidas

	
	public TableroBuscaminas(int filas,int columnas,int minas) {
		numFilas = filas;
		numColumnas = columnas;
		numMinas=minas;
		numCasillasEspeciales=numMinas/2;
		inicializarCasillas();
	}
	public void inicializarCasillas() {
		casillas = new Casilla[numFilas][numColumnas];
		for(int i=0;i<casillas.length;i++) {
			for(int j=0;j<casillas[0].length;j++) {
				casillas[i][j] = new Casilla(i,j);
			}
		}
		generarMinas();
		generarCasillasEspeciales();
	}
	public void generarMinas() {
		Random rand = new Random();
		int minasCreadas=0;
		while(minasCreadas<numMinas) {
			int fila = rand.nextInt(casillas.length);
			int col = rand.nextInt(casillas[0].length);
			if(!casillas[fila][col].esMina()) {
				casillas[fila][col].setMina(true);
				minasCreadas++;
			}
		}
		actualizarMinasAlrededor();
	}
	public void generarCasillasEspeciales() {
		Random rand = new Random();
		ArrayList<Casilla> casillasEspeciales = getCasillasRodeadasConMinas();
		int casillasEsp=0;
		while(casillasEsp!=numCasillasEspeciales) {
			int pos = rand.nextInt(casillasEspeciales.size());
			if(!casillasEspeciales.get(pos).esMina() && !casillasEspeciales.get(pos).esComodin()) {
				int eventoE = rand.nextInt(3)+1;
				casillasEspeciales.get(pos).setComodin(true);
				casillasEspeciales.get(pos).setEventoEspecial(eventoE);
				casillasEsp++;
			}
		}
		//System.out.println("con ningun 0 alrededor: "+casillasRodeadasConMinas.size());
	}
	public ArrayList<Casilla> getCasillasRodeadasConMinas(){//casillas != 0, rodeadas por cualquier numero menos el 0
		ArrayList<Casilla> casillasRodeadasConMinas = new ArrayList<Casilla>();
		for(int i=0;i<casillas.length;i++) {
			for(int j=0;j<casillas[0].length;j++) {
				if(casillas[i][j].getNumMinasAlrededor()==0) {
					continue;
				}
				ArrayList<Casilla> casillasAlrededor = getCasillasAlrededor(i,j);
				int cont=0;
				for(Casilla c: casillasAlrededor) {
					if(c.getNumMinasAlrededor()!=0) {
						cont++;
					}
				}
				if(cont==casillasAlrededor.size()) {
					casillasRodeadasConMinas.add(casillas[i][j]);
				}
			}
		}
		return casillasRodeadasConMinas;
	}
	public void actualizarMinasAlrededor() {
		for(int i=0;i<casillas.length;i++) {
			for(int j=0;j<casillas[0].length;j++) {
				if(casillas[i][j].esMina()) {
					ArrayList<Casilla> casillasAlrededor = getCasillasAlrededor(i, j);
					for(Casilla c: casillasAlrededor) {
						c.incremenetarMinasAlrededor();
					}
				}
			}
		}
	}
	public ArrayList<Casilla> getCasillasAlrededor(int fila,int col){
		ArrayList<Casilla> listaCasillas = new ArrayList<Casilla>();
		int tmpFila;
		int tmpColumna;
		for(int i=-1;i<2;i++) {
			tmpFila = fila+i;
			for(int j=-1;j<2;j++) {
				tmpColumna= col+j;
				if(tmpFila==fila && tmpColumna==col) {
					continue; 
				}
				if((tmpFila>=0 && tmpFila<casillas.length) && (tmpColumna>=0 && tmpColumna<casillas[0].length)) {
					listaCasillas.add(casillas[tmpFila][tmpColumna]);
				}
			}
		}
		return listaCasillas;
		
	}
	public ArrayList<Casilla> getCasillasConMina(){
		ArrayList<Casilla> casillasConMina = new ArrayList<Casilla>();
		for(int i=0;i<casillas.length;i++) {
			for(int j=0;j<casillas.length;j++) {
				if(casillas[i][j].esMina()) {
					casillasConMina.add(casillas[i][j]);
				}
			}
		}
		return casillasConMina;
	}
	public ArrayList<ArrayList<Casilla>> getMinas_SegunApertura(){
		ArrayList<ArrayList<Casilla>> minasSegunApertura = new ArrayList<ArrayList<Casilla>>();
		minasSegunApertura.add(new ArrayList<Casilla>());
		minasSegunApertura.add(new ArrayList<Casilla>());
		for(Casilla mina: getCasillasConMina()) {
			if(!mina.esMinaDescubierta()) {
				minasSegunApertura.get(0).add(mina);
			}
			else {
				minasSegunApertura.get(1).add(mina);
			}
		}
		return minasSegunApertura;
	}
	public void seleccionarCasilla(int fila,int col) {
		eventocasillaAbierta.accept(casillas[fila][col]);
		
		if(casillas[fila][col].esMina()) {
			ArrayList<Casilla> casillasConMina = getCasillasConMina();
			eventoPartidaPerdida.accept(casillasConMina);
		}
		else if(casillas[fila][col].esComodin()) {
			this.eventocasillaEspecial.accept(casillas[fila][col]);
			switch(casillas[fila][col].getEventoEspecial()) {
			case 1:
				ArrayList<Casilla> minasRandom = getMinasAleatorias();
				this.eventoVerMinasRandom.accept(minasRandom);
				System.out.println("Casillas Descubiertas (R): ");
				printTablero();
				System.out.println("/////");
				printPistas();
				break;
			case 2:
				this.girarTablero();
				this.eventoGirarTablero.accept(casillas);
				break;
			case 3:
				this.moverMinasEnTablero();
				this.eventoMoverMinas.accept(casillas);
			}
		}
		else if(casillas[fila][col].getNumMinasAlrededor()==0) {
			marcarCasilla(fila, col);//abrir casilla
			ArrayList<Casilla> casillasAlrededor = getCasillasAlrededor(fila, col);
			for(Casilla c: casillasAlrededor) {
				if(!c.estaAbierta()) {
					seleccionarCasilla(c.getFila(),c.getColumna()); 
				}
			}
		}
		else {
			marcarCasilla(fila, col);
		}
		if(partidaGanada()) {
			 eventoPartidaGanada.accept(getCasillasConMina());
		}
	}
	public void marcarCasilla(int fila, int col) {
		if(!casillas[fila][col].estaAbierta()) {
			casillas[fila][col].setAbierta(true);
			numCasillasAbiertas++;
		}
	}
	public ArrayList<Casilla> getMinasAleatorias() {
		Random rand = new Random();
		ArrayList<Casilla> casillasConMina = getCasillasConMina();
		ArrayList<Casilla> minasRandom = new ArrayList<Casilla>();
		
		int cant  = rand.nextInt(2)+1;
		while(minasRandom.size()!=cant) {
			int pos = rand.nextInt(casillasConMina.size());
			if(!minasRandom.contains(casillasConMina.get(pos)) && !casillasConMina.get(pos).esMinaDescubierta()) {
				casillasConMina.get(pos).setMinaDescubierta(true);
				minasRandom.add(casillasConMina.get(pos));
			}
		}
		return minasRandom;
	}
	public void girarTablero() {
		Casilla [][] casillasCongiro = new Casilla [casillas.length][casillas[0].length];
		
		for(int i=0;i<casillas.length;i++) {
			for(int j=0;j<casillas[0].length;j++) {
				casillas[casillas.length-1-j][i].setFila(i);
				casillas[casillas.length-1-j][i].setColumna(j);
				casillasCongiro[i][j] = casillas[casillas.length-1-j][i];
			}
		}
		casillas = casillasCongiro;
		System.out.println("\n GIRO DE TABLERO DERECHA:");
		printTablero();
		System.out.println("/////");
		printPistas();
	}
	
	public void moverMinasEnTablero() {
		Random rand = new Random();
		for(Casilla[]fila :casillas) {
			for(Casilla c: fila) {
				c.setNumMinasAlrededor(0);;
			}
		}
		ArrayList<Casilla> casillasConMina = getCasillasConMina();
		//ArrayList<Casilla> casillasAbiertas= getCasillasAbiertas();
		ArrayList<Casilla> casillasSinAbrir = getCasillasSinAbrir();
		int pos,fila,col;
		for(Casilla mina: casillasConMina) {
			do {
				pos = rand.nextInt(casillasSinAbrir.size());
			}while((casillasSinAbrir.get(pos).esMina()) || (casillasSinAbrir.get(pos).esComodin()));
			fila = casillasSinAbrir.get(pos).getFila();
			col = casillasSinAbrir.get(pos).getColumna();
			
			intercambiar(mina,fila,col);	
		}	
		actualizarMinasAlrededor();
		System.out.println("\n\nMINAS EN OTRAS CASILLAS:");
		printTablero();
		System.out.println("/////");
		printPistas();
		
	}
	public ArrayList<Casilla>getCasillasAbiertas(){
		ArrayList<Casilla> casillasAbiertas = new ArrayList<Casilla>();
		for(int i=0;i<casillas.length;i++) {
			for(int j=0;j<casillas[i].length;j++) {
				if(casillas[i][j].estaAbierta()) {
					casillasAbiertas.add(casillas[i][j]);
				}
			}
		}
		return casillasAbiertas;
	}
	public ArrayList<Casilla>getCasillasSinAbrir(){
		ArrayList<Casilla> casillasSinAbrir = new ArrayList<Casilla>();
		for(int i=0;i<casillas.length;i++) {
			for(int j=0;j<casillas[i].length;j++) {
				if(!casillas[i][j].estaAbierta()) {
					casillasSinAbrir.add(casillas[i][j]);
				}
			}
		}
		return casillasSinAbrir;
	}
	public void intercambiar(Casilla mina, int f, int c) {
		Casilla aux = casillas[f][c];
		casillas[f][c] = mina;
		casillas[mina.getFila()][mina.getColumna()]=aux;
		
		aux.setFila(mina.getFila());
		aux.setColumna(mina.getColumna());
		mina.setFila(f);
		mina.setColumna(c);
	}

	public boolean partidaGanada() {
		return numCasillasAbiertas>=numFilas*numColumnas-numMinas;
	}
	public void setEventoPartidaPerdida(Consumer<ArrayList<Casilla>> evento) {
		this.eventoPartidaPerdida = evento;
	}
	public void setEventoCasillaAbierta(Consumer<Casilla> evento) {
		this.eventocasillaAbierta = evento;
	}
	public void setEventoCasillaEspecial(Consumer<Casilla> evento) {
		this.eventocasillaEspecial = evento;
	}
	public void setEventoPartidaGanada(Consumer<ArrayList<Casilla>> evento) {
		this.eventoPartidaGanada = evento;
	}
	public void setEventoVerMinasRandom(Consumer<ArrayList<Casilla>> evento) {
		this.eventoVerMinasRandom = evento;
	}
	public void setEventoGirarTablero (Consumer<Casilla [][]>evento) {
		this.eventoGirarTablero = evento;
	}
	public void setEventoMoverMinas(Consumer<Casilla [][]>evento) {
		this.eventoMoverMinas = evento;
	}
	public Consumer<Casilla> getEventoCasillaAbierta() {
		return this.eventocasillaAbierta;
	}
	public Consumer<ArrayList<Casilla>> getEventoVerMinasRandom() {
		return this.eventoVerMinasRandom;
	}
	public Casilla [][] getCasillas(){
		return casillas;
	}
	
	public void printTablero() {
		for(Casilla [] fila: casillas) {
			for(Casilla c: fila) {
				if(c.esMina() && !c.esMinaDescubierta()) {
					System.out.print("X ");
				}
				else if(c.esMinaDescubierta()) {
					System.out.print("R ");
				}
				else if(c.esComodin()) {
					System.out.print("E ");
				}
				else {
					System.out.print("O ");
				}
			}
			System.out.println();
		}
	}	
	public void printPistas() {
		for(Casilla [] fila: casillas) {
			for(Casilla c: fila) {
				System.out.print(c.getNumMinasAlrededor()+" ");
			}
			System.out.println();
		}
	}	
	public static void main(String[] args) {
		TableroBuscaminas tablero = new TableroBuscaminas(10, 10, 17);
		tablero.printTablero();
		System.out.println("/////");
		tablero.printPistas();
	}
	
	

}