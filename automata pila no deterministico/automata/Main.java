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

  // NFAPila pa= parser("prueba4.txt");
   //System.out.println("Con la cadena 'aabb': "+pa.accepts("aaabbb"));


   NFAPila p= parser("gramatica2.txt");
   System.out.println("Con la cadena '011001': "+p.accepts("011001"));//acepta con gramatica 2
   System.out.println("Con la cadena '011010001': "+p.accepts("011010001"));//no acepta con gramatica 2
   NFAPila pf = p.stackEmptyToFinalState();
   System.out.println("Con la cadena '011010001': "+pf.accepts("011010001"));//no acepta con gramatica 2



  }
  public static NFAPila parser(String filePath)throws IOException{
    File fichero = new File(filePath);

    Scanner s = null;
    NFAPila A=null;
    ArrayList<String> array=new ArrayList<String>();
  		try {
  			s = new Scanner(fichero);

  			// Leemos linea a linea el fichero
  			while (s.hasNextLine()) {
  				String linea = s.nextLine(); 	// Guardamos la linea en un String
          array.add(linea);

  			}
        if((array.get(0)).matches("digraph\\{")){
          A=interpretDot(array);
          System.out.println("... entro por  DOT ...");

          System.out.println(A.to_dot());
        }else{
          System.out.println("... entro por Gramatica ...");

          A=interpretarGramatica(array);
        }

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





  public static NFAPila interpretDot(ArrayList<String> data){
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

    NFAPila AFP=new NFAPila(newStates,newAlphabet,newStackAlphabet,newTransitions,AP.Initial,newInitial,newFinal_states);
    return AFP;
  }

  public static NFAPila interpretarGramatica(ArrayList<String> data){
    Set<State> newStates= new HashSet<State>();
    Set<Character> newAlphabet=new HashSet<Character>();
    Set<Character> newStackAlphabet=new HashSet<Character>();
    Set<Quintuple<State, Character,Character,String, State>> newTransitions=new HashSet<Quintuple<State, Character,Character,String, State>>();
    Set<State> newFinal_states=new HashSet<State>();
    State newInitial=null;
    boolean match;
    Set<Character> terminales=new HashSet<Character>();
    Set<Character> noTerminales=new HashSet<Character>();
    LinkedList<String> right=new LinkedList<String>();
    Character newInitialCharacter=null;
    State q0=new State("q0");
    for(int i=0;i<data.size();i++){
      match=false;
      String aux=data.get(i).replaceAll("\\s", "");


      if(aux.matches(".+->.+")){

        String[]  pares=null;
        pares=aux.split("->");
        if(i==0){
             newInitialCharacter=(pares[0]).charAt(0);
        }
        Character a=(pares[0]).charAt(0);
        if(!isContained(noTerminales,a)){
          noTerminales.add(a);
          newStackAlphabet.add(a);
        }
        pares=(pares[1]).split("\\|");
        int j=0;
        while(j<=pares.length-1){
          right.add(pares[j]);
          newTransitions.add(new Quintuple<State, Character,Character,String, State>(q0,AP.Lambda,a,pares[j],q0));

          j++;

        }
      }
      else
      {
        System.out.println("LA GRAMATICA ESTA MAL ESCRITA.");
        return null;
      }
    }
    while(!right.isEmpty()){
        String aux=right.remove();
        int l=0;
        while(l<(aux.length())){
          if(!isContained(noTerminales,aux.charAt(l))){
            terminales.add(aux.charAt(l));
            newAlphabet.add(aux.charAt(l));
            newStackAlphabet.add(aux.charAt(l));
          }
          l++;
        }
    }
    for(Character c:terminales){
      newTransitions.add(new Quintuple<State, Character,Character,String, State>(q0,c,c,""+AP.Lambda,q0) );
    }
    newInitial=new State("newInitial");
    newStates.add(newInitial);
    newStates.add(q0);
    newTransitions.add(new Quintuple<State, Character,Character,String, State>(newInitial,AP.Lambda,newInitialCharacter,""+newInitialCharacter,q0) );
    NFAPila Ap=new NFAPila(newStates,newAlphabet,newStackAlphabet,newTransitions,newInitialCharacter,newInitial,newFinal_states);
    Ap.toString();
    return Ap;
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

  public static boolean isContained(Set<Character> characters, Character element){
    if(characters.isEmpty()){
      return false;
    }
    for(Character s:characters){
      if((s).equals(element)){

        return true;
      }

    }
    return false;
  }

}
