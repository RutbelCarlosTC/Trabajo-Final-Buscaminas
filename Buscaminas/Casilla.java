package buscaminas;

public class Casilla {
	private int fila,columna;
	private boolean esMina;
	private int numMinasAlrededor;
	private boolean estaAbierta;
	private boolean esComodin;
	private int eventoEspecial;
	private boolean minaDescubierta;

	public Casilla(int f, int c) {
		setFila(f);
		setColumna(c);
		setMina(false);
		setAbierta(false);
		setComodin(false);
		setMinaDescubierta(false);
		
	}
	public int getFila() {
		return fila;
	}
	public int getColumna() {
		return columna;
	}
	public int getEventoEspecial() {
		return eventoEspecial;
	}
	public void setFila(int f) {
		fila = f;
	}
	public void setColumna(int c) {
		columna = c;
	}
	public void setEventoEspecial(int e) {
		eventoEspecial = e;
	}
	public boolean esMina() {
		return esMina;
	}
	public boolean estaAbierta() {
		return estaAbierta;
	}
	public boolean esComodin() {
		return esComodin;
	}
	public void setAbierta(boolean val) {
		estaAbierta =val;
	}
	public void setMina(boolean val) {
		esMina =val;
	}
	public void setComodin(boolean val) {
		esComodin=val;
	}
	public void incremenetarMinasAlrededor() {
		numMinasAlrededor = getNumMinasAlrededor() + 1;
	}
	public int getNumMinasAlrededor() {
		return numMinasAlrededor;
	}
	public void setNumMinasAlrededor(int i) {
		numMinasAlrededor =i;
		
	}
	public boolean esMinaDescubierta() {
		return minaDescubierta;
	}
	public void setMinaDescubierta(boolean minaDescubierta) {
		this.minaDescubierta = minaDescubierta;
	}
	
	
}
