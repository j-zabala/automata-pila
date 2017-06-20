package automata;


import java.io.File;
import java.util.Scanner;
import java.io.FileReader;
import java.io.IOException;

import java.util.Set;
import java.util.Stack;
import java.util.*;
import utils.Quintuple;

public class Main{
  public static void main(String[] args) throws IOException{
    DFAPila Pa=parser("hola.txt");
    System.out.println("Con la cadena 'aaaaab':"+Pa.accepts("aaaaab"));
    System.out.println("Con la cadena 'aaaabb':"+Pa.accepts("aaaabb"));
  //  System.out.println(Pa.to_dot());


/*     DFAPila pf = Pa.finalStateToEmptyStack();//  ESTO NO SE PUEDE REALIZAR POR QUE AL APLICAR EL ALGORITMO DEJA DE SER DETERMINISTA Y NO PASA EL rep_ok.
    // System.out.println(pf.to_dot());
     System.out.println("Con la cadena 'aaaaab':"+pf.accepts("aaaaab"));
     System.out.println("Con la cadena 'aaaabb':"+pf.accepts("aaaabb")); */

     //EL ALGORITMO DE PASO DE PILA VACIA A ESTADO FINAL NO CUMPLE CON EL rep_ok POR EL MISMO MOTIVO DEL ALGORITMO DE ESTADO FINAL A PILA VACIA.

  }
  public static DFAPila parser(String filePath)throws IOException{
    File fichero = new File(filePath);
    Scanner s = null;

    DFAPila A=null;
    ArrayList<String> array=new ArrayList<String>();
  		try {


  			s = new Scanner(fichero);

  			// Leemos linea a linea el fichero
  			while (s.hasNextLine()) {
  				String linea = s.nextLine(); 	// Guardamos la linea en un String
          array.add(linea);

  			}
         A=interpretDot(array);



  		} catch (Exception ex) {
  			System.out.println("Mensaje: " + ex.getMessage());
  		} finally {
  			// Cerramos el fichero tanto si la lectura ha sido correcta o no
  			try {
  				if (s != null)
  					s.close();
  			} catch (Exception ex2) {
  				System.out.println("Mensaje 2: " + ex2.getMessage());
  			}

  		}
      return A;
  	}



  public static DFAPila interpretDot(ArrayList<String> data){
    Set<State> newStates= new HashSet<State>();
    Set<Character> newAlphabet=new HashSet<Character>();
    Set<Character> newStackAlphabet=new HashSet<Character>();
    Set<Quintuple<State, Character,Character,String, State>> newTransitions=new HashSet<Quintuple<State, Character,Character,String, State>>();
    Character newStackInitial;
    Set<State> newFinal_states=new HashSet<State>();
    State newInitial=null;
    State newFinal;
    boolean match;
    for(int i=0;i<data.size();i++){
      match=false;

      String aux=data.get(i).replaceAll("\\s", "");
      if(aux.matches("digraph\\{")){
        match=true;
        System.out.println("entro al digraph{");
      }
      if(aux.matches("inic\\[shape\\=point\\]\\;")){
        match=true;
        System.out.println("entro al inic[shape=point];");

      }
      if(aux.matches("inic->.+\\;")){
        match=true;
        System.out.println("entro al: inic->.+\\;");

        String[] t=  aux.split("->");

        String name=(t[1]).replaceAll("\\;","");
        newInitial=new State(name);
        if(!isContained(newStates,newInitial)){newStates.add(newInitial);}

      }


      if (aux.matches(".+->.+\\[label\\=\\\".+\\/.+\\/.+\\\"\\]\\;")){
        match=true;
        System.out.println("entro por.+->.+\\[label\\="+'"'+".+\\/.+\\/.+"+'"'+"\\]\\;");

        String[] pares=aux.split("->");
        String[] ax=pares[1].split("\\[");

        State a=new State(pares[0]);
        State b=new State(ax[0]);
        if(!isContained(newStates,a)){newStates.add(a);}
        if(!isContained(newStates,b)){newStates.add(b);}
        pares=ax[1].split("\\\"");
        pares=pares[1].split("\\/");
        newAlphabet.add(pares[0].charAt(0));
        newStackAlphabet.add(pares[1].charAt(0));
        Quintuple<State, Character,Character,String, State> transition = new Quintuple<State, Character,Character,String, State> (a,pares[0].charAt(0),pares[1].charAt(0),pares[2],b);
        newTransitions.add(transition);
      }
      if(aux.matches(".+\\[shape\\=doublecircle\\]\\;")){
        match=true;
        System.out.println("entro al: "+".+\\[shape\\=doublecircle\\]\\;");
        String[] t=  aux.split("\\[");
        newFinal=new State(t[0]);
        if(!isContained(newFinal_states,newFinal)){newFinal_states.add(newFinal);}
      }
      if(aux.matches("\\}")){
        match=true;
        System.out.println("\\}");
      }
      if(!match){
        System.out.println("EL AUTOMATA INGRESADO NO ES CORRECTO.");
        return null;
      }


    }


    DFAPila AFP=new DFAPila(newStates,newAlphabet,newStackAlphabet,newTransitions,AP.Initial,newInitial,newFinal_states);
    return AFP;
  }

  public static boolean isContained(Set<State> states, State element){
    if(states.isEmpty()){
      return false;
    }

    for(State s:states){

      if((s.name()).equals(element.name())){


        return true;
      }

    }
    return false;
  }

}
