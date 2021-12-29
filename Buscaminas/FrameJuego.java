package buscaminas;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class FrameJuego extends JFrame {
	
	private int numFilas,numColumnas,numMinas;
	private int nivel=1;
	
	private JButton[][] botones;
	private JPanel cabecera,contenido;
	private static final int ANCHO = 600;
	private static final int ALTO = 700;
	private TableroBuscaminas tableroBuscaminas;
	private JPanel TableroJuego;
	private JButton ComodinActivado;
	private JLabel nombreComodin ;
	private JTextArea descripcionComodin; 
	
	private JMenuItem n1,n2,n3;
	private JMenuItem nuevoJuego;
	
	public FrameJuego(){
		setTitle("tablero buscaminas");
		setSize(ANCHO,ALTO);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		initComponentes();
		nuevoJuego();
	}
	
	public void nuevoJuego() {
		switch(nivel) {
		case 1:
			numFilas = 5;
			numColumnas = 5;
			numMinas =8;
			break;
		case 2:
			numFilas = 10;
			numColumnas = 10;
			numMinas =17;
			break;
		case 3:
			numFilas = 15;
			numColumnas = 15;
			numMinas =20;
		}
		borrarComponentes();
		agregarComponenentes();
		crearTableroBuscaminas();
		setVisible(true);
	}
	public void crearTableroBuscaminas() {
		tableroBuscaminas = new TableroBuscaminas(numFilas,numColumnas,numMinas);
		
		tableroBuscaminas.setEventoPartidaPerdida(new Consumer<ArrayList<Casilla>>() {
			
			public void accept(ArrayList<Casilla> t) {
				for(Casilla[] fila: tableroBuscaminas.getCasillas()) {
					for(Casilla c: fila) {
						tableroBuscaminas.getEventoCasillaAbierta().accept(c);
						botones[c.getFila()][c.getColumna()].setEnabled(false);
					}
				}
				for(Casilla mina: t) {
					botones[mina.getFila()][mina.getColumna()].setText("M");
				}
				JOptionPane.showInternalMessageDialog(null, "PERDISTE");
				
			}
		});
		tableroBuscaminas.printTablero();
		System.out.println();
		tableroBuscaminas.printPistas();
		tableroBuscaminas.setEventoVerMinasRandom(new Consumer<ArrayList<Casilla>>() {
			@Override
			public void accept(ArrayList<Casilla> t) {
				for(Casilla mina: t) {
					botones[mina.getFila()][mina.getColumna()].setText("MD");
					botones[mina.getFila()][mina.getColumna()].setEnabled(false);;
				}
			}
		});
		tableroBuscaminas.setEventoGirarTablero(new Consumer<Casilla[][]>() {
			@Override
			public void accept(Casilla[][] tablero) {
				ArrayList<Casilla> minasDescubiertas = new ArrayList<Casilla>();
				for(Casilla[] fila: tablero) {
					for(Casilla c: fila) {
						if(c.estaAbierta()) {
							tableroBuscaminas.getEventoCasillaAbierta().accept(c);
						}
						else {
							botones[c.getFila()][c.getColumna()].setEnabled(true);
							botones[c.getFila()][c.getColumna()].setText("");
						}
						if(c.esMinaDescubierta()) {
							minasDescubiertas.add(c);
						}
							
					}
				}
				if(minasDescubiertas.size()>0) {
					tableroBuscaminas.getEventoVerMinasRandom().accept(minasDescubiertas);;
				}
				
			}
		});
		tableroBuscaminas.setEventoMoverMinas(new Consumer<Casilla[][]>() {

			@Override
			public void accept(Casilla[][] tablero) {
				ArrayList<Casilla> minasDescubiertas = new ArrayList<Casilla>();
				for(Casilla[] fila: tablero) {
					for(Casilla c: fila) {
						if(c.estaAbierta()) {
							tableroBuscaminas.getEventoCasillaAbierta().accept(c);
						}
						else {
							botones[c.getFila()][c.getColumna()].setEnabled(true);
							botones[c.getFila()][c.getColumna()].setText("");
						}
						if(c.esMinaDescubierta()) {
							minasDescubiertas.add(c);
						}
					}
				}
				if(minasDescubiertas.size()>0) {
					tableroBuscaminas.getEventoVerMinasRandom().accept(minasDescubiertas);;
				}
				
			}
		});

		tableroBuscaminas.setEventoCasillaAbierta(new Consumer<Casilla>() {
			@Override
			public void accept(Casilla t) {
				botones[t.getFila()][t.getColumna()].setEnabled(false);
				String minasAlrededor = String.valueOf(t.getNumMinasAlrededor());
				if(minasAlrededor.equals("0")) {
					minasAlrededor="";
				}
				botones[t.getFila()][t.getColumna()].setText(minasAlrededor) ;;
			}
		});
		
		tableroBuscaminas.setEventoCasillaEspecial(new Consumer<Casilla>() {
			@Override
			public void accept(Casilla t) {
				switch(t.getEventoEspecial()) {
				case 1:
					ComodinActivado.setBackground(Color.cyan);
					nombreComodin.setText("Ver mina");
					descripcionComodin.setText("Ahora puedes ver 1 0 2 minas en el tablero");
					break;
				case 2: 
					ComodinActivado.setBackground(Color.YELLOW);
					nombreComodin.setText("Girar tablero");
					descripcionComodin.setText("El tablero ha girado 90 grados");
					break;
				case 3:
					ComodinActivado.setBackground(Color.GREEN);
					nombreComodin.setText("Mover minas");
					descripcionComodin.setText("Las minas han cambiado de posicion");
					
				}
			}
		});
		
		tableroBuscaminas.setEventoPartidaGanada(new Consumer<ArrayList<Casilla>>() {
			@Override
			public void accept(ArrayList<Casilla> t) {
				for(Casilla[] fila: tableroBuscaminas.getCasillas()) {
					for(Casilla c: fila) {
						tableroBuscaminas.getEventoCasillaAbierta().accept(c);
						botones[c.getFila()][c.getColumna()].setEnabled(false);
					}
				}
				for(Casilla mina: t) {
					botones[mina.getFila()][mina.getColumna()].setText(":)");
				}
				JOptionPane.showInternalMessageDialog(null, "FELICIDADES GANASTE");
				
			}
		});
	}
	public void cargarBotones() {
		
		Listener listener= new Listener();
		botones = new JButton[numFilas][numColumnas];
		for(int i=0;i<botones.length;i++) {
			for(int j=0;j<botones[0].length;j++) {
				botones[i][j] = new JButton();
				botones[i][j].setName(i+","+j);
				//botones[i][j].setBorder(null);
				TableroJuego.add(botones[i][j]);
				botones[i][j].addActionListener(listener);
			}
		}
		
	}
	
	public void initComponentes() {
		EventosJuego event = new EventosJuego();
		JMenuBar barra;
		JMenu juego,niveles;
		cabecera =  new JPanel(new GridLayout(4,2)); 
		contenido= new JPanel(new BorderLayout());
				
		barra = new JMenuBar();
		setJMenuBar(barra);
		juego = new JMenu("Juego");
		niveles = new JMenu("Nivel");
		barra.add(juego);
		barra.add(niveles);
		
		n1 = new JMenuItem("facil");
		n2 = new JMenuItem("medio");
		n3 = new JMenuItem("dificil");
		
		niveles.add(n1);
		niveles.add(n2);
		niveles.add(n3);
		
		n1.addActionListener(event);
		n2.addActionListener(event);
		n3.addActionListener(event);
		//
		nuevoJuego = new JMenuItem("nuevo juego");
		nuevoJuego.addActionListener(event);
		juego.add(nuevoJuego);
		
		String saltos = "";
		String espacios = "";
		
		for(int i=0;i<10;i++) {
			saltos+="\n";
			espacios+=" ";
		}
		contenido.add(new JLabel(saltos),BorderLayout.NORTH);
		contenido.add(new JLabel(saltos),BorderLayout.SOUTH);
		contenido.add(new JLabel(espacios),BorderLayout.WEST);
		contenido.add(new JLabel(espacios),BorderLayout.EAST);
	}
	public void agregarComponenentes() {
		
		TableroJuego = new JPanel(new GridLayout(this.numFilas,this.numColumnas));
		cargarBotones();
		String saltos = "";
		String espacios = "";
		
		for(int i=0;i<10;i++) {
			saltos+="\n";
			espacios+=" ";
		}
		contenido.add(new JLabel(saltos),BorderLayout.NORTH);
		contenido.add(new JLabel(saltos),BorderLayout.SOUTH);
		contenido.add(new JLabel(espacios),BorderLayout.WEST);
		contenido.add(new JLabel(espacios),BorderLayout.EAST);
		contenido.add(TableroJuego,BorderLayout.CENTER);
		
		//
		JLabel guia = new JLabel("GUIA"),eventoActivado = new JLabel("CASILLA ESPECIAL") ;//
		
		String[] textoLabelGuia = {"Mina","mina Descubierta","numero de minas al rededor"};
		JPanel[] listaGuia = new JPanel[textoLabelGuia.length];
		JButton[] botonesGuia = new JButton[textoLabelGuia.length];
		JLabel[] labelsGuia = new JLabel[textoLabelGuia.length];
		
		for(int i=0;i<listaGuia.length;i++) {
			listaGuia[i] = new JPanel(new FlowLayout());
			botonesGuia[i] = new JButton();
			botonesGuia[i].setSize(2, 2);
			labelsGuia[i] = new JLabel(textoLabelGuia[i]);
			
			listaGuia[i].add(botonesGuia[i]);
			listaGuia[i].add(labelsGuia[i]);			
		}
		
		JPanel evento = new JPanel(new FlowLayout()); //
		ComodinActivado = new JButton();
		nombreComodin = new JLabel("-----");
		evento.add(ComodinActivado);
		evento.add(nombreComodin);
		
		JPanel d = new JPanel(new FlowLayout());
		descripcionComodin = new JTextArea("------------"); // descripcion con listener
		descripcionComodin.setBackground(null);
		d.add(descripcionComodin);
		
		cabecera.add(guia);
		cabecera.add(eventoActivado);
		cabecera.add(listaGuia[0]);
		cabecera.add(evento);
		cabecera.add(listaGuia[1]);
		cabecera.add(new JLabel("Descripcion"));
		cabecera.add(listaGuia[2]);
		cabecera.add(d);
	
		add(cabecera,BorderLayout.NORTH);
		contenido.setSize(10, 10);
		add(contenido,BorderLayout.CENTER);
	}
	public void borrarComponentes() {
		if (botones!=null){
            for (int i = 0; i < botones.length; i++) {
                for (int j = 0; j < botones[i].length; j++) {
                    if (botones[i][j]!=null){
                    	TableroJuego.remove(botones[i][j]);
                    }
                }
            }
            cabecera.removeAll();
            contenido.removeAll();
        }
	}
	private class Listener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton)e.getSource();
			String[] coordenada = btn.getName().split(",");
			int posFila = Integer.parseInt(coordenada[0]);
			int posCol= Integer.parseInt(coordenada[1]);
			//JOptionPane.showMessageDialog(rootPane, posFila+","+posCol);
			tableroBuscaminas.seleccionarCasilla(posFila, posCol);			
		}
		
	}
	private class EventosJuego implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==nuevoJuego) {
				nuevoJuego();
			}
			else {
				switch(e.getActionCommand()){
				case "n1":
					nivel =1;
					break;
				case "n2":
					nivel =2;
					break;
				case "n3":
					nivel =3;
				}
			}
			
		}
		
	}
	public static void main(String[] args) {
		new FrameJuego();

	}

}
